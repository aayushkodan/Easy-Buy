package com.aayush.easybuycartorderservice.mapper;

import com.aayush.easybuycartorderservice.dto.response.OrderItemResponse;
import com.aayush.easybuycartorderservice.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemResponse toResponse(OrderItem item);

}