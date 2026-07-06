package com.aayush.easybuycartorderservice.mapper;

import com.aayush.easybuycartorderservice.dto.response.CartResponse;
import com.aayush.easybuycartorderservice.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {

    CartResponse toResponse(Cart cart);
}