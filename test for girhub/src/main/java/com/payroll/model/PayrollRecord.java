package com.payroll.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_records", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "month", "year"}, name = "uk_payroll_emp_month_year")
})
public class PayrollRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Integer month; // 1 to 12

    @Column(nullable = false)
    private Integer year;

    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "hra", nullable = false, precision = 12, scale = 2)
    private BigDecimal hra;

    @Column(name = "special_allowances", nullable = false, precision = 12, scale = 2)
    private BigDecimal specialAllowances;

    @Column(name = "overtime_pay", nullable = false, precision = 12, scale = 2)
    private BigDecimal overtimePay;

    @Column(name = "gross_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "lop_deduction", nullable = false, precision = 12, scale = 2)
    private BigDecimal lopDeduction;

    @Column(name = "pf_deduction", nullable = false, precision = 12, scale = 2)
    private BigDecimal pfDeduction;

    @Column(name = "pt_deduction", nullable = false, precision = 12, scale = 2)
    private BigDecimal ptDeduction;

    @Column(name = "tds_deduction", nullable = false, precision = 12, scale = 2)
    private BigDecimal tdsDeduction;

    @Column(name = "total_deductions", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDeductions;

    @Column(name = "net_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "PROCESSED"; // PROCESSED, PAID, ON_HOLD

    @Column(name = "processed_date", nullable = false)
    private LocalDate processedDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PayrollRecord() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.processedDate == null) {
            this.processedDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
