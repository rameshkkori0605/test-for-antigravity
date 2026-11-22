# 💼 Employee Payroll Management System

An Enterprise-grade, Full-Stack **Employee Payroll Management System** developed with **Java 17+**, **Spring Boot 3.x**, **Spring Data JPA**, **Spring Security**, **MySQL 8.x**, and a responsive frontend built with **Bootstrap 5 & Vanilla JavaScript** served directly from the Spring Boot embedded container.

---

## 🚀 Key Features

1. **Authentication & Role-Based Access Control (RBAC):**
   - Secured endpoints and pages with Spring Security 6.
   - Built-in `CommandLineRunner` automatically seeds administrative credentials (`admin@payroll.com` / `admin123`) hashed with BCrypt.
   - Modern administrative login page (`login.html`) with session persistence.

2. **Executive KPI Dashboard (`index.html`):**
   - Dynamic period selectors (Month & Year).
   - Real-time aggregated financial metric cards:
     - **Total Active Employees**
     - **Gross Monthly Payroll**
     - **Total Statutory & LOP Deductions**
     - **Net Disbursed Take-Home**
   - Recent payroll transaction stream with instant payslip access.

3. **Complete Employee Directory (CRUD):**
   - Full lifecycle management: Add, view, edit, activate/deactivate, and delete staff records.
   - Stores employee identity, department, designation, joining date, bank account number, IFSC routing code, and PAN tax identifier.
   - Compensation breakdown: Basic Salary, House Rent Allowance (HRA), and Special Allowances.

4. **Monthly Attendance & Leave Log:**
   - Track monthly working days, present days, Loss of Pay (LOP) unpaid leaves, and overtime hours per employee.

5. **Precise Java Payroll Engine (`PayrollService.java`):**
   - Financial precision using strict `java.math.BigDecimal` (no floating-point inaccuracy).
   - **Formulas Implemented:**
     - `Hourly Rate = Basic Salary / (Total Working Days * 8)`
     - `Overtime Pay = Hourly Rate * Overtime Hours * 1.5`
     - `Gross Salary = Basic + HRA + Special Allowances + Overtime Pay`
     - `LOP Deduction = (Gross Salary / Total Working Days) * Unpaid Leaves`
     - `Provident Fund (PF) = 12% of Basic Salary`
     - `Professional Tax (PT) = ₹200.00`
     - `Income Tax (TDS) = Progressive multi-slab tax based on annualized gross`
     - `Total Deductions = LOP + PF + PT + TDS`
     - `Net In-Hand Salary = Gross Salary - Total Deductions`
   - Duplicate prevention prevents double disbursement for the same employee in the same period.

6. **Itemized Printable Payslip (`payslip.html`):**
   - Printable formatted salary slip with company branding, employee details, bank credentials, attendance summary, itemized earnings vs. deductions table, net salary figure, and net salary in words (e.g. *"Seventy-Five Thousand Four Hundred Rupees Only"*).

---

## 🛠 Tech Stack

- **Backend:** Java 17+, Spring Boot 3.2.5 (Spring Web, Spring Data JPA, Spring Security 6, Bean Validation)
- **Database:** MySQL 8.x (with H2 available for tests)
- **Build Tool:** Apache Maven
- **Frontend:** HTML5, CSS3, Bootstrap 5.3, Bootstrap Icons, Vanilla JavaScript (`fetch()` API)
- **Host / Port:** Single server at `http://localhost:8080`

---

## 📂 Project Structure

```
├── pom.xml
├── .gitignore
├── README.md
└── src
    └── main
        ├── java
        │   └── com
        │       └── payroll
        │           ├── EmployeePayrollApplication.java
        │           ├── config
        │           │   └── SecurityConfig.java
        │           ├── controller
        │           │   ├── AuthController.java
        │           │   ├── EmployeeController.java
        │           │   ├── AttendanceController.java
        │           │   └── PayrollController.java
        │           ├── dto
        │           │   ├── LoginRequest.java
        │           │   ├── AuthResponse.java
        │           │   ├── EmployeeDTO.java
        │           │   ├── AttendanceDTO.java
        │           │   ├── PayrollGenerateRequest.java
        │           │   ├── PayrollResponseDTO.java
        │           │   └── DashboardSummaryDTO.java
        │           ├── exception
        │           │   ├── BadRequestException.java
        │           │   ├── DuplicateResourceException.java
        │           │   ├── ErrorResponse.java
        │           │   ├── GlobalExceptionHandler.java
        │           │   └── ResourceNotFoundException.java
        │           ├── model
        │           │   ├── Attendance.java
        │           │   ├── Employee.java
        │           │   ├── PayrollRecord.java
        │           │   └── User.java
        │           ├── repository
        │           │   ├── AttendanceRepository.java
        │           │   ├── EmployeeRepository.java
        │           │   ├── PayrollRecordRepository.java
        │           │   └── UserRepository.java
        │           └── service
        │               ├── AttendanceService.java
        │               ├── DataSeeder.java
        │               ├── EmployeeService.java
        │               ├── NumberToWordsConverter.java
        │               └── PayrollService.java
        └── resources
            ├── application.properties
            ├── schema.sql
            └── static
                ├── css
                │   └── styles.css
                ├── js
                │   └── app.js
                ├── index.html
                ├── login.html
                └── payslip.html
```

---

## ⚙️ Step-by-Step Setup & Execution

### Prerequisites
- **Java Development Kit (JDK) 17** or higher installed.
- **Apache Maven 3.8+** installed.
- **MySQL 8.x** server running.

### 1. Database Setup
Create the MySQL database or let the application auto-create it:
```sql
CREATE DATABASE IF NOT EXISTS payroll_db;
```
Verify or customize your MySQL username and password in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/payroll_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```
*(Optional: Run `src/main/resources/schema.sql` if you prefer manual table creation; otherwise Hibernate `ddl-auto=update` handles it automatically).*

### 2. Build and Run the Application
Open a terminal in the project root directory:
```bash
# Clean and package
mvn clean package -DskipTests

# Run the Spring Boot Application
mvn spring-boot:run
```

### 3. Access the Web Application
Open your web browser and navigate to:
👉 **[http://localhost:8080](http://localhost:8080)**

- **Default Administrator Login:**
  - **Email:** `admin@payroll.com`
  - **Password:** `admin123`

The `DataSeeder` automatically generates 4 diverse employee profiles, attendance records, and calculated payroll on first startup so your dashboard is immediately active and testable!
