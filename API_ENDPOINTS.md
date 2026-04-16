# EatClub API Endpoints Documentation

## Overview
Complete API documentation for EatClub Backend. All timestamps are in Unix milliseconds.

**Base URL:** `http://localhost:8080/api/v1`

---

## Table of Contents
1. [Authentication Controller](#1-auth-controller)
2. [User Controller](#2-user-controller)
3. [Restaurant Controller](#3-restaurant-controller)
4. [Menu Controller](#4-menu-controller)
5. [Cart Controller](#5-cart-controller)
6. [Order Controller](#6-order-controller)

---

## 1. AUTH CONTROLLER
**Base Path:** `/api/v1/auth`

### POST /signup - Create a new user account
Create a new user account with specified role.

**Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "role": "USER"
}
```

**Response (201 Created):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": 1713272400000
}
```

**Request Headers:**
```
Content-Type: application/json
```

**Validation Rules:**
- `name`: Required, non-blank
- `email`: Required, valid email format, non-blank
- `password`: Required, minimum 8 characters
- `role`: Required, must be USER or ADMIN

---

### POST /login - User login
Authenticate user and retrieve JWT token.

**Request:**
```json
{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzEzMjcyNDAwLCJleHAiOjE3MTMzNTg4MDB9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Request Headers:**
```
Content-Type: application/json
```

**Validation Rules:**
- `email`: Required, valid email format
- `password`: Required, non-blank

**Note:** Token is valid for 24 hours (86400000 milliseconds)

---

## 2. USER CONTROLLER
**Base Path:** `/api/v1/users`
**Required:** JWT Token in Authorization header

### GET /profile - Get current user profile
Retrieve the authenticated user's profile information.

**Request:**
```
GET /api/v1/users/profile
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "phoneNumber": "+1234567890",
  "address": "123 Main St, City",
  "createdAt": 1713272400000,
  "updatedAt": 1713272400000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

### PUT /profile - Update user profile
Update the authenticated user's profile information.

**Request:**
```json
{
  "name": "John Smith",
  "phoneNumber": "+9876543210",
  "address": "456 Oak Ave, New City"
}
```

**Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Smith",
  "email": "john@example.com",
  "role": "USER",
  "phoneNumber": "+9876543210",
  "address": "456 Oak Ave, New City",
  "createdAt": 1713272400000,
  "updatedAt": 1713272500000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Note:** Email cannot be updated and is immutable

---

## 3. RESTAURANT CONTROLLER
**Base Path:** `/api/v1/restaurants`

### POST / - Create restaurant (Admin only)
Create a new restaurant. Requires ADMIN role.

**Request:**
```json
{
  "restaurantName": "Pizza Palace",
  "description": "Best pizza in town",
  "address": "789 Restaurant St, City",
  "phoneNumber": "+1111111111",
  "cuisineType": "Italian",
  "isActive": true
}
```

**Response (201 Created):**
```json
{
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "adminId": "550e8400-e29b-41d4-a716-446655440000",
  "restaurantName": "Pizza Palace",
  "description": "Best pizza in town",
  "address": "789 Restaurant St, City",
  "phoneNumber": "+1111111111",
  "cuisineType": "Italian",
  "isActive": true,
  "menuItems": [],
  "createdAt": 1713272600000,
  "updatedAt": 1713272600000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Validation Rules:**
- `restaurantName`: Required, non-blank
- `address`: Required, non-blank
- `cuisineType`: Optional
- `isActive`: Optional, defaults to true

**Permissions:** Admin only (ROLE_ADMIN)

---

### GET / - Get all restaurants (paginated)
Retrieve all active restaurants with pagination and optional filters.

**Request:**
```
GET /api/v1/restaurants?page=0&size=10&cuisineType=Italian
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
      "restaurantName": "Pizza Palace",
      "cuisineType": "Italian",
      "isActive": true,
      "createdAt": 1713272600000
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

**Query Parameters:**
- `page`: Page number (0-indexed), default: 0
- `size`: Page size, default: 10
- `cuisineType`: Optional filter by cuisine type

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

### GET /{restaurantId} - Get restaurant by ID
Retrieve detailed information for a specific restaurant including menu items.

**Request:**
```
GET /api/v1/restaurants/660e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "adminId": "550e8400-e29b-41d4-a716-446655440000",
  "restaurantName": "Pizza Palace",
  "description": "Best pizza in town",
  "address": "789 Restaurant St, City",
  "phoneNumber": "+1111111111",
  "cuisineType": "Italian",
  "isActive": true,
  "menuItems": [
    {
      "itemId": "770e8400-e29b-41d4-a716-446655440002",
      "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
      "itemName": "Margherita Pizza",
      "price": 12.99,
      "category": "Pizza",
      "isAvailable": true
    }
  ],
  "createdAt": 1713272600000,
  "updatedAt": 1713272600000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

### PUT /{restaurantId} - Update restaurant (Admin only)
Update restaurant details. Requires ADMIN role.

**Request:**
```json
{
  "restaurantName": "Pizza Palace Plus",
  "description": "Best pizza updated",
  "cuisineType": "Italian & Mediterranean",
  "phoneNumber": "+2222222222",
  "isActive": true
}
```

**Response (200 OK):**
```json
{
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "adminId": "550e8400-e29b-41d4-a716-446655440000",
  "restaurantName": "Pizza Palace Plus",
  "description": "Best pizza updated",
  "address": "789 Restaurant St, City",
  "phoneNumber": "+2222222222",
  "cuisineType": "Italian & Mediterranean",
  "isActive": true,
  "menuItems": [],
  "createdAt": 1713272600000,
  "updatedAt": 1713272650000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Permissions:** Admin only (ROLE_ADMIN)

---

### DELETE /{restaurantId} - Delete restaurant (Admin only)
Delete a restaurant. Requires ADMIN role.

**Request:**
```
DELETE /api/v1/restaurants/660e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <JWT_TOKEN>
```

**Response (204 No Content)**

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Permissions:** Admin only (ROLE_ADMIN)

---

## 4. MENU CONTROLLER
**Base Path:** `/api/v1/restaurants/{restaurantId}/menu-items`

### POST / - Create menu item (Admin only)
Add a new menu item to a restaurant. Requires ADMIN role.

**Request:**
```json
{
  "itemName": "Margherita Pizza",
  "description": "Classic pizza with fresh mozzarella",
  "price": 12.99,
  "category": "Pizza",
  "isVegetarian": true,
  "isSpicy": false,
  "preparationTime": 20,
  "imageUrl": "https://image-url.com/pizza.jpg",
  "isAvailable": true
}
```

**Response (201 Created):**
```json
{
  "itemId": "770e8400-e29b-41d4-a716-446655440002",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "itemName": "Margherita Pizza",
  "description": "Classic pizza with fresh mozzarella",
  "price": 12.99,
  "category": "Pizza",
  "isVegetarian": true,
  "isSpicy": false,
  "preparationTime": 20,
  "imageUrl": "https://image-url.com/pizza.jpg",
  "isAvailable": true,
  "createdAt": 1713272700000,
  "updatedAt": 1713272700000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Validation Rules:**
- `itemName`: Required, non-blank
- `price`: Required, minimum 0.0
- Other fields: Optional

**Permissions:** Admin only (ROLE_ADMIN)

---

### GET / - Get menu items by restaurant
Retrieve all menu items for a restaurant with optional filters.

**Request:**
```
GET /api/v1/restaurants/660e8400-e29b-41d4-a716-446655440001/menu-items?category=Pizza&isVegetarian=true&page=0&size=20
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "itemId": "770e8400-e29b-41d4-a716-446655440002",
      "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
      "itemName": "Margherita Pizza",
      "description": "Classic pizza with fresh mozzarella",
      "price": 12.99,
      "category": "Pizza",
      "isVegetarian": true,
      "isSpicy": false,
      "preparationTime": 20,
      "imageUrl": "https://image-url.com/pizza.jpg",
      "isAvailable": true,
      "createdAt": 1713272700000,
      "updatedAt": 1713272700000
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

**Query Parameters:**
- `category`: Optional filter by category
- `isVegetarian`: Optional filter (true/false)
- `page`: Page number (0-indexed), default: 0
- `size`: Page size, default: 20

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

### PUT /{itemId} - Update menu item (Admin only)
Update menu item details. Requires ADMIN role.

**Request:**
```json
{
  "itemName": "Margherita Pizza Supreme",
  "price": 14.99,
  "description": "Updated description",
  "isAvailable": true
}
```

**Response (200 OK):**
```json
{
  "itemId": "770e8400-e29b-41d4-a716-446655440002",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "itemName": "Margherita Pizza Supreme",
  "description": "Updated description",
  "price": 14.99,
  "category": "Pizza",
  "isVegetarian": true,
  "isSpicy": false,
  "preparationTime": 20,
  "imageUrl": "https://image-url.com/pizza.jpg",
  "isAvailable": true,
  "createdAt": 1713272700000,
  "updatedAt": 1713272750000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Permissions:** Admin only (ROLE_ADMIN)

---

### DELETE /{itemId} - Delete menu item (Admin only)
Remove a menu item from a restaurant. Requires ADMIN role.

**Request:**
```
DELETE /api/v1/restaurants/660e8400-e29b-41d4-a716-446655440001/menu-items/770e8400-e29b-41d4-a716-446655440002
Authorization: Bearer <JWT_TOKEN>
```

**Response (204 No Content)**

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Permissions:** Admin only (ROLE_ADMIN)

---

### PATCH /{itemId}/availability - Update item availability (Admin only)
Update item availability status. Requires ADMIN role.

**Request:**
```json
{
  "isAvailable": false
}
```

**Response (200 OK):**
```json
{
  "itemId": "770e8400-e29b-41d4-a716-446655440002",
  "itemName": "Margherita Pizza",
  "isAvailable": false
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Permissions:** Admin only (ROLE_ADMIN)

---

## 5. CART CONTROLLER
**Base Path:** `/api/v1/cart`
**Required:** JWT Token in Authorization header

### POST / - Initialize cart
Create or initialize cart for the authenticated user.

**Request:**
```
POST /api/v1/cart
Authorization: Bearer <JWT_TOKEN>
```

**Response (201 Created):**
```json
{
  "cartId": "880e8400-e29b-41d4-a716-446655440003",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "restaurantId": null,
  "cartItems": [],
  "subtotal": 0.00,
  "tax": 0.00,
  "deliveryCharges": 0.00,
  "total": 0.00,
  "lastUpdated": 1713272800000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

---

### GET / - Get current cart
Retrieve the authenticated user's cart details.

**Request:**
```
GET /api/v1/cart
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "cartId": "880e8400-e29b-41d4-a716-446655440003",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "cartItems": [
    {
      "cartItemId": "990e8400-e29b-41d4-a716-446655440004",
      "itemId": "770e8400-e29b-41d4-a716-446655440002",
      "itemName": "Margherita Pizza",
      "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
      "quantity": 2,
      "unitPrice": 12.99,
      "totalPrice": 25.98,
      "specialInstructions": "Extra cheese",
      "addedAt": 1713272800000
    }
  ],
  "subtotal": 25.98,
  "tax": 2.60,
  "deliveryCharges": 3.00,
  "total": 31.58,
  "lastUpdated": 1713272800000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

### POST /items - Add item to cart
Add a menu item to the cart or update quantity if already present.

**Request:**
```json
{
  "itemId": "770e8400-e29b-41d4-a716-446655440002",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "quantity": 2,
  "specialInstructions": "Extra cheese"
}
```

**Response (201 Created):**
```json
{
  "cartItemId": "990e8400-e29b-41d4-a716-446655440004",
  "itemId": "770e8400-e29b-41d4-a716-446655440002",
  "itemName": "Margherita Pizza",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "quantity": 2,
  "unitPrice": 12.99,
  "totalPrice": 25.98,
  "specialInstructions": "Extra cheese",
  "addedAt": 1713272800000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Validation Rules:**
- `itemId`: Required, non-blank
- `restaurantId`: Required, non-blank
- `quantity`: Required, minimum 1
- `specialInstructions`: Optional

**Note:** Only items from the same restaurant can be in cart

---

### PUT /items/{cartItemId} - Update cart item quantity
Update quantity and special instructions for a cart item.

**Request:**
```json
{
  "quantity": 3,
  "specialInstructions": "Extra cheese and oregano"
}
```

**Response (200 OK):**
```json
{
  "cartItemId": "990e8400-e29b-41d4-a716-446655440004",
  "itemId": "770e8400-e29b-41d4-a716-446655440002",
  "itemName": "Margherita Pizza",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "quantity": 3,
  "unitPrice": 12.99,
  "totalPrice": 38.97,
  "specialInstructions": "Extra cheese and oregano",
  "addedAt": 1713272800000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Validation Rules:**
- `quantity`: Minimum 1

---

### DELETE /items/{cartItemId} - Remove item from cart
Remove a specific item from the cart.

**Request:**
```
DELETE /api/v1/cart/items/990e8400-e29b-41d4-a716-446655440004
Authorization: Bearer <JWT_TOKEN>
```

**Response (204 No Content)**

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

### DELETE / - Clear entire cart
Remove all items from the cart.

**Request:**
```
DELETE /api/v1/cart
Authorization: Bearer <JWT_TOKEN>
```

**Response (204 No Content)**

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 6. ORDER CONTROLLER
**Base Path:** `/api/v1/orders`
**Required:** JWT Token in Authorization header

### POST / - Create order
Create a new order from the current cart.

**Request:**
```json
{
  "deliveryAddress": "123 Main St, City",
  "phoneNumber": "+1234567890",
  "paymentMethod": "CREDIT_CARD",
  "specialInstructions": "Ring bell twice"
}
```

**Response (201 Created):**
```json
{
  "orderId": "aa0e8400-e29b-41d4-a716-446655440005",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "restaurantName": "Pizza Palace",
  "orderItems": [
    {
      "orderItemId": "bb0e8400-e29b-41d4-a716-446655440006",
      "itemId": "770e8400-e29b-41d4-a716-446655440002",
      "itemName": "Margherita Pizza",
      "quantity": 2,
      "unitPrice": 12.99,
      "totalPrice": 25.98
    }
  ],
  "deliveryAddress": "123 Main St, City",
  "subtotal": 25.98,
  "tax": 2.60,
  "deliveryCharges": 3.00,
  "total": 31.58,
  "orderStatus": "PENDING",
  "paymentStatus": "PAID",
  "paymentMethod": "CREDIT_CARD",
  "estimatedDeliveryTime": 1713273600000,
  "createdAt": 1713272900000,
  "deliveredAt": null
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Validation Rules:**
- `deliveryAddress`: Required, non-blank
- `phoneNumber`: Required, non-blank
- `paymentMethod`: Required, non-blank
- `specialInstructions`: Optional

**Note:** Order is automatically cleared from cart after creation

---

### GET /{orderId} - Get order by ID
Retrieve detailed information for a specific order.

**Request:**
```
GET /api/v1/orders/aa0e8400-e29b-41d4-a716-446655440005
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "orderId": "aa0e8400-e29b-41d4-a716-446655440005",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "restaurantName": "Pizza Palace",
  "orderItems": [
    {
      "orderItemId": "bb0e8400-e29b-41d4-a716-446655440006",
      "itemId": "770e8400-e29b-41d4-a716-446655440002",
      "itemName": "Margherita Pizza",
      "quantity": 2,
      "unitPrice": 12.99,
      "totalPrice": 25.98
    }
  ],
  "deliveryAddress": "123 Main St, City",
  "subtotal": 25.98,
  "tax": 2.60,
  "deliveryCharges": 3.00,
  "total": 31.58,
  "orderStatus": "PENDING",
  "paymentStatus": "PAID",
  "paymentMethod": "CREDIT_CARD",
  "estimatedDeliveryTime": 1713273600000,
  "createdAt": 1713272900000,
  "deliveredAt": null
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

### GET / - Get user's orders (paginated)
Retrieve all orders for the authenticated user with optional filters.

**Request:**
```
GET /api/v1/orders?page=0&size=10&status=DELIVERED
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "orderId": "aa0e8400-e29b-41d4-a716-446655440005",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
      "restaurantName": "Pizza Palace",
      "deliveryAddress": "123 Main St, City",
      "subtotal": 25.98,
      "tax": 2.60,
      "deliveryCharges": 3.00,
      "total": 31.58,
      "orderStatus": "DELIVERED",
      "paymentStatus": "PAID",
      "createdAt": 1713272900000,
      "deliveredAt": 1713273900000
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

**Query Parameters:**
- `page`: Page number (0-indexed), default: 0
- `size`: Page size, default: 10
- `status`: Optional filter by order status (PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

### PUT /{orderId}/cancel - Cancel order
Cancel a pending order and request refund.

**Request:**
```json
{
  "cancellationReason": "Changed my mind"
}
```

**Response (200 OK):**
```json
{
  "orderId": "aa0e8400-e29b-41d4-a716-446655440005",
  "originalStatus": "PENDING",
  "newStatus": "CANCELLED",
  "cancellationReason": "Changed my mind",
  "refundAmount": 31.58,
  "cancelledAt": 1713273000000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Note:** Only pending orders can be cancelled

---

### PATCH /{orderId}/status - Update order status (Admin only)
Update order status. Requires ADMIN role.

**Request:**
```json
{
  "newStatus": "DELIVERED"
}
```

**Response (200 OK):**
```json
{
  "orderId": "aa0e8400-e29b-41d4-a716-446655440005",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "restaurantId": "660e8400-e29b-41d4-a716-446655440001",
  "restaurantName": "Pizza Palace",
  "orderItems": [
    {
      "orderItemId": "bb0e8400-e29b-41d4-a716-446655440006",
      "itemId": "770e8400-e29b-41d4-a716-446655440002",
      "itemName": "Margherita Pizza",
      "quantity": 2,
      "unitPrice": 12.99,
      "totalPrice": 25.98
    }
  ],
  "deliveryAddress": "123 Main St, City",
  "subtotal": 25.98,
  "tax": 2.60,
  "deliveryCharges": 3.00,
  "total": 31.58,
  "orderStatus": "DELIVERED",
  "paymentStatus": "PAID",
  "paymentMethod": "CREDIT_CARD",
  "estimatedDeliveryTime": 1713273600000,
  "createdAt": 1713272900000,
  "deliveredAt": 1713273900000
}
```

**Request Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Valid Status Values:**
- PENDING
- CONFIRMED
- PREPARING
- OUT_FOR_DELIVERY
- DELIVERED
- CANCELLED

**Permissions:** Admin only (ROLE_ADMIN)

---

## Error Responses

All endpoints may return error responses with the following format:

**400 Bad Request:**
```json
{
  "timestamp": 1713272900000,
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input provided",
  "path": "/api/v1/auth/login"
}
```

**401 Unauthorized:**
```json
{
  "timestamp": 1713272900000,
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required or token expired",
  "path": "/api/v1/users/profile"
}
```

**403 Forbidden:**
```json
{
  "timestamp": 1713272900000,
  "status": 403,
  "error": "Forbidden",
  "message": "Admin access required",
  "path": "/api/v1/restaurants"
}
```

**404 Not Found:**
```json
{
  "timestamp": 1713272900000,
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/v1/orders/invalid-id"
}
```

**500 Internal Server Error:**
```json
{
  "timestamp": 1713272900000,
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/v1/users/profile"
}
```

---

## Authentication

### JWT Token Format
Tokens are JWT (JSON Web Tokens) that need to be included in the Authorization header:

```
Authorization: Bearer <token>
```

### Token Validity
- **Expiration:** 24 hours (86400000 milliseconds)
- **Refresh:** Not supported - user must login again

---

## Key Notes

- **Base URL:** `http://localhost:8080`
- **All timestamps:** Unix milliseconds
- **Prices:** BigDecimal format (e.g., 12.99)
- **Pagination:** Default size varies by endpoint
- **Admin Routes:** Require `ROLE_ADMIN` role
- **Protected Routes:** Require valid JWT token
- **Content-Type:** All POST/PUT/PATCH requests require `application/json`

---

## Testing Workflow Example

1. **Sign up user:**
   ```
   POST /api/v1/auth/signup
   ```

2. **Login user:**
   ```
   POST /api/v1/auth/login
   ```
   - Save the returned `token`

3. **View restaurants:**
   ```
   GET /api/v1/restaurants
   ```

4. **View restaurant menu:**
   ```
   GET /api/v1/restaurants/{restaurantId}/menu-items
   ```

5. **Initialize cart:**
   ```
   POST /api/v1/cart
   ```

6. **Add items to cart:**
   ```
   POST /api/v1/cart/items
   ```

7. **View cart:**
   ```
   GET /api/v1/cart
   ```

8. **Create order:**
   ```
   POST /api/v1/orders
   ```

9. **View orders:**
   ```
   GET /api/v1/orders
   ```

---

**Last Updated:** April 16, 2026
