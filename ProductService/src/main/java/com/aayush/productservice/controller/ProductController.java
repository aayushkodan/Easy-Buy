package com.aayush.productservice.controller;

import com.aayush.productservice.dto.request.AddReviewRequest;
import com.aayush.productservice.dto.request.CreateProductRequest;
import com.aayush.productservice.dto.request.UpdateProductRequest;
import com.aayush.productservice.dto.response.PagedResponse;
import com.aayush.productservice.dto.response.ProductResponse;
import com.aayush.productservice.dto.response.ReviewResponse;
import com.aayush.productservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "12") @Min(value = 1, message = "size must be greater than 0") @Max(value = 100, message = "size must be at most 100") int size
    ) {
        log.warn("Page: {}, Size: {}", page, size);
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID productId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.getProductById(productId));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "12") @Min(value = 1, message = "size must be greater than 0") @Max(value = 100, message = "size must be at most 100") int size
    ) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId, page, size));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID productId, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<ProductResponse> addCategoryToProduct(@PathVariable UUID productId, @PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.addCategoryToProduct(productId, categoryId));
    }

    @DeleteMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<ProductResponse> removeCategoryFromProduct(@PathVariable UUID productId, @PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.removeCategoryFromProduct(productId, categoryId));
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> addReviewToProduct(@PathVariable UUID productId, @Valid @RequestBody AddReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addReview(productId, request));
    }

    @PostMapping(value = "/{productId}/images", consumes = "multipart/form-data")
    public ResponseEntity<ProductResponse> addProductImages(
            @PathVariable UUID productId,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        return ResponseEntity.ok(productService.addProductImages(productId, files));
    }

    @GetMapping("/{productId}/images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductImages(productId));
    }
}
