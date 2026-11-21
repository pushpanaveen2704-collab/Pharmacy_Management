-- ============================================================
-- PHARMACY STOCK MANAGEMENT AND MEDICINE SALES ANALYSIS SYSTEM
-- Subject: Database Management Systems (DBMS - 25ITT31)
-- Analytical, Operational, and Transactional SQL Queries
-- ============================================================

-- 1. Real-Time Dashboard Summary Metrics
SELECT 
    (SELECT COUNT(*) FROM medicines) AS total_medicines,
    (SELECT COUNT(*) FROM suppliers) AS total_suppliers,
    (SELECT COUNT(*) FROM purchases) AS total_purchases,
    (SELECT COUNT(*) FROM sales) AS total_sales,
    (SELECT COALESCE(SUM(current_stock), 0) FROM stock) AS total_available_stock,
    (SELECT COALESCE(SUM(quantity_sold), 0) FROM sale_items) AS total_medicines_sold,
    (SELECT COALESCE(SUM(total_amount), 0.0) FROM sales) AS total_revenue;

-- 2. Stock Health Overview
SELECT 
    SUM(CASE WHEN current_stock > reorder_level AND expiry_date >= DATE('now') THEN 1 ELSE 0 END) AS available_count,
    SUM(CASE WHEN current_stock > 0 AND current_stock <= reorder_level THEN 1 ELSE 0 END) AS low_stock_count,
    SUM(CASE WHEN current_stock = 0 THEN 1 ELSE 0 END) AS out_of_stock_count,
    SUM(CASE WHEN expiry_date < DATE('now') THEN 1 ELSE 0 END) AS expired_count,
    SUM(CASE WHEN expiry_date >= DATE('now') AND expiry_date <= DATE('now', '+30 days') THEN 1 ELSE 0 END) AS near_expiry_count
FROM stock s
JOIN medicines m ON s.medicine_id = m.medicine_id;

-- 3. Today's Sales Performance (assuming current date parameter)
SELECT 
    COUNT(*) AS today_transactions,
    COALESCE(SUM(si.quantity_sold), 0) AS today_medicines_sold,
    COALESCE(SUM(s.total_amount), 0.0) AS today_revenue
FROM sales s
LEFT JOIN sale_items si ON s.sale_id = si.sale_id
WHERE s.sale_date = DATE('now');

-- 4. Top Selling Medicines by Quantity and Revenue
SELECT 
    m.medicine_id,
    m.name AS medicine_name,
    m.category,
    SUM(si.quantity_sold) AS total_qty_sold,
    SUM(si.subtotal) AS total_revenue
FROM sale_items si
JOIN medicines m ON si.medicine_id = m.medicine_id
GROUP BY m.medicine_id, m.name, m.category
ORDER BY total_qty_sold DESC;

-- 5. Near Expiry and Expired Medicines Monitor
SELECT 
    m.medicine_id,
    m.name AS medicine_name,
    m.category,
    m.expiry_date,
    s.current_stock,
    CASE 
        WHEN m.expiry_date < DATE('now') THEN 'EXPIRED'
        WHEN m.expiry_date <= DATE('now', '+30 days') THEN 'NEAR EXPIRY'
        ELSE 'VALID'
    END AS expiry_status
FROM medicines m
JOIN stock s ON m.medicine_id = s.medicine_id
WHERE m.expiry_date <= DATE('now', '+30 days')
ORDER BY m.expiry_date ASC;

-- 6. Low Stock Alert Query
SELECT 
    m.medicine_id,
    m.name AS medicine_name,
    s.current_stock,
    s.reorder_level,
    CASE 
        WHEN s.current_stock = 0 THEN 'OUT OF STOCK'
        ELSE 'LOW STOCK'
    END AS stock_status
FROM stock s
JOIN medicines m ON s.medicine_id = m.medicine_id
WHERE s.current_stock <= s.reorder_level
ORDER BY s.current_stock ASC;

-- 7. Supplier-wise Purchase Analysis
SELECT 
    sup.supplier_id,
    sup.supplier_name,
    COUNT(p.purchase_id) AS total_orders,
    COALESCE(SUM(p.total_amount), 0.0) AS total_purchase_value
FROM suppliers sup
LEFT JOIN purchases p ON sup.supplier_id = p.supplier_id
GROUP BY sup.supplier_id, sup.supplier_name
ORDER BY total_purchase_value DESC;

-- 8. Daily Revenue Trend Query
SELECT 
    sale_date,
    COUNT(sale_id) AS transaction_count,
    SUM(total_amount) AS daily_revenue
FROM sales
GROUP BY sale_date
ORDER BY sale_date ASC;
