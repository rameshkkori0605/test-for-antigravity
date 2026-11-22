package com.payroll.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AttendanceDTO {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String employeeName;
    private String empCode;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;

    @NotNull(message = "Total working days is required")
    @Min(value = 1, message = "Total working days must be at least 1")
    @Max(value = 31, message = "Total working days cannot exceed 31")
    private Integer totalWorkingDays;

    @NotNull(message = "Days present is required")
    @Min(value = 0, message = "Days present cannot be negative")
    private Integer daysPresent;

    @NotNull(message = "Unpaid leaves is required")
    @Min(value = 0, message = "Unpaid leaves cannot be negative")
    private Integer unpaidLeaves = 0;

    @NotNull(message = "Overtime hours is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Overtime hours cannot be negative")
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    public AttendanceDTO() {
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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
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
}
