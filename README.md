# Smart Inventory & Warehouse Management System — Java Backend

A Spring Boot REST API for the warehouse management system: JWT authentication,
role-based access control, and JPA-backed inventory, order, and user management.
Pairs with the React frontend prototype, but is a complete, independently runnable
Java project on its own.

## Stack

- **Java 17**, **Spring Boot 3.3** (Web, Security, Data JPA, Validation)
- **Spring Security + JWT** (`io.jsonwebtoken`) for stateless authentication
- **Role-based access control** via `@EnableMethodSecurity` + URL-level rules (`SecurityConfig`)
- **H2** (file-backed, zero setup) for local/dev; **PostgreSQL** ready for production — swap in `application.properties`
- **Lombok** to cut boilerplate on entities/DTOs

## Project structure

```
src/main/java/com/warehouse/
  config/       SecurityConfig, DataSeeder (demo data on startup)
  security/     JwtUtil, JwtAuthFilter, CustomUserDetailsService
  model/        User, Role, Item, Order, OrderLine, OrderStatus  (JPA entities)
  repository/   Spring Data JPA repositories
  dto/          Request/response records — entities never leave the service layer
  service/      Business logic (stock rules, order pipeline, auth)
  controller/   REST endpoints
  exception/    Centralized error handling (404 / 409 / 401 / 403 / validation)
```

## Roles

| Role    | Can do |
|---------|--------|
| `ADMIN`   | Everything: manage users & roles, inventory, orders, analytics |
| `MANAGER` | Manage inventory & orders, view analytics — cannot manage users |
| `STAFF`   | View inventory, advance orders through picking/packing — cannot edit items, costs, or users |

Enforced in two layers: URL-level rules in `SecurityConfig`, and method-level
`@PreAuthorize` is available via `@EnableMethodSecurity` if you want finer-grained
checks inside services.

## Running it

```bash
cd warehouse-backend
mvn spring-boot:run
```

The app starts on `http://localhost:8080` and auto-creates an H2 database file
at `./data/warehousedb`. On first boot, `DataSeeder` creates three demo accounts
and a starter catalog of items:

| Username | Password    | Role    |
|----------|-------------|---------|
| admin    | admin123    | ADMIN   |
| manager  | manager123  | MANAGER |
| staff    | staff123    | STAFF   |

H2 console (browse the DB directly): `http://localhost:8080/h2-console`
JDBC URL: `jdbc:h2:file:./data/warehousedb`

### Switching to PostgreSQL

In `application.properties`, comment out the H2 block and uncomment the
PostgreSQL block, pointing it at a real database and credentials. No code
changes needed — Spring Data JPA and Hibernate handle the dialect switch.

## API reference

All endpoints except `/api/auth/**` require a `Authorization: Bearer <token>` header.

### Auth
```
POST /api/auth/login      { "username": "admin", "password": "admin123" }
POST /api/auth/register   { "username", "password", "fullName", "role" }
```
Both return `{ token, username, fullName, role }`.

### Items (inventory)
```
GET    /api/items?query=&category=      any authenticated role
GET    /api/items/{id}                  any authenticated role
POST   /api/items                       ADMIN, MANAGER
PUT    /api/items/{id}                  ADMIN, MANAGER
PATCH  /api/items/{id}/stock            ADMIN, MANAGER   body: { "delta": -5 }
DELETE /api/items/{id}                  ADMIN, MANAGER
```

### Orders
```
GET    /api/orders                      any authenticated role
GET    /api/orders/{id}                 any authenticated role
POST   /api/orders                      ADMIN, MANAGER   body: { customer, lines: [{sku, quantity}] }
PATCH  /api/orders/{id}/advance         any authenticated role (picking is floor work)
DELETE /api/orders/{id}                 ADMIN, MANAGER
```
Creating an order decrements stock immediately and validates availability;
deleting a non-shipped order restocks it.

### Dashboard
```
GET /api/dashboard/summary              ADMIN, MANAGER
```
Returns total SKUs, units on hand, inventory value, open orders, low-stock
count/list, and stock grouped by category.

### Users (admin only)
```
GET   /api/users
PATCH /api/users/{id}/role       body: { "role": "MANAGER" }
PATCH /api/users/{id}/enabled    body: { "enabled": false }
```

## Example: end-to-end curl session

```bash
# Log in as manager
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"manager123"}' | jq -r .token)

# List low-stock items via the dashboard
curl -s localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer $TOKEN" | jq .lowStockItems

# Create an order
curl -s -X POST localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"customer":"Northbridge Retail","lines":[{"sku":"SKU-1001","quantity":3}]}'
```

## Notes on design decisions worth mentioning in an interview

- **Stateless JWT auth** — no server-side session store, so the API scales horizontally without sticky sessions.
- **DTOs never expose entities directly** — password hashes and JPA lazy-proxy issues never leak into JSON responses.
- **Order creation is transactional** — stock decrement and order-line creation succeed or fail together (`@Transactional` in `OrderService`).
- **Centralized exception handling** — `GlobalExceptionHandler` turns domain exceptions into consistent JSON error shapes instead of leaking stack traces.
- **RBAC enforced at the framework level**, not scattered `if` checks in controllers — one source of truth in `SecurityConfig`.
