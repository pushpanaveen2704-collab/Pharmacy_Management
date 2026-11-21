# Software Requirements Specification (SRS)
## Course: 25ITT33 - Software Engineering (SE)
### Scenario 19: Pharmacy Stock Management and Medicine Sales Analysis System

---

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification (SRS) document details the functional, non-functional, and interface requirements for the **Pharmacy Stock Management and Medicine Sales Analysis System**. The primary objective is to replace manual pharmacy record-keeping with an automated, robust, centralized console-driven management and data analysis system.

### 1.2 Scope
The system provides end-to-end management for retail and wholesale pharmacy operations:
- Registration and lifecycle management of medicines with expiry tracking.
- Supplier relationship and procurement tracking.
- Automatic inventory reconciliation upon purchase and sale transactions.
- Automated safety checks: preventing sales of expired drugs and preventing out-of-stock dispensing.
- Multi-dimensional analytical reporting (daily, weekly, monthly, top-selling, revenue analysis).
- Seamless integration with Python for data analytics, statistical modeling, and visualization.

### 1.3 Definitions, Acronyms, and Abbreviations
- **DBMS**: Database Management System
- **CMD**: Windows Command Prompt Console Interface
- **FIFO**: First-In, First-Out inventory strategy
- **API**: Application Programming Interface
- **EDA**: Exploratory Data Analysis
- **FDS**: Foundations of Data Science

---

## 2. Overall Description

### 2.1 Product Perspective
The application follows a modular, 3-tier architectural model:
1. **Presentation Layer**: Menu-driven Windows CMD interactive interface with ASCII tabular styling.
2. **Business Logic Layer**: Core Java application implementing validation rules, ACID transactional controls, and reporting.
3. **Data Layer**: Relational Database Management System maintaining relational tables, foreign key constraints, and performance indexes.
4. **Analytics Layer**: Python analytical module utilizing Pandas and Matplotlib for statistical analysis and visualization.

### 2.2 User Classes and Characteristics
- **Administrator**: Full system privileges, database diagnostics, user/supplier maintenance.
- **Pharmacist**: Manages medicine records, expiry monitoring, inventory replenishment, and purchase orders.
- **Sales Staff**: Operates sales billing, searches medicines, validates stock availability, issues itemized receipts.
- **Data Analyst**: Executes Python analytical scripts, studies revenue trends, category distributions, and inventory turns.

### 2.3 Operating Environment
- Operating System: Microsoft Windows 10 / 11
- Java Runtime: OpenJDK / Oracle JDK 17+
- Database: Embedded SQLite / JDBC 4.2+ compliant RDBMS
- Python Runtime: Python 3.7+ with Matplotlib, Pandas, NumPy

---

## 3. Specific System Requirements

### 3.1 Functional Requirements

#### FR-1: Medicine Management
- The system shall allow registering medicines with unique `medicine_id`, name, category, manufacturer, unit price, and expiry date.
- The system shall support updating medicine prices and reorder levels.
- The system shall support searching medicines by ID or substring matching on name.
- The system shall support deleting medicines with foreign-key dependency verification.

#### FR-2: Supplier Management
- The system shall register suppliers with unique `supplier_id`, name, contact number, email, and address.
- The system shall list all suppliers and allow updates to contact details.

#### FR-3: Purchase Management
- The system shall record purchases linked to valid suppliers and medicines.
- Upon recording a purchase, the system shall atomically increment the available stock for that medicine.
- The system shall validate that purchase quantities and costs are strictly positive numbers.

#### FR-4: Stock Management
- The system shall monitor real-time stock levels.
- The system shall categorize medicines into:
  - **AVAILABLE**: Current stock > Reorder level.
  - **LOW STOCK**: 0 < Current stock <= Reorder level.
  - **OUT OF STOCK**: Current stock = 0.
- The system shall support manual stock reconciliation and adjustments with logged reasons.

#### FR-5: Medicine Sales Processing
- The system shall check available quantity prior to processing any sale.
- The system shall reject sales of expired drugs with a clear warning alert.
- The system shall reject sales if requested quantity exceeds available stock.
- Upon completing a sale, the system shall decrement available stock atomically and generate an itemized sales receipt.

#### FR-6: Expiry Surveillance
- The system shall compare medicine expiry dates against current system date.
- The system shall flag medicines as **EXPIRED** (expiry date < current date) or **NEAR EXPIRY** (expiry within 30 days).

#### FR-7: Real-Time CMD Dashboard
- The system shall feature a live, dynamic console dashboard accessible from the main menu.
- The dashboard shall compute overall totals, inventory health, daily sales, top-selling medicine, and critical alerts dynamically from the database.

#### FR-8: Python Data Analysis
- The system shall integrate a Python data analytics module to preprocess database records and generate graphical visualizations (sales trends, category breakdown, stock levels, top-selling items).

---

## 4. Non-Functional Requirements

### 4.1 Performance & Scalability
- Sub-second response time (< 500ms) for all console search, stock check, and transaction commits.
- Capability to handle 10,000+ medicine records and 100,000+ sales transactions without degradation.

### 4.2 Reliability & Transactional Integrity
- Zero data loss: All multi-table updates (sale + stock decrement, purchase + stock increment) must execute within atomic database transactions (`setAutoCommit(false)` with explicit `commit()` and `rollback()` on exceptions).

### 4.3 Usability & Console Ergonomics
- Clean Windows CMD interface with aligned headers, formatted currency (`Rs. XX.XX`), and standardized date formats (`YYYY-MM-DD`).
- Robust input handling: Resilient against non-numeric characters, blank inputs, and illegal menu choices without application crashes.
- Dual-logging architecture: Real-time interactive console operation while automatically capturing logs into corresponding `_Output.txt` files.
