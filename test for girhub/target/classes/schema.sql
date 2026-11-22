-- ===================================================================
-- Employee Payroll Management System - MySQL 8.x Database Schema
-- Database: payroll_db
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `payroll_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `payroll_db`;

-- 1. Users Table (Authentication and RBAC)
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'ROLE_ADMIN',
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Employees Table
CREATE TABLE IF NOT EXISTS `employees` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `emp_code` VARCHAR(30) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL,
    `department` VARCHAR(50) NOT NULL,
    `designation` VARCHAR(50) NOT NULL,
    `joining_date` DATE NOT NULL,
    `bank_account_number` VARCHAR(50) NOT NULL,
    `ifsc_code` VARCHAR(20) NOT NULL,
    `pan_number` VARCHAR(20) NOT NULL,
    `basic_salary` DECIMAL(12, 2) NOT NULL,
    `hra` DECIMAL(12, 2) NOT NULL,
    `special_allowances` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Attendance & Leave Records Table
CREATE TABLE IF NOT EXISTS `attendance` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `employee_id` BIGINT NOT NULL,
    `month` INT NOT NULL,
    `year` INT NOT NULL,
    `total_working_days` INT NOT NULL,
    `days_present` INT NOT NULL,
    `unpaid_leaves` INT NOT NULL DEFAULT 0,
    `overtime_hours` DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_attendance_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uk_attendance_emp_month_year` UNIQUE (`employee_id`, `month`, `year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Payroll Records Table
CREATE TABLE IF NOT EXISTS `payroll_records` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `employee_id` BIGINT NOT NULL,
    `month` INT NOT NULL,
    `year` INT NOT NULL,
    `basic_salary` DECIMAL(12, 2) NOT NULL,
    `hra` DECIMAL(12, 2) NOT NULL,
    `special_allowances` DECIMAL(12, 2) NOT NULL,
    `overtime_pay` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `gross_salary` DECIMAL(12, 2) NOT NULL,
    `lop_deduction` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `pf_deduction` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `pt_deduction` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `tds_deduction` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `total_deductions` DECIMAL(12, 2) NOT NULL,
    `net_salary` DECIMAL(12, 2) NOT NULL,
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PROCESSED',
    `processed_date` DATE NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_payroll_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uk_payroll_emp_month_year` UNIQUE (`employee_id`, `month`, `year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
