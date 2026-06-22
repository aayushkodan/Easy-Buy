package com.aayush.easybuyinventoryservice.service;

import java.util.List;
import java.util.UUID;

import com.aayush.easybuyinventoryservice.dto.request.AdjustStockRequest;
import com.aayush.easybuyinventoryservice.dto.request.CreateInventoryRequest;
import com.aayush.easybuyinventoryservice.dto.response.InventoryResponse;
import com.aayush.easybuyinventoryservice.dto.request.ReleaseStockRequest;
import com.aayush.easybuyinventoryservice.dto.request.ReserveStockRequest;
import com.aayush.easybuyinventoryservice.dto.request.UpdateInventoryRequest;


//inventory item main opreations :
//logics:
public interface InventoryService {

    //create the inventory
    InventoryResponse create(CreateInventoryRequest request);

    //update inventory
    InventoryResponse update(UUID id, UpdateInventoryRequest request);

    InventoryResponse getById(UUID id);

    InventoryResponse getBySku(String sku);

    InventoryResponse getByProductId(UUID productId);

    List<InventoryResponse> getAll();

    List<InventoryResponse> getLowStock(int threshold);

    InventoryResponse adjustStock(UUID id, AdjustStockRequest request);

    InventoryResponse reserveStock(UUID id, ReserveStockRequest request);

    InventoryResponse releaseStock(UUID id, ReleaseStockRequest request);

    InventoryResponse reserveStockByProductId(UUID productId, ReserveStockRequest request);

    InventoryResponse releaseStockByProductId(UUID productId, ReleaseStockRequest request);

    void delete(UUID id);
}