package com.warehouse.model;

public enum OrderStatus {
    PENDING,
    PICKING,
    PACKED,
    SHIPPED;

    /** Returns the next status in the fulfillment pipeline, or the same status if already terminal. */
    public OrderStatus next() {
        OrderStatus[] flow = values();
        int idx = this.ordinal();
        return idx < flow.length - 1 ? flow[idx + 1] : this;
    }
}
