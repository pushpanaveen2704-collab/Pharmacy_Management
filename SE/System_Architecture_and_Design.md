# System Architecture and Design Document
## Course: 25ITT33 - Software Engineering (SE)
### Project: Pharmacy Stock Management and Medicine Sales Analysis System

---

## 1. System Architecture Overview

The system employs a 4-Tier Architectural Pattern:
1. **User Role Layer**: Admin, Pharmacist, Sales Staff, Supplier, Data Analyst.
2. **Presentation Layer**: Windows CMD Interactive Console Interface with ASCII Table Formatting and ANSI-compliant styling.
3. **Application & Business Logic Layer**: Java DAO/Service Pattern managing business rules, transactions, and report formatting.
4. **Data & Analytics Layer**: Relational Database Engine (SQLite / PostgreSQL / MySQL) + Python Data Analytics Engine (Pandas & Matplotlib).

```mermaid
flowchart TD
    subgraph Users ["Users"]
        U1["Admin"]
        U2["Pharmacist"]
        U3["Sales Staff"]
        U4["Data Analyst"]
    end

    subgraph Presentation ["Presentation Layer (Windows CMD Interface)"]
        UI["Centralized Menu-Driven Console (PharmacyManagementSystem)"]
    end

    subgraph Application ["Application Layer (Java Business Logic)"]
        M1["Medicine Management"]
        M2["Supplier & Purchase Management"]
        M3["Stock & Expiry Management"]
        M4["Sales Processing & Billing"]
        M5["Report Generation Engine"]
        M6["Python Analytics Bridge"]
    end

    subgraph Persistence ["Data & Analytics Layer"]
        DB[("Relational Database (pharmacy.db)")]
        PY["Python Engine (Pandas, Matplotlib)"]
    end

    subgraph Outputs ["Output Layer"]
        O1["Live Console Dashboard"]
        O2["Formatted Audit Logs (*_Output.txt)"]
        O3["Statistical Visualizations (PNG / SVG Charts)"]
    end

    Users --> Presentation
    Presentation --> Application
    Application --> Persistence
    Persistence --> Outputs
```

---

## 2. Use Case Diagram

```mermaid
flowchart LR
    Admin(["Admin"])
    Pharmacist(["Pharmacist"])
    SalesStaff(["Sales Staff"])
    DataAnalyst(["Data Analyst"])

    subgraph SystemBoundary ["Pharmacy Stock & Sales System"]
        UC1(["Manage Medicine Details"])
        UC2(["Manage Supplier Details"])
        UC3(["Record Medicine Purchases"])
        UC4(["Update & Reconcile Stock"])
        UC5(["Process Medicine Sales"])
        UC6(["Check Stock Availability"])
        UC7(["Monitor Expiry Dates"])
        UC8(["Generate Stock Reports"])
        UC9(["Generate Daily Sales Reports"])
        UC10(["Analyze Sales Trends (Python)"])
        UC11(["View Real-Time Dashboard"])
    end

    Admin --> UC1
    Admin --> UC2
    Admin --> UC8
    Admin --> UC11

    Pharmacist --> UC1
    Pharmacist --> UC3
    Pharmacist --> UC4
    Pharmacist --> UC6
    Pharmacist --> UC7
    Pharmacist --> UC8
    Pharmacist --> UC11

    SalesStaff --> UC5
    SalesStaff --> UC6
    SalesStaff --> UC9
    SalesStaff --> UC11

    DataAnalyst --> UC8
    DataAnalyst --> UC9
    DataAnalyst --> UC10
    DataAnalyst --> UC11
```

---

## 3. Data Flow Diagram (DFD)

### 3.1 DFD Level 0 (Context Diagram)

```mermaid
flowchart LR
    Admin["Admin"] -->|Medicine & Supplier Info| System["Pharmacy Stock Management System"]
    Pharmacist["Pharmacist"] -->|Purchase & Stock Updates| System
    SalesStaff["Sales Staff"] -->|Sales Inquiries & Orders| System
    System -->|Bills & Receipts| SalesStaff
    System -->|Alerts & Reports| Pharmacist
    System -->|Audit Summaries| Admin
    System -->|Raw Datasets| Analyst["Data Analyst"]
    Analyst -->|Analytics Configuration| System
    System -->|Visual Insights & Charts| Analyst
```

### 3.2 DFD Level 1 (Decomposition)

```mermaid
flowchart TD
    Admin["Admin"] -->|Input Medicine Details| P1["1.0 Manage Medicines"]
    P1 -->|Store/Update Medicine| D1[("D1: Medicines Store")]

    Supplier["Supplier"] -->|Invoice & Dispatch Details| P2["2.0 Manage Supplier & Purchases"]
    P2 -->|Record Purchase| D2[("D2: Purchases Store")]
    P2 -->|Increment Inventory| D3[("D3: Stock Store")]

    Pharmacist["Pharmacist"] -->|Stock Audit & Expiry Check| P3["3.0 Manage Stock & Expiry"]
    D3 -->|Stock Counts| P3
    D1 -->|Expiry Dates| P3
    P3 -->|Expiry & Low Stock Alerts| Pharmacist

    SalesStaff["Sales Staff"] -->|Sale Request| P4["4.0 Process Sales"]
    P4 -->|Check Availability & Expiry| D3
    P4 -->|Deduct Stock| D3
    P4 -->|Save Transaction| D4[("D4: Sales Store")]
    P4 -->|Generate Receipt| SalesStaff

    DataAnalyst["Data Analyst"] -->|Trigger Analytics| P5["5.0 Generate Reports & Python Analysis"]
    D1 --> P5
    D3 --> P5
    D4 --> P5
    P5 -->|Interactive Charts & Reports| DataAnalyst
```

---

## 4. Sequence Diagram: Sales Processing & Billing

```mermaid
sequenceDiagram
    autonumber
    actor Staff as Sales Staff
    participant UI as Console Interface
    participant SS as SaleService
    participant MS as MedicineDAO
    participant SD as StockDAO
    participant DB as Relational Database

    Staff->>UI: Enter Medicine ID & Sale Quantity
    UI->>SS: processSale(medId, qty, customerName)
    SS->>MS: getMedicineById(medId)
    MS->>DB: SELECT * FROM medicines WHERE medicine_id = ?
    DB-->>MS: Return Medicine Details
    MS-->>SS: Medicine Object

    alt Medicine is Expired
        SS-->>UI: ERROR: Expired medicine cannot be sold!
        UI-->>Staff: Display Expiry Warning Alert
    else Medicine is Valid
        SS->>SD: getStockByMedicineId(medId)
        SD->>DB: SELECT current_stock FROM stock WHERE medicine_id = ?
        DB-->>SD: current_stock
        SD-->>SS: Stock Object

        alt Stock < Quantity
            SS-->>UI: ERROR: Insufficient stock available!
            UI-->>Staff: Display Out-of-Stock Alert
        else Stock >= Quantity
            SS->>DB: BEGIN TRANSACTION
            SS->>DB: INSERT INTO sales & sale_items
            SS->>SD: deductStock(medId, qty)
            SD->>DB: UPDATE stock SET current_stock = current_stock - ?
            SS->>DB: COMMIT TRANSACTION
            SS-->>UI: SUCCESS: Sale completed & Bill generated
            UI-->>Staff: Display Itemized Bill & Print Confirmation
        end
    end
```

---

## 5. Activity Diagram: Sale Workflow

```mermaid
flowchart TD
    Start([Start]) --> Login[Access Sales Module]
    Login --> Input[Enter Medicine ID & Quantity]
    Input --> CheckMed{Medicine Exists?}
    CheckMed -- No --> Err1[Display 'Invalid Medicine ID'] --> Input
    CheckMed -- Yes --> CheckExp{Medicine Expired?}
    CheckExp -- Yes --> Err2[Reject: Expired Drug Alert] --> End([Return to Menu])
    CheckExp -- No --> CheckStock{Stock >= Quantity?}
    CheckStock -- No --> Err3[Reject: Insufficient Stock Alert] --> End
    CheckStock -- Yes --> Calc[Compute Subtotal & Taxes]
    Calc --> Trans[Begin DB Transaction]
    Trans --> RecSale[Insert Sale & Sale Item]
    RecSale --> UpdateStock[Decrement Stock in DB]
    UpdateStock --> Commit[Commit Transaction]
    Commit --> PrintBill[Generate & Display Itemized Receipt]
    PrintBill --> End
```
