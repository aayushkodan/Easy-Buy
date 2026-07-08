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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductClient productClient;

    @Override
    @Transactional
    public InventoryResponse create(CreateInventoryRequest request) {

        log.info(
                "Creating inventory for productId={}, sku={}",
                request.productId(),
                request.sku()
        );

        try {
            productClient.getProductById(request.productId());
        } catch (Exception e) {

            log.warn(
                    "Product validation failed for productId={}",
                    request.productId()
            );

            throw new EasybuyException(
                    ErrorCode.PRODUCT_NOT_FOUND,
                    "Product not found with productId: " + request.productId()
            );
        }

        String sku = normalizeSku(request.sku());

        if (inventoryRepository.existsBySku(sku)) {
            throw new EasybuyException(
                    ErrorCode.SKU_ALREADY_EXISTS,
                    "SKU already exists with sku: " + sku
            );
        }

        if (inventoryRepository.existsByProductId(request.productId())) {
            throw new EasybuyException(
                    ErrorCode.INVENTORY_ALREADY_EXISTS,
                    "Inventory already exists for productId: " + request.productId()
            );
        }

        Inventory inventory = inventoryMapper.toEntity(request);

        inventory.setActive(true);

        Inventory saved = inventoryRepository.save(inventory);

        log.info(
                "Inventory created successfully. inventoryId={}, productId={}",
                saved.getId(),
                saved.getProductId()
        );

        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse update(UUID id, UpdateInventoryRequest request) {

        log.info("Updating inventory. inventoryId={}", id);

        Inventory inventory = findById(id);

        inventoryMapper.updateInventory(request, inventory);

        Inventory updated = inventoryRepository.save(inventory);

        log.info("Inventory updated successfully. inventoryId={}", id);

        return inventoryMapper.toResponse(updated);
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
    @Transactional
    public InventoryResponse adjustStock(UUID id, AdjustStockRequest request) {

        log.info(
                "Adjusting stock. inventoryId={}, quantity={}, reason={}",
                id,
                request.quantity(),
                request.reason()
        );

        Inventory inventory = findByIdForUpdate(id);

        int newAvailableQuantity =
                safeInt(inventory.getAvailableQuantity()) + request.quantity();

        if (newAvailableQuantity < 0) {
            throw new EasybuyException(
                    ErrorCode.INSUFFICIENT_STOCK,
                    "Insufficient stock for inventoryId: " + id
            );
        }

        inventory.setAvailableQuantity(newAvailableQuantity);

        Inventory saved = inventoryRepository.save(inventory);

        log.info(
                "Stock adjusted successfully. inventoryId={}, availableQuantity={}",
                id,
                saved.getAvailableQuantity()
        );

        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse reserveStock(UUID id, ReserveStockRequest request) {

        log.info(
                "Reserving stock. inventoryId={}, quantity={}",
                id,
                request.quantity()
        );

        Inventory inventory = findByIdForUpdate(id);

        int available = safeInt(inventory.getAvailableQuantity());

        if (available < request.quantity()) {

            log.warn(
                    "Insufficient stock. inventoryId={}, available={}, requested={}",
                    id,
                    available,
                    request.quantity()
            );

            throw new EasybuyException(
                    ErrorCode.INSUFFICIENT_STOCK,
                    "Insufficient stock for inventoryId: " + id
            );
        }

        inventory.setAvailableQuantity(
                available - request.quantity()
        );

        inventory.setReservedQuantity(
                safeInt(inventory.getReservedQuantity())
                        + request.quantity()
        );

        Inventory saved = inventoryRepository.save(inventory);

        if (saved.isLowStock()) {
            log.warn(
                    "Low stock detected. inventoryId={}, availableQuantity={}, reorderLevel={}",
                    saved.getId(),
                    saved.getAvailableQuantity(),
                    saved.getReorderLevel()
            );
        }

        log.info(
                "Stock reserved successfully. inventoryId={}, available={}, reserved={}",
                id,
                saved.getAvailableQuantity(),
                saved.getReservedQuantity()
        );

        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse releaseStock(UUID id, ReleaseStockRequest request) {

        log.info(
                "Releasing stock. inventoryId={}, quantity={}",
                id,
                request.quantity()
        );

        Inventory inventory = findByIdForUpdate(id);

        int reserved = safeInt(inventory.getReservedQuantity());

        if (reserved < request.quantity()) {
            throw new EasybuyException(
                    ErrorCode.STOCK_RELEASE_FAILED,
                    "Cannot release more stock than reserved"
            );
        }

        inventory.setReservedQuantity(
                reserved - request.quantity()
        );

        inventory.setAvailableQuantity(
                safeInt(inventory.getAvailableQuantity())
                        + request.quantity()
        );

        Inventory saved = inventoryRepository.save(inventory);

        log.info(
                "Stock released successfully. inventoryId={}, available={}, reserved={}",
                id,
                saved.getAvailableQuantity(),
                saved.getReservedQuantity()
        );

        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse reserveStockByProductId(UUID productId, ReserveStockRequest request) {
        return reserveStock(findByProductIdForUpdate(productId).getId(), request);
    }

    @Override
    @Transactional
    public InventoryResponse releaseStockByProductId(UUID productId, ReleaseStockRequest request) {
        return releaseStock(findByProductIdForUpdate(productId).getId(), request);
    }

    @Override
    @Transactional
    public void delete(UUID id) {

        log.info("Deleting inventory. inventoryId={}", id);

        Inventory inventory = findById(id);

        inventoryRepository.delete(inventory);

        log.info("Inventory deleted successfully. inventoryId={}", id);
    }

    //checks for sku contains text:
    private String normalizeSku(String sku) {
        //sku normaize rules
        if (!StringUtils.hasText(sku)) {
            throw new EasybuyException(
                    ErrorCode.INVALID_SKU,
                    "SKU must not be empty"
            );
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
