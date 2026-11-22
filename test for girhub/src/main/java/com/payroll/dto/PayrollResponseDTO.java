package com.payroll.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollResponseDTO {

    private Long id;
    private Long employeeId;
    private String empCode;
    private String employeeName;
    private String email;
    private String department;
    private String designation;
    private String bankAccountNumber;
    private String ifscCode;
    private String panNumber;

    private Integer month;
    private Integer year;
    private String monthName;

    // Earnings
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal specialAllowances;
    private BigDecimal overtimePay;
    private BigDecimal grossSalary;

    // Deductions
    private BigDecimal lopDeduction;
    private BigDecimal pfDeduction;
    private BigDecimal ptDeduction;
    private BigDecimal tdsDeduction;
    private BigDecimal totalDeductions;

    // Net
    private BigDecimal netSalary;
    private String netSalaryInWords;

    // Attendance info
    private Integer totalWorkingDays;
    private Integer daysPresent;
    private Integer unpaidLeaves;
    private BigDecimal overtimeHours;

    private String paymentStatus;
    private LocalDate processedDate;

    public PayrollResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getHra() {
        return hra;
    }

    public void setHra(BigDecimal hra) {
        this.hra = hra;
    }

    public BigDecimal getSpecialAllowances() {
        return specialAllowances;
    }

    public void setSpecialAllowances(BigDecimal specialAllowances) {
        this.specialAllowances = specialAllowances;
    }

    public BigDecimal getOvertimePay() {
        return overtimePay;
    }

    public void setOvertimePay(BigDecimal overtimePay) {
        this.overtimePay = overtimePay;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }

    public BigDecimal getLopDeduction() {
        return lopDeduction;
    }

    public void setLopDeduction(BigDecimal lopDeduction) {
        this.lopDeduction = lopDeduction;
    }

    public BigDecimal getPfDeduction() {
        return pfDeduction;
    }

    public void setPfDeduction(BigDecimal pfDeduction) {
        this.pfDeduction = pfDeduction;
    }

    public BigDecimal getPtDeduction() {
        return ptDeduction;
    }

    public void setPtDeduction(BigDecimal ptDeduction) {
        this.ptDeduction = ptDeduction;
    }

    public BigDecimal getTdsDeduction() {
        return tdsDeduction;
    }

    public void setTdsDeduction(BigDecimal tdsDeduction) {
        this.tdsDeduction = tdsDeduction;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(BigDecimal totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public String getNetSalaryInWords() {
        return netSalaryInWords;
    }

    public void setNetSalaryInWords(String netSalaryInWords) {
        this.netSalaryInWords = netSalaryInWords;
    }

    public Integer getTotalWorkingDays() {
        return totalWorkingDays;
    }

    public void setTotalWorkingDays(Integer totalWorkingDays) {
        this.totalWorkingDays = totalWorkingDays;
    }

    public Integer getDaysPresent() {
        return daysPresent;
    }

    public void setDaysPresent(Integer daysPresent) {
        this.daysPresent = daysPresent;
    }

    public Integer getUnpaidLeaves() {
        return unpaidLeaves;
    }

    public void setUnpaidLeaves(Integer unpaidLeaves) {
        this.unpaidLeaves = unpaidLeaves;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDate processedDate) {
        this.processedDate = processedDate;
    }
}
