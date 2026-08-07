package com.warehouse.model;

/**
 * Application roles, from broadest to narrowest access.
 * ADMIN    - manage users, all inventory & order operations, full analytics
 * MANAGER  - manage inventory & orders, view analytics, cannot manage users
 * STAFF    - view inventory, advance order status (picking/packing), cannot edit items or costs
 */
public enum Role {
    ADMIN,
    MANAGER,
    STAFF
}
