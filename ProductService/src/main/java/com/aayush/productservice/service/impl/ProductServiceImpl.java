package com.aayush.productservice.service.impl;

import com.aayush.productservice.dto.request.AddReviewRequest;
import com.aayush.productservice.dto.request.CreateProductRequest;
import com.aayush.productservice.dto.request.UpdateProductRequest;
import com.aayush.productservice.dto.response.PagedResponse;
import com.aayush.productservice.dto.response.ProductResponse;
import com.aayush.productservice.dto.response.ReviewResponse;
import com.aayush.productservice.entity.Category;
import com.aayush.productservice.entity.Product;
import com.aayush.productservice.exception.EasyBuyException;
import com.aayush.productservice.exception.ErrorCode;
import com.aayush.productservice.mapper.ProductMapper;
import com.aayush.productservice.mapper.ReviewMapper;
import com.aayush.productservice.repository.CategoryRepository;
import com.aayush.productservice.repository.ProductRepository;
import com.aayush.productservice.service.ImageStorageService;
import com.aayush.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageStorageService imageStorageService;
    private final ProductMapper productMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public PagedResponse<ProductResponse> getAllProducts(int page, int size) {

        log.debug(
                "Fetching all products - page: {}, size: {}",
                page,
                size
        );

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productRepository.findAll(pageable)
                .map(this::buildProductResponse);
        return toPageResponse(products);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(UUID productId) {

        log.debug("Fetching product with id: {}", productId);

        return buildProductResponse(findProduct(productId));
    }

    @Override
    public PagedResponse<ProductResponse> getProductsByCategoryId(Long categoryId, int page, int size) {

        log.debug(
                "Fetching products for category {}",
                categoryId
        );

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productRepository.findByCategoryId(categoryId, pageable)
                .map(this::buildProductResponse);
        return toPageResponse(products);
    }

    @Transactional
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        log.info("Creating product with title: {}", request.title());

        Product product = productMapper.createToEntity(request);

        product.setCategory(categoryRepository.findById(request.categoryId()).orElseThrow(() -> new EasyBuyException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found: " + request.categoryId())));

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());

        return buildProductResponse(savedProduct);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {

        log.info("Updating product with id: {}", productId);

        Product product = findProduct(productId);

        productMapper.updateProduct(request, product);

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with id: {}", productId);

        return buildProductResponse(updatedProduct);
    }

    @Transactional
    @Override
    public void deleteProduct(UUID productId) {

        log.warn("Deleting product with id: {}", productId);

        Product product = findProduct(productId);

        productRepository.delete(product);

        log.info("Product deleted successfully with id: {}", productId);
    }

    @Transactional
    @Override
    public ProductResponse addCategoryToProduct(UUID productId, Long categoryId) {

        log.info(
                "Assigning category {} to product {}",
                categoryId,
                productId
        );

        Product product = findProduct(productId);

        Category category = findCategory(categoryId);

        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        log.info(
                "Category {} assigned successfully to product {}",
                categoryId,
                productId
        );

        return buildProductResponse(updatedProduct);
    }

    @Transactional
    @Override
    public ProductResponse removeCategoryFromProduct(
            UUID productId,
            Long categoryId
    ) {

        log.info(
                "Removing category {} from product {}",
                categoryId,
                productId
        );

        Product product = findProduct(productId);

        if (product.getCategory() == null ||
                !product.getCategory().getId().equals(categoryId)) {

            log.warn(
                    "Category {} not associated with product {}",
                    categoryId,
                    productId
            );

            throw new EasyBuyException(
                    ErrorCode.CATEGORY_NOT_FOUND,
                    "Category not found: " + categoryId
            );
        }

        product.setCategory(null);

        Product updatedProduct = productRepository.save(product);

        log.info(
                "Category removed successfully from product {}",
                productId
        );

        return buildProductResponse(updatedProduct);
    }

    @Transactional
    @Override
    public List<ReviewResponse> addReview(
            UUID productId,
            AddReviewRequest request
    ) {

        log.info(
                "Adding review to product {} with rating {}",
                productId,
                request.rating()
        );

        Product product = findProduct(productId);

        product.addReview(
                reviewMapper.addReviewToEntity(request)
        );

        Product updatedProduct = productRepository.save(product);

        log.info(
                "Review added successfully to product {}",
                productId
        );

        return updatedProduct.getReviews()
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public ProductResponse addProductImages(
            UUID productId,
            List<MultipartFile> files
    ) throws IOException {

        log.info(
                "Uploading {} images for product {}",
                files.size(),
                productId
        );

        Product product = findProduct(productId);

        List<String> objectKeys = uploadImages(files);

        if (product.getProductImages() == null) {
            product.setProductImages(new ArrayList<>());
        }

        product.getProductImages().addAll(objectKeys);

        Product updatedProduct = productRepository.save(product);

        log.info(
                "Successfully uploaded {} images for product {}",
                objectKeys.size(),
                productId
        );

        return buildProductResponse(updatedProduct);
    }

    @Override
    public List<String> getProductImages(UUID productId) {

        log.debug(
                "Fetching product images for product {}",
                productId
        );

        Product product = findProduct(productId);

        return product.getProductImages() == null
                ? List.of()
                : product.getProductImages()
                .stream()
                .map(imageStorageService::generatePresignedUrl)
                .toList();
    }

    private PagedResponse<ProductResponse> toPageResponse(Page<ProductResponse> page) {
        return PagedResponse.<ProductResponse>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();
    }

    private ProductResponse buildProductResponse(Product product) {

        ProductResponse response =
                productMapper.toResponse(product);

        List<String> imageUrls =
                product.getProductImages() == null
                        ? List.of()
                        : product.getProductImages()
                        .stream()
                        .map(imageStorageService::generatePresignedUrl)
                        .toList();

        return new ProductResponse(
                response.id(),
                response.title(),
                response.shortDesc(),
                response.longDesc(),
                response.price(),
                response.discount(),
                response.live(),
                imageUrls,
                response.category(),
                response.createdAt(),
                response.updatedAt()
        );
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EasyBuyException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + productId));
    }
    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EasyBuyException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found: " + categoryId));
    }
    private List<String> uploadImages(List<MultipartFile> files) throws IOException {
        if(files == null || files.isEmpty()){
            throw new EasyBuyException(ErrorCode.INVALID_REQUEST, "At least one product image is required");
        }
        List<String> urls = new ArrayList<>();
        for(MultipartFile file : files){
            urls.add(imageStorageService.upload(file));
        }
        return urls;
    }
}
