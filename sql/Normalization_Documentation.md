# Database Normalization Documentation
## Course: 25ITT31 - Database Management Systems (DBMS)
### Project: Pharmacy Stock Management and Medicine Sales Analysis System

---

## 1. Overview of Normalization

Normalization is the systematic approach of decomposing tables to eliminate data redundancy and undesirable anomalies (Insertion, Update, and Deletion anomalies). This document details how the schema of the Pharmacy Stock Management System was normalized from an unnormalized form (UNF) through First Normal Form (1NF), Second Normal Form (2NF), Third Normal Form (3NF), and Boyce-Codd Normal Form (BCNF).

---

## 2. Unnormalized Form (UNF)

In an unnormalized state, a single transaction record could store customer, medicine, supplier, quantity, and purchase/sale details together with repeating groups:

```text
PHARMACY_RECORD(TransID, Date, TransType, SupplierID, SupplierName, SupplierPhone, 
                MedicineID, MedName, Category, Manufacturer, UnitPrice, 
                Quantity, TotalAmount, ExpiryDate, StockQty)
```

**Anomalies in UNF:**
- **Repeating Groups**: Multiple medicines purchased or sold under one transaction.
- **Redundancy**: Repeating supplier name, phone, medicine name, manufacturer for every single sale.
- **Update Anomaly**: Changing a supplier's phone requires editing hundreds of purchase records.
- **Insertion Anomaly**: Cannot record a new medicine unless it has an active purchase/sale transaction.
- **Deletion Anomaly**: Deleting the only sale of a medicine erases all information about that medicine.

---

## 3. First Normal Form (1NF)

**Definition**: A relation is in 1NF if and only if all underlying domains contain only atomic (indivisible) values, and there are no repeating groups.

**Decomposition to 1NF:**
Repeating groups of medicines within a purchase/sale are separated into distinct transaction item relations with composite keys:
- Separate tables for `Purchases` and `Purchase_Items`.
- Separate tables for `Sales` and `Sale_Items`.
- Each attribute holds a single atomic value (e.g., `phone`, `unit_price`, `quantity`).

---

## 4. Second Normal Form (2NF)

**Definition**: A relation is in 2NF if and only if it is in 1NF and every non-prime attribute is fully functionally dependent on the entire primary key (no partial functional dependencies).

**Analysis of Functional Dependencies:**
In `Purchase_Items(purchase_id, medicine_id, ...)`:
- Composite Key: `{purchase_id, medicine_id}`
- `medicine_name`, `category`, `unit_price` depend only on `medicine_id`, NOT the full composite key `{purchase_id, medicine_id}`.
- This was a partial dependency: `medicine_id -> {medicine_name, category, unit_price}`.

**Decomposition into 2NF:**
1. Extract medicine details into `MEDICINE(medicine_id, name, category, manufacturer, unit_price, expiry_date, reorder_level)`.
2. Keep only transactional line-item attributes in `PURCHASE_ITEM(purchase_item_id, purchase_id, medicine_id, batch_no, expiry_date, quantity, purchase_price)`.
3. Extract supplier details into `SUPPLIER(supplier_id, supplier_name, phone, email, address)`.

Result: All non-prime attributes are fully functionally dependent on their table's primary key.

---

## 5. Third Normal Form (3NF)

**Definition**: A relation is in 3NF if and only if it is in 2NF and there is no transitive dependency of non-prime attributes on the primary key (no $X \rightarrow Y$ and $Y \rightarrow Z$ where $Z$ is a non-prime attribute).

**Analysis:**
- In `PURCHASE(purchase_id, supplier_id, supplier_name, supplier_phone)`:
  - `purchase_id -> supplier_id`
  - `supplier_id -> {supplier_name, supplier_phone}`
  - Transitive dependency: `purchase_id -> {supplier_name, supplier_phone}`.
- In `SALE_ITEM(sale_item_id, sale_id, medicine_id, unit_price, quantity_sold, subtotal)`:
  - `subtotal` is derived via calculation (`quantity_sold * selling_price`). Stored explicitly only for finalized invoice integrity.
- By isolating `SUPPLIER` and referencing only foreign key `supplier_id` in `PURCHASE`, all transitive dependencies are eliminated.

Resulting 3NF Relations:
- `SUPPLIERS(supplier_id PK, supplier_name, phone, email, address)`
- `MEDICINES(medicine_id PK, name, category, manufacturer, unit_price, expiry_date, reorder_level)`
- `STOCK(stock_id PK, medicine_id FK UNIQUE, current_stock, reorder_level, last_updated)`
- `PURCHASES(purchase_id PK, supplier_id FK, purchase_date, total_amount)`
- `PURCHASE_ITEMS(purchase_item_id PK, purchase_id FK, medicine_id FK, batch_no, expiry_date, quantity, purchase_price)`
- `SALES(sale_id PK, sale_date, customer_name, total_amount)`
- `SALE_ITEMS(sale_item_id PK, sale_id FK, medicine_id FK, quantity_sold, selling_price, subtotal)`

---

## 6. Boyce-Codd Normal Form (BCNF)

**Definition**: A relation is in BCNF if and only if for every functional dependency $X \rightarrow Y$, $X$ is a superkey.

**Verification:**
- In `MEDICINES`: Only non-trivial functional dependency is `medicine_id -> {name, category, manufacturer, unit_price, expiry_date, reorder_level}`. Here `medicine_id` is candidate key. Satisfies BCNF.
- In `SUPPLIERS`: `supplier_id -> {supplier_name, phone, email, address}`. `supplier_id` is superkey. Satisfies BCNF.
- In `STOCK`: `stock_id -> {medicine_id, current_stock, reorder_level, last_updated}` and `medicine_id -> {stock_id, current_stock, reorder_level, last_updated}`. Both are candidate keys. Satisfies BCNF.
- In `PURCHASES` and `SALES`: Primary keys are `purchase_id` and `sale_id`. Satisfies BCNF.
- In `PURCHASE_ITEMS` and `SALE_ITEMS`: Primary keys are synthetic unique serial IDs. Satisfies BCNF.

---

## 7. Conclusion

The relational schema achieves complete BCNF normalization. It eliminates data redundancy, guarantees loss-less joins, preserves all functional dependencies, and ensures high operational efficiency for live pharmacy inventory and sales analytics.
