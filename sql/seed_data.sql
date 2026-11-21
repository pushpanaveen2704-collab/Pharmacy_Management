-- ============================================================
-- PHARMACY STOCK MANAGEMENT AND MEDICINE SALES ANALYSIS SYSTEM
-- Subject: Database Management Systems (DBMS - 25ITT31)
-- Seed Data Script (DML)
-- Current System Reference Date: 2026-11-20
-- ============================================================

-- Insert Suppliers
INSERT INTO suppliers (supplier_id, supplier_name, phone, email, address) VALUES
('S101', 'ABC Medicals', '9876543210', 'orders@abcmedicals.com', '124 Anna Salai, Chennai'),
('S102', 'Sri Pharma', '9876543211', 'contact@sripharma.com', '45 Perundurai Road, Erode'),
('S103', 'Health Distributors', '9876543212', 'info@healthdist.com', '88 Cross Cut Road, Coimbatore'),
('S104', 'MedLife Suppliers', '9876543213', 'sales@medlifesuppliers.com', '12 Brough Road, Erode'),
('S105', 'Apex Pharmaceuticals', '9876543214', 'support@apexpharma.in', '67 Gandhipuram, Coimbatore');

-- Insert Medicines
INSERT INTO medicines (medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level) VALUES
('MED101', 'Paracetamol', 'Tablet', 'Sun Pharma', 25.00, '2026-12-05', 20),
('MED102', 'Amoxicillin', 'Antibiotic', 'Cipla', 60.00, '2026-10-15', 15),
('MED103', 'Cetirizine', 'Allergy', 'Dr. Reddy', 15.00, '2027-05-20', 10),
('MED104', 'Dolo 650', 'Tablet', 'Micro Labs', 30.00, '2027-08-10', 20),
('MED105', 'Azithromycin 500', 'Antibiotic', 'Zydus', 120.00, '2026-12-10', 15),
('MED106', 'Benadryl Syrup', 'Syrup', 'Johnson & Johnson', 85.00, '2027-03-15', 12),
('MED107', 'Metformin 500', 'Diabetes', 'USV', 45.00, '2027-11-20', 25),
('MED108', 'Pantoprazole 40', 'Antacid', 'Alkem', 55.00, '2026-11-28', 15),
('MED109', 'Ibuprofen 400', 'Painkiller', 'Abbott', 35.00, '2026-09-01', 15),
('MED110', 'Vitamin C 500', 'Vitamin', 'Abbott', 40.00, '2027-09-30', 20),
('MED111', 'Atorvastatin 10', 'Cardiac', 'Torrent', 95.00, '2027-04-12', 15),
('MED112', 'Montelukast 10', 'Allergy', 'Sun Pharma', 75.00, '2026-12-18', 10),
('MED113', 'Omeprazole 20', 'Antacid', 'Cipla', 42.00, '2026-08-20', 15),
('MED114', 'Multivitamin Softgels', 'Vitamin', 'Dr. Reddy', 110.00, '2028-01-15', 15),
('MED115', 'Ciprofloxacin 500', 'Antibiotic', 'Bayer', 68.00, '2027-10-05', 15);

-- Insert Stock Records
INSERT INTO stock (medicine_id, current_stock, reorder_level) VALUES
('MED101', 250, 20),
('MED102', 120, 15),
('MED103', 8, 10),
('MED104', 0, 20),
('MED105', 45, 15),
('MED106', 60, 12),
('MED107', 180, 25),
('MED108', 12, 15),
('MED109', 15, 15),
('MED110', 220, 20),
('MED111', 35, 15),
('MED112', 5, 10),
('MED113', 0, 15),
('MED114', 90, 15),
('MED115', 75, 15);

-- Insert Purchases
INSERT INTO purchases (purchase_id, supplier_id, purchase_date, total_amount) VALUES
('PUR101', 'S101', '2026-11-01', 7500.00),
('PUR102', 'S102', '2026-11-05', 9000.00),
('PUR103', 'S103', '2026-11-10', 4500.00),
('PUR104', 'S104', '2026-11-15', 12000.00),
('PUR105', 'S105', '2026-11-18', 5100.00);

-- Insert Purchase Items
INSERT INTO purchase_items (purchase_id, medicine_id, batch_no, expiry_date, quantity, purchase_price) VALUES
('PUR101', 'MED101', 'BAT2601', '2026-12-05', 300, 20.00),
('PUR101', 'MED103', 'BAT2602', '2027-05-20', 50, 12.00),
('PUR102', 'MED102', 'BAT2603', '2026-10-15', 150, 48.00),
('PUR102', 'MED104', 'BAT2604', '2027-08-10', 60, 25.00),
('PUR103', 'MED106', 'BAT2605', '2027-03-15', 50, 70.00),
('PUR103', 'MED107', 'BAT2606', '2027-11-20', 20, 35.00),
('PUR104', 'MED105', 'BAT2607', '2026-12-10', 60, 95.00),
('PUR104', 'MED110', 'BAT2608', '2027-09-30', 150, 32.00),
('PUR105', 'MED114', 'BAT2609', '2028-01-15', 50, 85.00),
('PUR105', 'MED115', 'BAT2610', '2027-10-05', 15, 55.00);

-- Insert Sales
INSERT INTO sales (sale_id, sale_date, customer_name, total_amount) VALUES
('SL101', '2026-11-15', 'Ramesh Kumar', 750.00),
('SL102', '2026-11-16', 'Priya Sundaram', 600.00),
('SL103', '2026-11-17', 'Karthik Raja', 1800.00),
('SL104', '2026-11-18', 'Anand Babu', 940.00),
('SL105', '2026-11-19', 'Deepa Lakshmi', 1250.00),
('SL106', '2026-11-20', 'Suresh Chandran', 2500.00),
('SL107', '2026-11-20', 'Meena Kumari', 850.00),
('SL108', '2026-11-20', 'Vijay Narayanan', 1150.00);

-- Insert Sale Items
INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, selling_price, subtotal) VALUES
('SL101', 'MED101', 20, 25.00, 500.00),
('SL101', 'MED104', 8, 30.00, 240.00),
('SL101', 'MED103', 1, 15.00, 15.00),
('SL102', 'MED104', 20, 30.00, 600.00),
('SL103', 'MED105', 15, 120.00, 1800.00),
('SL104', 'MED106', 4, 85.00, 340.00),
('SL104', 'MED104', 20, 30.00, 600.00),
('SL105', 'MED101', 50, 25.00, 1250.00),
('SL106', 'MED101', 80, 25.00, 2000.00),
('SL106', 'MED110', 10, 40.00, 400.00),
('SL106', 'MED107', 2, 45.00, 90.00),
('SL107', 'MED106', 10, 85.00, 850.00),
('SL108', 'MED114', 10, 110.00, 1100.00),
('SL108', 'MED101', 2, 25.00, 50.00);
