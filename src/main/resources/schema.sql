-- ============================================================
-- FoodApp PostgreSQL Schema
-- Run this in pgAdmin to create all tables and types.
-- ============================================================

-- 1. ENUM TYPES
CREATE TYPE user_type_enum AS ENUM (
    'CUSTOMER',
    'DELIVERY_AGENT',
    'ADMIN'
);

CREATE TYPE delivery_agent_status AS ENUM (
    'AVAILABLE',
    'ON_DELIVERY',
    'UNAVAILABLE'
);

CREATE TYPE payment_mode_enum AS ENUM (
    'CASH',
    'UPI'
);

CREATE TYPE order_status_enum AS ENUM (
    'PLACED',
    'APPROVED',
    'READY_FOR_DELIVERY',
    'OUT_FOR_DELIVERY',
    'DELIVERED'
);

-- 2. USERS (base table)
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    user_type user_type_enum NOT NULL
);

-- 3. CUSTOMER (subtype of users)
CREATE TABLE customer (
    customer_id INT PRIMARY KEY,
    address VARCHAR(500),
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

-- 4. ADMIN (subtype of users)
CREATE TABLE admin (
    admin_id INT PRIMARY KEY,
    FOREIGN KEY (admin_id) REFERENCES users(user_id)
);

-- 5. DELIVERY AGENT (subtype of users)
CREATE TABLE delivery_agent (
    delivery_agent_id INT PRIMARY KEY,
    status delivery_agent_status NOT NULL DEFAULT 'UNAVAILABLE',
    gross_earning NUMERIC(12,2) DEFAULT 0.00,
    base_salary NUMERIC(12,2) DEFAULT 0.00,
    commission_rate NUMERIC(5,4) DEFAULT 0.0000,
    FOREIGN KEY (delivery_agent_id) REFERENCES users(user_id)
);

-- 6. MENU CATEGORY (self-referencing for subcategories)
CREATE TABLE menu_category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    parent_category_id INT,
    FOREIGN KEY (parent_category_id) REFERENCES menu_category(category_id)
);

-- 7. MENU ITEM
CREATE TABLE menu_item (
    item_id SERIAL PRIMARY KEY,
    item_name VARCHAR(200) NOT NULL UNIQUE,
    item_price NUMERIC(10,2) NOT NULL CHECK (item_price > 0),
    category_id INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES menu_category(category_id)
);

-- 8. CART
CREATE TABLE cart (
    cart_id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL UNIQUE,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- 9. CART ITEM
CREATE TABLE cart_item (
    cart_id INT,
    item_id INT,
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (cart_id, item_id),
    FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_item(item_id)
);

-- 10. ORDERS
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    customer_address TEXT NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL CHECK (subtotal > 0),
    discount_amount NUMERIC(12,2) DEFAULT 0.00 CHECK (discount_amount >= 0.00),
    final_amount NUMERIC(12,2) NOT NULL CHECK (final_amount > 0),
    payment_mode payment_mode_enum NOT NULL,
    status order_status_enum NOT NULL DEFAULT 'PLACED',
    assigned_agent_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (assigned_agent_id) REFERENCES delivery_agent(delivery_agent_id)
);

-- 11. ORDER ITEM
CREATE TABLE order_item (
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price > 0.0),
    PRIMARY KEY (order_id, item_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (item_id) REFERENCES menu_item(item_id)
);

-- 12. PAYMENT
CREATE TABLE payment (
    payment_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    amount NUMERIC(12,2) NOT NULL CHECK (amount > 0.0),
    payment_mode payment_mode_enum NOT NULL,
    payment_identifier VARCHAR(100) UNIQUE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- 13. DISCOUNT
CREATE TABLE discount (
    price_threshold NUMERIC(12,2) PRIMARY KEY CHECK (price_threshold > 0.0),
    discount_rate NUMERIC(5,4) NOT NULL CHECK (discount_rate > 0.0)
);
