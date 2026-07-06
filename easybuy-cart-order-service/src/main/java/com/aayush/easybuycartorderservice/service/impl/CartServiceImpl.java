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
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductClient productClient;

    @Override
    public CartResponse getCart(UUID userId) {
        return cartMapper.toResponse(getOrCreateActiveCart(userId));
    }

    @Override
    public CartResponse addItem(UUID userId, AddCartItemRequest request) {

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
        item.setUnitPrice(finalUnitPrice(product.price().doubleValue(), product.discount()));
        item.setDiscountPercent(product.discount());
        item.setQuantity(safeQuantity(item.getQuantity())+ request.quantity());
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse updateItem(UUID userId, UUID productId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateActiveCart(userId);
        CartItem item = findCartItem(cart, productId);
        item.setQuantity(request.quantity());
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse removeItem(UUID userId, UUID productId) {
        Cart cart = getOrCreateActiveCart(userId);
        CartItem item = findCartItem(cart, productId);
        cart.getItems().remove(item);
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public void clearCart(UUID userId) {
        Cart cart = getOrCreateActiveCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateActiveCart(UUID userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setItems(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    private ProductSummaryResponse fetchProduct(UUID productId) {

        ProductSummaryResponse product = productClient.getProductById(productId);

        if (product == null || Boolean.FALSE.equals(product.live())) {
            throw new EasybuyException(
                    ErrorCode.PRODUCT_NOT_FOUND,
                    "Product not found or inactive: " + productId
            );
        }

        return product;
    }

    private CartItem findCartItem(Cart cart, UUID productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EasybuyException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found or inactive: " + productId));
    }

    private BigDecimal finalUnitPrice(Double price, Integer discount) {
        BigDecimal base = BigDecimal.valueOf(price == null ? 0.0 : price);
        BigDecimal discountFactor = BigDecimal.valueOf(100 - defaultZero(discount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return base.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }

    private int defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private int safeQuantity(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }
}
