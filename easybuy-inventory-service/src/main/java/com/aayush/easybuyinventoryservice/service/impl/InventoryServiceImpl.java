package com.aayush.easybuyinventoryservice.service.impl;

import com.aayush.easybuyinventoryservice.dto.request.*;
import com.aayush.easybuyinventoryservice.dto.response.InventoryResponse;
import com.aayush.easybuyinventoryservice.entity.Inventory;
import com.aayush.easybuyinventoryservice.exception.EasybuyException;
import com.aayush.easybuyinventoryservice.exception.ErrorCode;
import com.aayush.easybuyinventoryservice.external.ProductClient;
import com.aayush.easybuyinventoryservice.mapper.InventoryMapper;
import com.aayush.easybuyinventoryservice.repository.InventoryRepository;
import com.aayush.easybuyinventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductClient productClient;

    @Override
    public InventoryResponse create(CreateInventoryRequest request) {

        try{
            productClient.getProductById(request.productId());
        }
        catch (Exception e){
            throw new EasybuyException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found with productId: " + request.productId());
        }

        String sku = normalizeSku(request.sku());

        if(inventoryRepository.existsBySku(sku)){
            throw new EasybuyException(ErrorCode.SKU_ALREADY_EXISTS, "SKU already exists with sku: " + sku);
        }
        if(inventoryRepository.existsById(request.productId())){
            throw new EasybuyException(ErrorCode.INVENTORY_ALREADY_EXISTS, "inventory already exists with productId: " + request.productId());
        }
        return inventoryMapper.toResponse(inventoryRepository.save(inventoryMapper.toEntity(request)));
    }

    @Override
    public InventoryResponse update(UUID id, UpdateInventoryRequest request) {
        Inventory inventory = findById(id);
        inventoryMapper.updateInventory(request,inventory);
        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getById(UUID id) {
        return inventoryMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getBySku(String sku) {
        return inventoryMapper.toResponse(inventoryRepository.findBySku(normalizeSku(sku))
                .orElseThrow(() -> new EasybuyException(ErrorCode.INVENTORY_NOT_FOUND, "Inventory not found with sku: " + sku)));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(UUID productId) {
        return inventoryMapper.toResponse(inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EasybuyException(ErrorCode.INVENTORY_NOT_FOUND, "Inventory not found with productId: " + productId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAll() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStock(int threshold) {
        return inventoryRepository.findByAvailableQuantityLessThanEqualAndActiveTrueOrderByAvailableQuantityAsc(threshold)
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    public InventoryResponse adjustStock(UUID id, AdjustStockRequest request) {
        Inventory inventory = findByIdForUpdate(id);
        Integer quantity = request.quantity();
        int newAvailableQuantity = safeInt(inventory.getAvailableQuantity()) + quantity;
        if(newAvailableQuantity < 0){
            throw new EasybuyException(ErrorCode.INSUFFICIENT_STOCK, "Insufficient stock for productId: " + id);
        }
        inventory.setAvailableQuantity(newAvailableQuantity);

        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse reserveStock(UUID id, ReserveStockRequest request) {
        Inventory inventory = findByIdForUpdate(id);
        Integer quantity = request.quantity();
        Integer available = safeInt(inventory.getAvailableQuantity());
        if(available < quantity){
            throw new EasybuyException(ErrorCode.INSUFFICIENT_STOCK, "Insufficient stock for productId: " + id);
        }
        inventory.setAvailableQuantity(available - quantity);
        inventory.setReservedQuantity(safeInt(inventory.getReservedQuantity()) + quantity);

        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse releaseStock(UUID id, ReleaseStockRequest request) {
        Inventory inventory = findByIdForUpdate(id);
        Integer quantity = request.quantity();
        int reserved = safeInt(inventory.getReservedQuantity());

        if(reserved < quantity){
            throw new EasybuyException(ErrorCode.INSUFFICIENT_STOCK, "Insufficient stock for productId: " + id);
        }

        inventory.setReservedQuantity(reserved - quantity);
        inventory.setAvailableQuantity(safeInt(inventory.getAvailableQuantity()) + quantity);

        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse reserveStockByProductId(UUID productId, ReserveStockRequest request) {
        return reserveStock(findByProductIdForUpdate(productId).getId(), request);
    }

    @Override
    public InventoryResponse releaseStockByProductId(UUID productId, ReleaseStockRequest request) {
        return releaseStock(findByProductIdForUpdate(productId).getId(), request);
    }

    @Override
    public void delete(UUID id) {
        inventoryRepository.deleteById(id);
    }

    //checks for sku contains text:
    private String normalizeSku(String sku) {
        //sku normaize rules
        if (!StringUtils.hasText(sku)) {
            throw new EasybuyException(ErrorCode.SKU_ALREADY_EXISTS, "SKU must not be empty");
        }
//		IPHONE-14-BLACK
        return sku.trim().toUpperCase();
    }

    private Inventory findById(UUID id){
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new EasybuyException(ErrorCode.INVENTORY_NOT_FOUND, "Inventory not found with id: " + id));
    }

    private Inventory findByIdForUpdate(UUID id){
        return inventoryRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EasybuyException(ErrorCode.INVENTORY_NOT_FOUND, "Inventory not found with id: " + id));
    }

    private Inventory findByProductIdForUpdate(UUID productId){
        return inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new EasybuyException(ErrorCode.INVENTORY_NOT_FOUND, "Inventory not found with productId: " + productId));
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
