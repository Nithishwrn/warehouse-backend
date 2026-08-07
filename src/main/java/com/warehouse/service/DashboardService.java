package com.warehouse.service;

import com.warehouse.dto.DashboardSummaryResponse;
import com.warehouse.dto.ItemDtos.ItemResponse;
import com.warehouse.model.Item;
import com.warehouse.model.OrderStatus;
import com.warehouse.repository.ItemRepository;
import com.warehouse.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    public DashboardSummaryResponse summary() {
        List<Item> items = itemRepository.findAll();

        long totalUnits = items.stream().mapToLong(Item::getStock).sum();
        BigDecimal inventoryValue = items.stream()
                .map(i -> i.getUnitCost().multiply(BigDecimal.valueOf(i.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Item> lowStock = items.stream()
                .filter(i -> !"OK".equals(i.getStockStatus()))
                .toList();

        long openOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.PICKING)
                .count();

        Map<String, Long> stockByCategory = items.stream()
                .collect(Collectors.groupingBy(Item::getCategory, Collectors.summingLong(Item::getStock)));

        return new DashboardSummaryResponse(
                items.size(),
                totalUnits,
                inventoryValue,
                openOrders,
                lowStock.size(),
                stockByCategory,
                lowStock.stream().map(ItemResponse::from).toList()
        );
    }
}
