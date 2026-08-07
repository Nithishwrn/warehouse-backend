package com.warehouse.controller;

import com.warehouse.dto.OrderDtos.OrderRequest;
import com.warehouse.dto.OrderDtos.OrderResponse;
import com.warehouse.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> list() {
        return orderService.findAll().stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return OrderResponse.from(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(OrderResponse.from(orderService.create(request)));
    }

    /** Moves the order to the next stage: PENDING -> PICKING -> PACKED -> SHIPPED. */
    @PatchMapping("/{id}/advance")
    public OrderResponse advance(@PathVariable Long id) {
        return OrderResponse.from(orderService.advanceStatus(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
