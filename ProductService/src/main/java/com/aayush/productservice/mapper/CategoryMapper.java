package com.aayush.productservice.mapper;

import com.aayush.productservice.dto.request.CreateCategoryRequest;
import com.aayush.productservice.dto.request.UpdateCategoryRequest;
import com.aayush.productservice.dto.request.UpdateProductRequest;
import com.aayush.productservice.dto.response.CategoryResponse;
import com.aayush.productservice.entity.Category;
import com.aayush.productservice.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    Category toEntity(CreateCategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(UpdateCategoryRequest request, @MappingTarget Category category);
}
