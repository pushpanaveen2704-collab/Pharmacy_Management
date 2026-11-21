# Pharmacy Stock Management and Medicine Sales Analysis System
### Scenario 19 | Velalar College of Engineering and Technology, Erode - 12
**Department of Information Technology**  
**Academic Year**: 2026 - 2027 (ODD) | **Class**: 25IT3A&B  
**Activity**: Project-Based Assessment

---

## 1. Project Overview

The **Pharmacy Stock Management and Medicine Sales Analysis System** is an enterprise-grade, menu-driven Windows Command Prompt (CMD) software solution designed for retail and wholesale pharmaceutical dispensing. It automates medicine inventory tracking, supplier procurement, batch-level expiry monitoring, POS retail sales, and statistical sales analytics.

The repository is modularized into four academic subject folders matching the curriculum:
1. **DBMS (25ITT31)**: Relational database design, ER Diagram, BCNF normalization, transactional integrity, and SQL scripts.
2. **JAVA (25ITT32)**: Enterprise Java application architecture, MVC/DAO design pattern, interactive CMD console, live dynamic dashboard, and automated dual-logging.
3. **SE (25ITT33)**: IEEE 830 compliant SRS document, system architecture, DFDs, UML diagrams (Use Case, Class, Sequence, Activity), and comprehensive test execution matrices.
4. **FDS (25ITC31)**: Foundations of Data Science Python module for exploratory data analysis (EDA), dataset preprocessing, statistical modeling, and visual insights.

---

## 2. Subject Folder Structure

```text
Pharmacy Stock Management/
├── DBMS/                                  <-- Subject 1: Database Management Systems (25ITT31)
│   ├── schema.sql                         <-- DDL: Table creation, constraints, indexes
│   ├── seed_data.sql                      <-- DML: Pre-populated sample records
│   ├── queries.sql                        <-- Analytical, operational & diagnostic queries
│   ├── pharmacy.db                        <-- Embedded SQLite database engine
│   ├── ER_Diagram_and_Relational_Model.md <-- Complete ER mapping & foreign key documentation
│   └── Normalization_Documentation.md     <-- 1NF to BCNF normalization breakdown
│
├── JAVA/                                  <-- Subject 2: Java Programming (25ITT32)
│   ├── src/com/pharmacy/
│   │   ├── model/                         <-- Entity POJOs (Medicine, Supplier, Purchase, Sale, Stock, etc.)
│   │   ├── dao/                           <-- JDBC DAO layer with ACID transactions (AutoCommit=false)
│   │   ├── service/                       <-- Business logic, safety rules, validation
│   │   ├── util/                          <-- Dual-logging TeePrintStream, ConsoleUtil, CurrencyUtil
│   │   └── main/                          <-- Main central entry point + 9 module launchers
│   ├── lib/                               <-- SQLite JDBC driver & SLF4J libraries
│   ├── bin/                               <-- Compiled .class bytecode
│   ├── PharmacyManagement.bat             <-- Main centralized CMD launcher
│   ├── MedicineManagement.bat             <-- Standalone module launcher
│   ├── SupplierManagement.bat
│   ├── PurchaseManagement.bat
│   ├── StockManagement.bat
│   ├── SalesManagement.bat
│   ├── PharmacyAnalysis.bat
│   ├── ExpiryManagement.bat
│   ├── PythonAnalysis.bat
│   ├── DatabaseManagement.bat
│   └── *_Output.txt                       <-- Dual-logged execution output files
│
├── SE/                                    <-- Subject 3: Software Engineering (25ITT33)
│   ├── SRS_Document.md                    <-- IEEE 830 Software Requirements Specification
│   ├── System_Architecture_and_Design.md  <-- Mermaid Architecture, DFD Level 0/1, UML diagrams
│   └── Software_Testing_and_Test_Cases.md <-- Unit, integration, system, boundary test cases
│
├── FDS/                                   <-- Subject 4: Foundations of Data Science (25ITC31)
│   ├── data_analysis.py                   <-- Python data extraction, preprocessing & analysis
│   ├── requirements.txt                   <-- Pandas, Matplotlib, Seaborn, NumPy dependencies
│   ├── dataset/                           <-- Extracted CSV tables (medicines, sales, stock, etc.)
│   ├── visualizations/                    <-- Generated SVG / PNG data charts & trend graphs
│   └── output/                            <-- Statistical summaries & EDA metrics
│
├── PharmacyManagement.bat                 <-- Root launcher for double-click execution
├── [Module].bat                           <-- 10 Standalone batch files for double-click execution
└── [Module]_Output.txt                    <-- 10 Verified execution transcripts
```

---

## 3. Technology Stack

- **Java**: OpenJDK 17 LTS (Object-Oriented Programming, JDBC transactions, Streams, Collections)
- **DBMS**: SQLite 3 (Zero-configuration, ACID compliant, portable embedded relational storage)
- **Python**: Python 3.7+ (Data preprocessing, descriptive statistics, Matplotlib/SVG graphical visualization)
- **OS Platform**: Microsoft Windows Command Prompt (CMD)

---

## 4. Execution Instructions

### Option A: Centralized Interactive Launcher (Recommended)
Double-click `PharmacyManagement.bat` in the root directory (or in `JAVA/`).
This launches the central application with the complete menu:
```text
C:\PharmacySystem> java PharmacyManagementSystem

============================================================
PHARMACY MANAGEMENT SYSTEM

         PHARMACY STOCK & SALES ANALYSIS
============================================================

                MAIN MENU

1. Dashboard
2. Medicine Management
3. Supplier Management
4. Purchase Management
5. Stock Management
6. Medicine Sales Management
7. Sales Analysis & Reports
8. Expiry Management
9. Python Data Analysis
10. Database Management
11. Exit

============================================================
Enter your choice: 1
```

### Option B: Standalone Module Execution
You can launch any module directly by double-clicking its respective `.bat` file:
1. `PharmacyManagement.bat` -> Central Main Menu & Live Real-Time Dashboard
2. `MedicineManagement.bat` -> Add, search, view, update, delete medicines
3. `SupplierManagement.bat` -> Add, search, view, update, delete suppliers
4. `PurchaseManagement.bat` -> Record purchases with automatic inventory increment
5. `StockManagement.bat` -> Real-time stock levels, low-stock & out-of-stock audits
6. `SalesManagement.bat` -> Point-of-Sale (POS) billing with safety checks
7. `PharmacyAnalysis.bat` -> Revenue reports, daily/weekly/monthly trends, top selling medicines
8. `ExpiryManagement.bat` -> Real-time quarantine of expired drugs and near-expiry surveillance
9. `PythonAnalysis.bat` -> Python EDA pipelines, statistical charts, and SVG/PNG generation
10. `DatabaseManagement.bat` -> Live JDBC connection test and table storage metrics

---

## 5. Key System Features & Business Logic

1. **Live Database Dashboard**:
   - Computes dynamic counts directly from relational tables.
   - Shows real-time sales revenue, inventory counts, top-selling drug, and critical warnings.
   - Never uses mock or hardcoded numbers.
2. **Patient Safety Rules**:
   - Prevents sales of expired medicines with warning alerts.
   - Validates quantity before billing to ensure inventory is never negative.
3. **Automated Stock Synchronization**:
   - Purchases automatically increment stock atomically.
   - Sales automatically decrement stock atomically.
4. **Dual-Logging Output Architecture**:
   - Standard output is displayed live in Windows CMD for full user interactivity.
   - All console prompts, user selections, table views, and transactions are simultaneously recorded into corresponding `*_Output.txt` audit files.
5. **Python Visualization Fallback**:
   - Python script generates Matplotlib PNG visualizations if installed.
   - Natively renders ASCII/Unicode bar charts in CMD and exports clean standalone SVG charts without requiring external pip dependencies.

---

## 6. Generated Output Text Files

Each module generates its dedicated log file preserving interactive user sessions:
- `PharmacyManagement_Output.txt`
- `MedicineManagement_Output.txt`
- `SupplierManagement_Output.txt`
- `PurchaseManagement_Output.txt`
- `StockManagement_Output.txt`
- `SalesManagement_Output.txt`
- `PharmacyAnalysis_Output.txt`
- `ExpiryManagement_Output.txt`
- `PythonAnalysis_Output.txt`
- `DatabaseManagement_Output.txt`

---

## 7. College Project Submission Compliance

This project satisfies all requirements outlined in the **Velalar College of Engineering and Technology (VCET)** project-based assessment guideline:
- SRS Document, Design Diagrams, and Testing Documentation under `SE/`.
- Normalized relational database, ER diagram, and SQL scripts under `DBMS/`.
- Robust Java source code, batch scripts, and verified execution transcripts under `JAVA/`.
- Python data extraction, preprocessing, statistical analysis, and visual charts under `FDS/`.
