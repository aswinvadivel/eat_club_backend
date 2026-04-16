-- ============================================================
-- EatClub - Database Schema and Setup Script
-- Tech Stack: MySQL 8.0+
-- ============================================================

-- Create Database
CREATE DATABASE IF NOT EXISTS eatclub_db;
USE eatclub_db;

-- ============================================================
-- 1. Users Table
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
  userId VARCHAR(36) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('USER', 'ADMIN') DEFAULT 'USER' NOT NULL,
  phoneNumber VARCHAR(20),
  address TEXT,
  profileImageUrl VARCHAR(500),
  isActive BOOLEAN DEFAULT TRUE,
  lastLoginAt BIGINT NULL,
  createdAt BIGINT NOT NULL,
  updatedAt BIGINT NOT NULL,
  INDEX idx_email (email),
  INDEX idx_role (role),
  INDEX idx_isActive (isActive)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 2. Restaurants Table
-- ============================================================
CREATE TABLE IF NOT EXISTS restaurants (
  restaurantId VARCHAR(36) PRIMARY KEY,
  adminId VARCHAR(36) NOT NULL,
  restaurantName VARCHAR(255) NOT NULL,
  description TEXT,
  address TEXT NOT NULL,
  phoneNumber VARCHAR(20),
  cuisineType VARCHAR(100),
  imageUrl VARCHAR(500),
  isActive BOOLEAN DEFAULT TRUE,
  createdAt BIGINT NOT NULL,
  updatedAt BIGINT NOT NULL,
  FOREIGN KEY (adminId) REFERENCES users(userId) ON DELETE CASCADE,
  INDEX idx_adminId (adminId),
  INDEX idx_restaurantName (restaurantName),
  INDEX idx_isActive (isActive),
  INDEX idx_cuisineType (cuisineType)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 3. Menu Items Table
-- ============================================================
CREATE TABLE IF NOT EXISTS menu_items (
  itemId VARCHAR(36) PRIMARY KEY,
  restaurantId VARCHAR(36) NOT NULL,
  itemName VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10, 2) NOT NULL,
  category VARCHAR(100),
  isVegetarian BOOLEAN DEFAULT FALSE,
  isSpicy BOOLEAN DEFAULT FALSE,
  preparationTime INT DEFAULT 20,
  imageUrl VARCHAR(500),
  isAvailable BOOLEAN DEFAULT TRUE,
  createdAt BIGINT NOT NULL,
  updatedAt BIGINT NOT NULL,
  FOREIGN KEY (restaurantId) REFERENCES restaurants(restaurantId) ON DELETE CASCADE,
  INDEX idx_restaurantId (restaurantId),
  INDEX idx_category (category),
  INDEX idx_isAvailable (isAvailable),
  INDEX idx_isVegetarian (isVegetarian)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 4. Cart Table
-- ============================================================
CREATE TABLE IF NOT EXISTS carts (
  cartId VARCHAR(36) PRIMARY KEY,
  userId VARCHAR(36) UNIQUE NOT NULL,
  restaurantId VARCHAR(36),
  createdAt BIGINT NOT NULL,
  updatedAt BIGINT NOT NULL,
  FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
  FOREIGN KEY (restaurantId) REFERENCES restaurants(restaurantId) ON DELETE SET NULL,
  INDEX idx_userId (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 5. Cart Items Table
-- ============================================================
CREATE TABLE IF NOT EXISTS cart_items (
  cartItemId VARCHAR(36) PRIMARY KEY,
  cartId VARCHAR(36) NOT NULL,
  itemId VARCHAR(36) NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  specialInstructions TEXT,
  addedAt BIGINT NOT NULL,
  FOREIGN KEY (cartId) REFERENCES carts(cartId) ON DELETE CASCADE,
  FOREIGN KEY (itemId) REFERENCES menu_items(itemId) ON DELETE CASCADE,
  INDEX idx_cartId (cartId),
  INDEX idx_itemId (itemId),
  UNIQUE KEY unique_cart_item (cartId, itemId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 6. Orders Table
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
  orderId VARCHAR(36) PRIMARY KEY,
  userId VARCHAR(36) NOT NULL,
  restaurantId VARCHAR(36) NOT NULL,
  deliveryAddress TEXT NOT NULL,
  phoneNumber VARCHAR(20) NOT NULL,
  subtotal DECIMAL(10, 2) NOT NULL,
  tax DECIMAL(10, 2) NOT NULL DEFAULT 0,
  deliveryCharges DECIMAL(10, 2) NOT NULL DEFAULT 50.00,
  discountAmount DECIMAL(10, 2) DEFAULT 0,
  total DECIMAL(10, 2) NOT NULL,
  orderStatus ENUM('CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED') DEFAULT 'CONFIRMED',
  paymentStatus ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
  paymentMethod VARCHAR(50),
  specialInstructions TEXT,
  estimatedDeliveryTime BIGINT,
  deliveredAt BIGINT NULL,
  cancelledAt BIGINT NULL,
  cancellationReason VARCHAR(255),
  createdAt BIGINT NOT NULL,
  updatedAt BIGINT NOT NULL,
  FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE RESTRICT,
  FOREIGN KEY (restaurantId) REFERENCES restaurants(restaurantId) ON DELETE RESTRICT,
  INDEX idx_userId (userId),
  INDEX idx_restaurantId (restaurantId),
  INDEX idx_orderStatus (orderStatus),
  INDEX idx_createdAt (createdAt),
  INDEX idx_orderId (orderId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 7. Order Items Table
-- ============================================================
CREATE TABLE IF NOT EXISTS order_items (
  orderItemId VARCHAR(36) PRIMARY KEY,
  orderId VARCHAR(36) NOT NULL,
  itemId VARCHAR(36) NOT NULL,
  itemName VARCHAR(255) NOT NULL,
  quantity INT NOT NULL,
  unitPrice DECIMAL(10, 2) NOT NULL,
  totalPrice DECIMAL(10, 2) NOT NULL,
  specialInstructions TEXT,
  createdAt BIGINT NOT NULL,
  FOREIGN KEY (orderId) REFERENCES orders(orderId) ON DELETE CASCADE,
  FOREIGN KEY (itemId) REFERENCES menu_items(itemId) ON DELETE RESTRICT,
  INDEX idx_orderId (orderId),
  INDEX idx_itemId (itemId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- MOCK DATA FOR TESTING
-- ============================================================

-- Epoch millisecond timestamps for: 2024-04-16 10:00:00 UTC = 1713265200000
-- Using incremental timestamps for test data

-- ============================================================
-- Insert Test Users (2 users, 2 admins)
-- Passwords are hashed (for demo, use actual bcrypt hashes in production)
-- ============================================================

INSERT INTO users (userId, name, email, password, role, phoneNumber, address, isActive, createdAt, updatedAt) VALUES
('user-001', 'Rajesh Kumar', 'rajesh.kumar@example.com', '$2a$10$xTgLkJoU8bYbAXVYz5Kp2e2l1m1n1o1p1q1r1s1t1u1v1w1x1y1z2', 'USER', '+91-9876543210', '123 Main Street, Chennai, Tamil Nadu 600001', TRUE, 1712200800000, 1712200800000),
('user-002', 'Priya Singh', 'priya.singh@example.com', '$2a$10$xTgLkJoU8bYbAXVYz5Kp2e2l1m1n1o1p1q1r1s1t1u1v1w1x1y1z2', 'USER', '+91-8765432109', '456 Park Avenue, Bangalore, Karnataka 560001', TRUE, 1712287200000, 1712287200000),
('admin-001', 'Arjun Verma', 'arjun.verma@restaurant.com', '$2a$10$xTgLkJoU8bYbAXVYz5Kp2e2l1m1n1o1p1q1r1s1t1u1v1w1x1y1z2', 'ADMIN', '+91-7654321098', '789 Restaurant Row, Chennai, Tamil Nadu 600002', TRUE, 1712373600000, 1712373600000),
('admin-002', 'Neha Gupta', 'neha.gupta@restaurant.com', '$2a$10$xTgLkJoU8bYbAXVYz5Kp2e2l1m1n1o1p1q1r1s1t1u1v1w1x1y1z2', 'ADMIN', '+91-6543210987', '321 Food Court, Bangalore, Karnataka 560002', TRUE, 1712460000000, 1712460000000);

-- ============================================================
-- Insert Restaurants
-- ============================================================

INSERT INTO restaurants (restaurantId, adminId, restaurantName, description, address, phoneNumber, cuisineType, isActive, createdAt, updatedAt) VALUES
('rest-001', 'admin-001', 'Tasty Bites', 'Authentic North Indian cuisine with traditional recipes', '789 Restaurant Row, Chennai, Tamil Nadu 600002', '+91-4434556677', 'North Indian', TRUE, 1712546400000, 1712546400000),
('rest-002', 'admin-002', 'Spice Garden', 'South Indian specialties and traditional dosas', '321 Food Court, Bangalore, Karnataka 560002', '+91-8022334455', 'South Indian', TRUE, 1712632800000, 1712632800000);

-- ============================================================
-- Insert Menu Items for Restaurant 1 (Tasty Bites)
-- ============================================================

INSERT INTO menu_items (itemId, restaurantId, itemName, description, price, category, isVegetarian, isSpicy, preparationTime, isAvailable, createdAt, updatedAt) VALUES
('item-001', 'rest-001', 'Butter Chicken', 'Creamy butter chicken with aromatic spices', 299.99, 'Main Course', FALSE, TRUE, 20, TRUE, 1712719200000, 1712719200000),
('item-002', 'rest-001', 'Tandoori Chicken', 'Grilled chicken with yogurt and spices', 349.99, 'Main Course', FALSE, TRUE, 25, TRUE, 1712805600000, 1712805600000),
('item-003', 'rest-001', 'Paneer Tikka Masala', 'Paneer cheese in creamy tomato sauce', 279.99, 'Main Course', TRUE, FALSE, 18, TRUE, 1712892000000, 1712892000000),
('item-004', 'rest-001', 'Dal Makhani', 'Creamy lentil curry cooked overnight', 229.99, 'Main Course', TRUE, FALSE, 30, TRUE, 1712978400000, 1712978400000),
('item-005', 'rest-001', 'Garlic Naan', 'Soft bread with garlic and butter', 59.99, 'Bread', TRUE, FALSE, 8, TRUE, 1713064800000, 1713064800000),
('item-006', 'rest-001', 'Biryani', 'Fragrant basmati rice with chicken and spices', 349.99, 'Rice', FALSE, TRUE, 35, TRUE, 1713151200000, 1713151200000),
('item-007', 'rest-001', 'Gulab Jamun', 'Sweet milk solids in sugar syrup', 99.99, 'Dessert', TRUE, FALSE, 5, TRUE, 1713237600000, 1713237600000),
('item-008', 'rest-001', 'Mango Lassi', 'Yogurt-based mango drink', 79.99, 'Beverage', TRUE, FALSE, 3, TRUE, 1713324000000, 1713324000000);

-- ============================================================
-- Insert Menu Items for Restaurant 2 (Spice Garden)
-- ============================================================

INSERT INTO menu_items (itemId, restaurantId, itemName, description, price, category, isVegetarian, isSpicy, preparationTime, isAvailable, createdAt, updatedAt) VALUES
('item-009', 'rest-002', 'Masala Dosa', 'Crispy crepe with spiced potato filling', 159.99, 'Main Course', TRUE, TRUE, 15, TRUE, 1713410400000, 1713410400000),
('item-010', 'rest-002', 'Idli Sambar', 'Steamed rice cakes with lentil soup', 119.99, 'Main Course', TRUE, FALSE, 12, TRUE, 1713496800000, 1713496800000),
('item-011', 'rest-002', 'Chicken 65', 'Spicy fried chicken appetizer', 229.99, 'Appetizer', FALSE, TRUE, 15, TRUE, 1713583200000, 1713583200000),
('item-012', 'rest-002', 'Rasam', 'Tangy tamarind and tomato soup', 89.99, 'Soup', TRUE, TRUE, 8, TRUE, 1713669600000, 1713669600000),
('item-013', 'rest-002', 'Chikhalwali', 'Traditional South Indian sweet', 129.99, 'Dessert', TRUE, FALSE, 10, TRUE, 1713756000000, 1713756000000),
('item-014', 'rest-002', 'Filter Coffee', 'South Indian filter coffee', 59.99, 'Beverage', TRUE, FALSE, 5, TRUE, 1713842400000, 1713842400000);

-- ============================================================
-- Insert Test Carts (empty carts for users)
-- ============================================================

INSERT INTO carts (cartId, userId, restaurantId, createdAt, updatedAt) VALUES
('cart-001', 'user-001', NULL, 1713928800000, 1713928800000),
('cart-002', 'user-002', NULL, 1714015200000, 1714015200000);

-- ============================================================
-- Insert Test Orders (for order history)
-- ============================================================

INSERT INTO orders (orderId, userId, restaurantId, deliveryAddress, phoneNumber, subtotal, tax, deliveryCharges, total, orderStatus, paymentStatus, paymentMethod, specialInstructions, createdAt, updatedAt) VALUES
('ORD-20240410-001', 'user-001', 'rest-001', '123 Main Street, Chennai, Tamil Nadu 600001', '+91-9876543210', 599.98, 107.99, 50.00, 757.97, 'DELIVERED', 'COMPLETED', 'CREDIT_CARD', 'Extra spicy', 1712721000000, 1712722500000),
('ORD-20240412-002', 'user-002', 'rest-002', '456 Park Avenue, Bangalore, Karnataka 560001', '+91-8765432109', 379.98, 68.39, 50.00, 498.37, 'DELIVERED', 'COMPLETED', 'DEBIT_CARD', 'No onions', 1712893800000, 1712895300000),
('ORD-20240414-003', 'user-001', 'rest-002', '123 Main Street, Chennai, Tamil Nadu 600001', '+91-9876543210', 459.98, 82.79, 50.00, 592.77, 'OUT_FOR_DELIVERY', 'COMPLETED', 'CREDIT_CARD', NULL, 1713066600000, 1713068100000),
('ORD-20240416-004', 'user-002', 'rest-001', '456 Park Avenue, Bangalore, Karnataka 560001', '+91-8765432109', 749.98, 134.99, 50.00, 934.97, 'CONFIRMED', 'PENDING', 'WALLET', 'Ring bell twice', 1713239400000, 1713239400000);

-- ============================================================
-- Insert Order Items (matching the orders)
-- ============================================================

INSERT INTO order_items (orderItemId, orderId, itemId, itemName, quantity, unitPrice, totalPrice, specialInstructions, createdAt) VALUES
('oitem-001', 'ORD-20240410-001', 'item-001', 'Butter Chicken', 2, 299.99, 599.98, 'Extra spicy', 1712721000000),
('oitem-002', 'ORD-20240412-002', 'item-009', 'Masala Dosa', 2, 159.99, 319.98, 'No onions', 1712893800000),
('oitem-003', 'ORD-20240412-002', 'item-014', 'Filter Coffee', 1, 59.99, 59.99, NULL, 1712893800000),
('oitem-004', 'ORD-20240414-003', 'item-010', 'Idli Sambar', 2, 119.99, 239.98, NULL, 1713066600000),
('oitem-005', 'ORD-20240414-003', 'item-012', 'Rasam', 2, 89.99, 179.98, NULL, 1713066600000),
('oitem-006', 'ORD-20240416-004', 'item-002', 'Tandoori Chicken', 2, 349.99, 699.98, NULL, 1713239400000),
('oitem-007', 'ORD-20240416-004', 'item-005', 'Garlic Naan', 1, 59.99, 59.99, 'Ring bell twice', 1713239400000);

-- ============================================================
-- INDEXES AND STATISTICS (For Performance)
-- ============================================================

-- Analyze tables to update statistics
ANALYZE TABLE users;
ANALYZE TABLE restaurants;
ANALYZE TABLE menu_items;
ANALYZE TABLE carts;
ANALYZE TABLE cart_items;
ANALYZE TABLE orders;
ANALYZE TABLE order_items;

-- ============================================================
-- Test Data Query Examples
-- ============================================================

-- Query 1: Get all restaurants
-- SELECT * FROM restaurants WHERE isActive = TRUE;

-- Query 2: Get menu items for a specific restaurant
-- SELECT * FROM menu_items WHERE restaurantId = 'rest-001' AND isAvailable = TRUE;

-- Query 3: Get all vegetarian menu items
-- SELECT * FROM menu_items WHERE isVegetarian = TRUE AND isAvailable = TRUE;

-- Query 4: Get user's order history
-- SELECT * FROM orders WHERE userId = 'user-001' ORDER BY createdAt DESC;

-- Query 5: Get order details with items
-- SELECT o.*, oi.* FROM orders o 
-- LEFT JOIN order_items oi ON o.orderId = oi.orderId 
-- WHERE o.orderId = 'ORD-20240410-001';

-- Query 6: Get total orders and revenue for a restaurant
-- SELECT restaurantId, COUNT(*) as totalOrders, SUM(total) as totalRevenue 
-- FROM orders WHERE orderStatus = 'DELIVERED' GROUP BY restaurantId;

-- Query 7: Get restaurants managed by an admin
-- SELECT * FROM restaurants WHERE adminId = 'admin-001';

-- ============================================================
-- Test Credentials
-- ============================================================
/*
Regular Users:
Email: rajesh.kumar@example.com
Password: password123

Email: priya.singh@example.com
Password: password123

Admin Users (Restaurant Owners):
Email: arjun.verma@restaurant.com
Password: password123

Email: neha.gupta@restaurant.com
Password: password123

Note: Use bcrypt for password hashing in production
*/
