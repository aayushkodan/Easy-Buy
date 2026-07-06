package com.aayush.easybuycartorderservice.mapper;

import com.aayush.easybuycartorderservice.dto.response.CartItemResponse;
import com.aayush.easybuycartorderservice.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    CartItemResponse toResponse(CartItem item);

}