package com.aayush.productservice.service;

import com.aayush.productservice.dto.request.CreateCategoryRequest;
import com.aayush.productservice.dto.request.UpdateCategoryRequest;
import com.aayush.productservice.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long categoryId);

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest request);

    void deleteCategory(Long categoryId);
}