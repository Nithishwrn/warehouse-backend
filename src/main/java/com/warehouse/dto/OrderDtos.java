package com.warehouse.dto;

import com.warehouse.model.Order;
import com.warehouse.model.OrderLine;
import com.warehouse.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {

    public record OrderLineRequest(
            @NotBlank String sku,
            @Min(1) int quantity
    ) {}

    public record OrderRequest(
            @NotBlank String customer,
            @NotEmpty @Valid List<OrderLineRequest> lines
    ) {}

    public record OrderLineResponse(String sku, String name, int quantity) {
        public static OrderLineResponse from(OrderLine line) {
            return new OrderLineResponse(line.getItem().getSku(), line.getItem().getName(), line.getQuantity());
        }
    }

    public record OrderResponse(
            Long id,
            String orderNumber,
            String customer,
            OrderStatus status,
            LocalDateTime createdAt,
            List<OrderLineResponse> lines
    ) {
        public static OrderResponse from(Order order) {
            return new OrderResponse(
                    order.getId(), order.getOrderNumber(), order.getCustomer(), order.getStatus(),
                    order.getCreatedAt(), order.getLines().stream().map(OrderLineResponse::from).toList()
            );
        }
    }
}
