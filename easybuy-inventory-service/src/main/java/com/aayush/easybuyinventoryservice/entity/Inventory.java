package com.aayush.easybuyinventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(
        name = "inventories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product_id",
                        columnNames = "product_id"
                ),
                @UniqueConstraint(
                        name = "uk_inventory_sku",
                        columnNames = "sku"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity{

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID productId;

    @Column(nullable = false, length = 100, unique = true)
    private String sku;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, length = 120)
    private String warehouseLocation;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    //threshold--> refill--> 5
    @Column(nullable = false)
    private Integer reorderLevel;

    @Column(nullable = false)
    private boolean active;

    @Transient
    public Integer getTotalQuantity() {
        return availableQuantity + reservedQuantity;
    }

    @Transient
    public boolean isLowStock() {
        return availableQuantity <= reorderLevel;
    }

    // executed before saving the entity
    @PrePersist
    void onCreate() {
        if (availableQuantity == null) {
            availableQuantity = 0;
        }
        if (reservedQuantity == null) {
            reservedQuantity = 0;
        }
        if (reorderLevel == null) {
            reorderLevel = 0;
        }
    }
}
