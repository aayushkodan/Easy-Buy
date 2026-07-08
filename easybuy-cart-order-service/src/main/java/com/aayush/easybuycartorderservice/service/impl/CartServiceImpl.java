package com.aayush.easybuycartorderservice.service.impl;

import com.aayush.easybuycartorderservice.dto.request.AddCartItemRequest;
import com.aayush.easybuycartorderservice.dto.request.UpdateCartItemRequest;
import com.aayush.easybuycartorderservice.dto.response.CartResponse;
import com.aayush.easybuycartorderservice.dto.response.ProductSummaryResponse;
import com.aayush.easybuycartorderservice.entity.Cart;
import com.aayush.easybuycartorderservice.entity.CartItem;
import com.aayush.easybuycartorderservice.entity.CartStatus;
import com.aayush.easybuycartorderservice.exception.EasybuyException;
import com.aayush.easybuycartorderservice.exception.ErrorCode;
import com.aayush.easybuycartorderservice.external.ProductClient;
import com.aayush.easybuycartorderservice.mapper.CartMapper;
import com.aayush.easybuycartorderservice.repository.CartRepository;
import com.aayush.easybuycartorderservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductClient productClient;

    @Override
    public CartResponse getCart(UUID userId) {

        log.debug("Fetching active cart for userId={}", userId);

        Cart cart = getOrCreateActiveCart(userId);

        log.debug(
                "Cart {} contains {} items",
                cart.getId(),
                cart.getItems().size()
        );

        return cartMapper.toResponse(cart);
    }

    @Override
    public CartResponse addItem(UUID userId, AddCartItemRequest request) {

        log.info(
                "Adding product {} (quantity={}) to cart of user {}",
                request.productId(),
                request.quantity(),
                userId
        );

        Cart cart = getOrCreateActiveCart(userId);
        ProductSummaryResponse product = fetchProduct(request.productId());

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.productId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setProductId(request.productId());
                    newItem.setCart(cart);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        item.setProductTitle(product.title());
        item.setUnitPrice(product.price());
        item.setDiscountPercent(product.discount());
        item.setQuantity(safeQuantity(item.getQuantity())+ request.quantity());

        log.info(
                "Product {} successfully added to cart {}",
                request.productId(),
                cart.getId()
        );

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse updateItem(UUID userId,
                                   UUID productId,
                                   UpdateCartItemRequest request) {

        log.info(
                "Updating quantity of product {} in cart of user {} to {}",
                productId,
                userId,
                request.quantity()
        );

        Cart cart = getOrCreateActiveCart(userId);

        CartItem item = findCartItem(cart, productId);

        item.setQuantity(request.quantity());

        Cart saved = cartRepository.save(cart);

        log.info(
                "Updated product {} in cart {}",
                productId,
                cart.getId()
        );

        return cartMapper.toResponse(saved);
    }

    @Override
    public CartResponse removeItem(UUID userId, UUID productId) {

        log.info(
                "Removing product {} from cart of user {}",
                productId,
                userId
        );

        Cart cart = getOrCreateActiveCart(userId);

        CartItem item = findCartItem(cart, productId);

        cart.getItems().remove(item);

        Cart saved = cartRepository.save(cart);

        log.info(
                "Removed product {} from cart {}",
                productId,
                cart.getId()
        );

        return cartMapper.toResponse(saved);
    }

    @Override
    public void clearCart(UUID userId) {

        log.info("Clearing cart of user {}", userId);

        Cart cart = getOrCreateActiveCart(userId);

        cart.getItems().clear();

        cartRepository.save(cart);

        log.info(
                "Cart {} cleared successfully",
                cart.getId()
        );
    }

    private Cart getOrCreateActiveCart(UUID userId) {

        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {

                    log.info(
                            "No active cart found for user {}. Creating new cart.",
                            userId
                    );

                    Cart cart = new Cart();

                    cart.setUserId(userId);
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setItems(new ArrayList<>());

                    Cart saved = cartRepository.save(cart);

                    log.info(
                            "Created cart {} for user {}",
                            saved.getId(),
                            userId
                    );

                    return saved;
                });
    }

    private ProductSummaryResponse fetchProduct(UUID productId) {

        log.debug(
                "Fetching product {} from Product Service",
                productId
        );

        ProductSummaryResponse product = productClient.getProductById(productId);

        if (product == null) {

            log.warn(
                    "Product {} not found",
                    productId
            );

            throw new EasybuyException(
                    ErrorCode.PRODUCT_NOT_FOUND,
                    "Product not found: " + productId
            );
        }

        if (Boolean.FALSE.equals(product.live())) {

            log.warn(
                    "Product {} is inactive",
                    productId
            );

            throw new EasybuyException(
                    ErrorCode.PRODUCT_INACTIVE,
                    "Product is inactive: " + productId
            );
        }

        return product;
    }

    private CartItem findCartItem(Cart cart, UUID productId) {

        return cart.getItems()
                .stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {

                    log.warn(
                            "Product {} not found in cart {}",
                            productId,
                            cart.getId()
                    );

                    return new EasybuyException(
                            ErrorCode.CART_ITEM_NOT_FOUND,
                            "Product not found in cart: " + productId
                    );
                });
    }

    private int safeQuantity(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }
}
