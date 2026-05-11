package com.aayush.productservice.service.impl;

import com.aayush.productservice.dto.request.CreateCategoryRequest;
import com.aayush.productservice.dto.request.UpdateCategoryRequest;
import com.aayush.productservice.dto.response.CategoryResponse;
import com.aayush.productservice.entity.Category;
import com.aayush.productservice.exception.EasyBuyException;
import com.aayush.productservice.exception.ErrorCode;
import com.aayush.productservice.mapper.CategoryMapper;
import com.aayush.productservice.repository.CategoryRepository;
import com.aayush.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> getAllCategories() {

        log.debug("Fetching all categories");

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponse getCategoryById(Long categoryId) {

        log.debug("Fetching category with id: {}", categoryId);

        return categoryMapper.toResponse(findCategory(categoryId));
    }

    @Transactional
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        log.info("Creating category with title: {}", request.title());

        Category category =
                categoryMapper.toEntity(request);

        Category savedCategory =
                categoryRepository.save(category);

        log.info(
                "Category created successfully with id: {}",
                savedCategory.getId()
        );

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional
    @Override
    public CategoryResponse updateCategory(
            Long categoryId,
            UpdateCategoryRequest request
    ) {

        log.info("Updating category with id: {}", categoryId);

        Category category = findCategory(categoryId);

        categoryMapper.updateCategory(request, category);

        Category updatedCategory =
                categoryRepository.save(category);

        log.info(
                "Category updated successfully with id: {}",
                categoryId
        );

        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {

        log.warn("Deleting category with id: {}", categoryId);

        Category category = findCategory(categoryId);

        categoryRepository.delete(category);

        log.info(
                "Category deleted successfully with id: {}",
                categoryId
        );
    }

    private Category findCategory(Long categoryId) {

        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new EasyBuyException(
                                ErrorCode.CATEGORY_NOT_FOUND,
                                "Category not found with id: " + categoryId
                        )
                );
    }
}