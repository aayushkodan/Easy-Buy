package com.aayush.productservice.service;

import com.aayush.productservice.dto.request.AddReviewRequest;
import com.aayush.productservice.dto.request.CreateProductRequest;
import com.aayush.productservice.dto.request.UpdateProductRequest;
import com.aayush.productservice.dto.response.PagedResponse;
import com.aayush.productservice.dto.response.ProductResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.aayush.productservice.dto.response.ReviewResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    //all products in paginated format
    PagedResponse<ProductResponse> getAllProducts(int page, int size);

    //product dto by product id
    ProductResponse getProductById(UUID productId);

    //product by category id in paginated way
    PagedResponse<ProductResponse> getProductsByCategoryId(Long categoryId, int page, int size);

    //create new product
    ProductResponse createProduct(CreateProductRequest request);


    //update the product by product id
    ProductResponse updateProduct(UUID productId, UpdateProductRequest request);

    //delete product by id
    void deleteProduct(UUID productId);

    //Add category to product --> product id, category id
    ProductResponse addCategoryToProduct(UUID productId, Long categoryId);

    //Remove the category from product
    ProductResponse removeCategoryFromProduct(UUID productId, Long categoryId);

    //Add Review to product--> product id ,
    List<ReviewResponse> addReview(UUID productId, AddReviewRequest request);

    //Add product images
    ProductResponse addProductImages(UUID productId, List<MultipartFile> files) throws IOException;

    //Get images of product
    List<String> getProductImages(UUID productId);
}
