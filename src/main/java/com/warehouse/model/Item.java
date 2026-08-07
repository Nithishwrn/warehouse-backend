package com.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "item", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int reorderPoint;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    // Physical warehouse location
    @Column(nullable = false, length = 4)
    private String zone;

    @Column(nullable = false)
    private int aisle;

    @Column(nullable = false)
    private int bin;

    public String getLocation() {
        return "%s-%d-%02d".formatted(zone, aisle, bin);
    }

    @Transient
    public String getStockStatus() {
        if (stock == 0) return "CRITICAL";
        if (stock <= reorderPoint) return "LOW";
        return "OK";
    }
}
