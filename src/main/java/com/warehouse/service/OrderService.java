package com.warehouse.service;

import com.warehouse.dto.OrderDtos.OrderLineRequest;
import com.warehouse.dto.OrderDtos.OrderRequest;
import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.model.Item;
import com.warehouse.model.Order;
import com.warehouse.model.OrderLine;
import com.warehouse.model.OrderStatus;
import com.warehouse.repository.ItemRepository;
import com.warehouse.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final Random random = new Random();

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @Transactional
    public Order create(OrderRequest req) {
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(req.customer())
                .status(OrderStatus.PENDING)
                .build();

        for (OrderLineRequest lineReq : req.lines()) {
            Item item = itemRepository.findBySku(lineReq.sku())
                    .orElseThrow(() -> new ResourceNotFoundException("Unknown SKU: " + lineReq.sku()));
            if (item.getStock() < lineReq.quantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for %s: requested %d, available %d".formatted(item.getSku(), lineReq.quantity(), item.getStock()));
            }
            item.setStock(item.getStock() - lineReq.quantity());
            itemRepository.save(item);

            order.addLine(OrderLine.builder().item(item).quantity(lineReq.quantity()).build());
        }

        return orderRepository.save(order);
    }

    /** Advances an order to the next stage in the fulfillment pipeline (PENDING -> PICKING -> PACKED -> SHIPPED). */
    public Order advanceStatus(Long id) {
        Order order = findById(id);
        order.setStatus(order.getStatus().next());
        return orderRepository.save(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = findById(id);
        // Restock items if the order is cancelled before shipping
        if (order.getStatus() != OrderStatus.SHIPPED) {
            for (OrderLine line : order.getLines()) {
                Item item = line.getItem();
                item.setStock(item.getStock() + line.getQuantity());
                itemRepository.save(item);
            }
        }
        orderRepository.delete(order);
    }

    private String generateOrderNumber() {
        String candidate;
        do {
            candidate = "ORD-" + (5000 + random.nextInt(5000));
        } while (orderRepository.findByOrderNumber(candidate).isPresent());
        return candidate;
    }
}
