package com.warehouse.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardSummaryResponse(
        long totalSkus,
        long totalUnits,
        BigDecimal inventoryValue,
        long openOrders,
        long lowStockCount,
        Map<String, Long> stockByCategory,
        List<ItemDtos.ItemResponse> lowStockItems
) {}
