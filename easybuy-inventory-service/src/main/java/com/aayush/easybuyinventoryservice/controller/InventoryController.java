package com.aayush.easybuyinventoryservice.controller;

import com.aayush.easybuyinventoryservice.dto.request.*;
import com.aayush.easybuyinventoryservice.dto.response.InventoryResponse;
import com.aayush.easybuyinventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/inventories/")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody CreateInventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<InventoryResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public InventoryResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/sku/{sku}")
    public InventoryResponse getBySku(@PathVariable String sku) {
        return service.getBySku(sku);
    }

    @GetMapping("/product/{productId}")
    public InventoryResponse getByProductId(@PathVariable UUID productId) {
        return service.getByProductId(productId);
    }

    @GetMapping("/low-stock")
    public List<InventoryResponse> getLowStock(@RequestParam(defaultValue = "10") @Min(0) int threshold) {
        return service.getLowStock(threshold);
    }

    @PutMapping("/{id}")
    public InventoryResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateInventoryRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/adjust-stock")
    public InventoryResponse adjustStock(@PathVariable UUID id, @Valid @RequestBody AdjustStockRequest request) {
        return service.adjustStock(id, request);
    }

    @PostMapping("/{id}/reserve")
    public InventoryResponse reserve(@PathVariable UUID id, @Valid @RequestBody ReserveStockRequest request) {
        return service.reserveStock(id, request);
    }

    @PostMapping("/{id}/release")
    public InventoryResponse release(@PathVariable UUID id, @Valid @RequestBody ReleaseStockRequest request) {
        return service.releaseStock(id, request);
    }

    @PostMapping("/product/{productId}/reserve")
    public InventoryResponse reserveByProductId(@PathVariable UUID productId, @Valid @RequestBody ReserveStockRequest request) {
        return service.reserveStockByProductId(productId, request);
    }

    @PostMapping("/product/{productId}/release")
    public InventoryResponse releaseByProductId(@PathVariable UUID productId, @Valid @RequestBody ReleaseStockRequest request) {
        return service.releaseStockByProductId(productId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
