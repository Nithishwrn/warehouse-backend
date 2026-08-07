package com.warehouse.dto;

import com.warehouse.model.Item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ItemDtos {

    public record ItemRequest(
            @NotBlank String name,
            @NotBlank String category,
            @Min(0) int stock,
            @Min(0) int reorderPoint,
            @NotNull BigDecimal unitCost,
            String zone,   // optional on create - randomly assigned if blank
            Integer aisle,
            Integer bin
    ) {}

    public record StockAdjustRequest(
            @NotNull Integer delta // positive to add stock, negative to remove
    ) {}

    public record ItemResponse(
            Long id,
            String sku,
            String name,
            String category,
            int stock,
            int reorderPoint,
            BigDecimal unitCost,
            String location,
            String status
    ) {
        public static ItemResponse from(Item item) {
            return new ItemResponse(
                    item.getId(), item.getSku(), item.getName(), item.getCategory(),
                    item.getStock(), item.getReorderPoint(), item.getUnitCost(),
                    item.getLocation(), item.getStockStatus()
            );
        }
    }
}
