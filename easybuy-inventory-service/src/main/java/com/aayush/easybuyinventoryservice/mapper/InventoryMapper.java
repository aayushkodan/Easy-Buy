package com.aayush.easybuyinventoryservice.mapper;

import com.aayush.easybuyinventoryservice.dto.request.CreateInventoryRequest;
import com.aayush.easybuyinventoryservice.dto.request.UpdateInventoryRequest;
import com.aayush.easybuyinventoryservice.dto.response.InventoryResponse;
import com.aayush.easybuyinventoryservice.entity.Inventory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(
            target = "totalQuantity",
            expression = "java(inventory.getTotalQuantity())"
    )
    @Mapping(
            target = "lowStock",
            expression = "java(inventory.isLowStock())"
    )
    InventoryResponse toResponse(Inventory inventory);

    Inventory toEntity(CreateInventoryRequest request);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateInventory(
            UpdateInventoryRequest request,
            @MappingTarget Inventory inventory
    );
}