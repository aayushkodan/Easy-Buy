package com.aayush.easybuyinventoryservice.mapper;

import com.aayush.easybuyinventoryservice.dto.request.CreateInventoryRequest;
import com.aayush.easybuyinventoryservice.dto.request.UpdateInventoryRequest;
import com.aayush.easybuyinventoryservice.dto.response.InventoryResponse;
import com.aayush.easybuyinventoryservice.entity.Inventory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryResponse toResponse(
            Inventory inventory
    );

    Inventory toEntity(
            CreateInventoryRequest request
    );

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateInventory(
            UpdateInventoryRequest request,
            @MappingTarget Inventory inventory
    );
}