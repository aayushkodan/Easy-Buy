package com.aayush.easybuycartorderservice.mapper;

import com.aayush.easybuycartorderservice.dto.response.OrderResponse;
import com.aayush.easybuycartorderservice.entity.Order;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = OrderItemMapper.class
)
public interface OrderMapper {

    OrderResponse toResponse(Order order);

}