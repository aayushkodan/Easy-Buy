package com.aayush.productservice.mapper;

import com.aayush.productservice.dto.request.CreateProductRequest;
import com.aayush.productservice.dto.request.UpdateProductRequest;
import com.aayush.productservice.dto.response.ProductResponse;
import com.aayush.productservice.entity.Product;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    Product createToEntity(CreateProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(UpdateProductRequest request, @MappingTarget Product product);
}
