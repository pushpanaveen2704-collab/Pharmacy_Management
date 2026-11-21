-- ============================================================
-- PHARMACY STOCK MANAGEMENT AND MEDICINE SALES ANALYSIS SYSTEM
-- Subject: Database Management Systems (DBMS - 25ITT31)
-- Schema Definition Script (DDL)
-- ============================================================

-- Drop tables if they exist in reverse order of foreign keys
DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS purchase_items;
DROP TABLE IF EXISTS purchases;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS suppliers;

-- 1. Suppliers Table
CREATE TABLE suppliers (
    supplier_id VARCHAR(20) PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(200)
);

-- 2. Medicines Table
CREATE TABLE medicines (
    medicine_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL CHECK(unit_price > 0),
    expiry_date DATE NOT NULL,
    reorder_level INT NOT NULL DEFAULT 15
);

-- 3. Stock Table
CREATE TABLE stock (
    stock_id INTEGER PRIMARY KEY AUTOINCREMENT,
    medicine_id VARCHAR(20) UNIQUE NOT NULL,
    current_stock INT NOT NULL DEFAULT 0 CHECK(current_stock >= 0),
    reorder_level INT NOT NULL DEFAULT 15,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE
);

-- 4. Purchases Table
CREATE TABLE purchases (
    purchase_id VARCHAR(20) PRIMARY KEY,
    supplier_id VARCHAR(20) NOT NULL,
    purchase_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL CHECK(total_amount >= 0),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE RESTRICT
);

-- 5. Purchase Items Table
CREATE TABLE purchase_items (
    purchase_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    purchase_id VARCHAR(20) NOT NULL,
    medicine_id VARCHAR(20) NOT NULL,
    batch_no VARCHAR(50) NOT NULL,
    expiry_date DATE NOT NULL,
    quantity INT NOT NULL CHECK(quantity > 0),
    purchase_price DECIMAL(10, 2) NOT NULL CHECK(purchase_price > 0),
    FOREIGN KEY (purchase_id) REFERENCES purchases(purchase_id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE RESTRICT
);

-- 6. Sales Table
CREATE TABLE sales (
    sale_id VARCHAR(20) PRIMARY KEY,
    sale_date DATE NOT NULL,
    customer_name VARCHAR(100) DEFAULT 'Walk-in Customer',
    total_amount DECIMAL(10, 2) NOT NULL CHECK(total_amount >= 0)
);

-- 7. Sale Items Table
CREATE TABLE sale_items (
    sale_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    sale_id VARCHAR(20) NOT NULL,
    medicine_id VARCHAR(20) NOT NULL,
    quantity_sold INT NOT NULL CHECK(quantity_sold > 0),
    selling_price DECIMAL(10, 2) NOT NULL CHECK(selling_price > 0),
    subtotal DECIMAL(10, 2) NOT NULL CHECK(subtotal >= 0),
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE RESTRICT
);

-- Performance Indexes
CREATE INDEX idx_med_name ON medicines(name);
CREATE INDEX idx_med_category ON medicines(category);
CREATE INDEX idx_med_expiry ON medicines(expiry_date);
CREATE INDEX idx_sales_date ON sales(sale_date);
CREATE INDEX idx_purchases_date ON purchases(purchase_date);
CREATE INDEX idx_stock_med ON stock(medicine_id);
