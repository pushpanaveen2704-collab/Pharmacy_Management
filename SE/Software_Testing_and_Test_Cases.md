# Software Testing and Test Cases Documentation
## Course: 25ITT33 - Software Engineering (SE)
### Project: Pharmacy Stock Management and Medicine Sales Analysis System

---

## 1. Testing Strategy and Methodologies

The Pharmacy Stock Management System was subjected to comprehensive multi-level software testing in accordance with IEEE 829 standards:
1. **Unit Testing**: Validating isolated methods in Model, DAO, and Service layers (e.g., input validators, date difference calculation, price multiplication).
2. **Integration Testing**: Verifying interactions between Java Services, JDBC DAO transactions, and SQLite relational tables.
3. **Black Box Testing**:
   - Equivalence Partitioning (Valid vs Invalid menu choices, negative prices, alphanumeric IDs).
   - Boundary Value Analysis (Stock = 0, Stock = Reorder level, Stock = 1, Expiry date = today, Expiry date = tomorrow, Expiry date = yesterday).
4. **System & Validation Testing**: End-to-end execution of workflows including concurrent stock reduction, batch procurement, dashboard live updates, and Python statistical execution.

---

## 2. Test Execution Matrix and Results

| Test Case ID | Test Category | Test Objective | Test Input / Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|---|
| **TC-MED-01** | Functional (Unit) | Register a new medicine with valid details | `MED116`, 'Amoxicillin 250mg', 'Antibiotic', 'Sun Pharma', 45.00, '2027-12-31', 15 | Medicine stored, initial stock row initialized to 0, success message displayed | `[SUCCESS] Medicine registered successfully.` | **PASS** |
| **TC-MED-02** | Validation (BVA) | Prevent duplicate medicine registration | Register with existing ID `MED101` | System detects duplicate PK and displays error without crashing | `[ERROR] Medicine with ID MED101 already exists.` | **PASS** |
| **TC-MED-03** | Validation (Domain) | Prevent negative or zero unit price | Register medicine with price `-10.00` | Rejected by input validator | `[ERROR] Medicine price must be positive.` | **PASS** |
| **TC-SUP-01** | Functional (Unit) | Register new supplier | `S106`, 'Kovai Meds', '9842100000', 'kovai@meds.com', 'Erode' | Supplier record inserted into database | `[SUCCESS] Supplier registered successfully.` | **PASS** |
| **TC-PUR-01** | Transaction (Integration) | Purchase increases inventory atomically | Record purchase of 50 units for `MED101` | Stock increases by 50 (`250 -> 300`), purchase and item rows logged | `[SUCCESS] Purchase recorded successfully.` Stock updated to 300. | **PASS** |
| **TC-PUR-02** | Validation | Reject purchase with zero/negative quantity | Enter quantity `0` or `-20` | Validator rejects input | `[ERROR] Purchase quantity must be positive.` | **PASS** |
| **TC-SAL-01** | Transaction (Integration) | Normal sale completes & decrements stock | Sell 10 units of `MED101` (stock: 250) | Sale record created, stock reduced to 240, bill displayed | `[SUCCESS] Medicine sale completed successfully.` | **PASS** |
| **TC-SAL-02** | Safety Rule (Business Logic) | Prevent sale of expired medicine | Attempt to sell `MED102` (Amoxicillin, expired 2026-10-15) | Sale rejected with expiry alert, stock unchanged | `[ERROR] Expired medicine cannot be sold! (Expired on: 2026-10-15)` | **PASS** |
| **TC-SAL-03** | Safety Rule (Business Logic) | Prevent sale exceeding available inventory | Attempt to sell 500 units of `MED105` (stock: 45) | Sale rejected due to insufficient stock | `[ERROR] Insufficient stock available. Available: 45, Requested: 500` | **PASS** |
| **TC-SAL-04** | Safety Rule (BVA) | Prevent sale of out-of-stock item | Attempt to sell `MED104` (Dolo 650, stock: 0) | Sale rejected immediately | `[ERROR] Insufficient stock available. Available: 0, Requested: 1` | **PASS** |
| **TC-STK-01** | Reporting | Identify Low Stock items | View Low Stock Report | Lists all medicines where `current_stock <= reorder_level` | Accurately lists `MED103`, `MED108`, `MED112` | **PASS** |
| **TC-EXP-01** | Reporting | Detect Near Expiry drugs | View Near Expiry Report (threshold: 30 days) | Lists medicines expiring within 30 days | Accurately identifies `MED101`, `MED105`, `MED108`, `MED112` | **PASS** |
| **TC-DSH-01** | System (Dashboard) | Real-time dynamic calculation of metrics | Select option 1 (Dashboard) from Main Menu | Computes total medicines, revenue, stock, alerts dynamically from DB | Accurately displays real-time summaries and top selling drug | **PASS** |
| **TC-UI-01** | Usability / Resilience | Non-numeric input handling on menu prompt | Enter `'abc'` or special characters `!@#` | Console warns user and re-prompts without exception | `[WARNING] Invalid input. Please enter a valid number.` | **PASS** |
| **TC-LOG-01** | Audit Logging | Dual logging to CMD and TXT file | Execute `PharmacyManagement.bat` | Output rendered on screen AND logged simultaneously to `PharmacyManagement_Output.txt` | Output file contains entire interactive transcript | **PASS** |
| **TC-FDS-01** | Integration | Invoke Python analysis module from Java | Select Option 9 (Python Data Analysis) | Java triggers Python script via ProcessBuilder, extracts DB data, produces statistics & visualizations | Statistical report and charts generated successfully | **PASS** |

---

## 3. Test Summary & Quality Sign-Off
- **Total Test Cases Executed**: 16
- **Passed**: 16
- **Failed**: 0
- **Defect Density**: 0.0
- **Conclusion**: The system satisfies all functional, safety, and performance criteria specified in the SRS.
