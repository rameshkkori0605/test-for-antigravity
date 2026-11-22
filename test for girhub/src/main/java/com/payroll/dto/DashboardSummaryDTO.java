package com.payroll.dto;

import java.math.BigDecimal;

public class DashboardSummaryDTO {

    private long totalActiveEmployees;
    private BigDecimal monthlyGrossPayroll;
    private BigDecimal monthlyTotalDeductions;
    private BigDecimal monthlyNetDisbursed;
    private int currentMonth;
    private int currentYear;

    public DashboardSummaryDTO() {
    }

    public DashboardSummaryDTO(long totalActiveEmployees, BigDecimal monthlyGrossPayroll,
                               BigDecimal monthlyTotalDeductions, BigDecimal monthlyNetDisbursed,
                               int currentMonth, int currentYear) {
        this.totalActiveEmployees = totalActiveEmployees;
        this.monthlyGrossPayroll = monthlyGrossPayroll;
        this.monthlyTotalDeductions = monthlyTotalDeductions;
        this.monthlyNetDisbursed = monthlyNetDisbursed;
        this.currentMonth = currentMonth;
        this.currentYear = currentYear;
    }

    public long getTotalActiveEmployees() {
        return totalActiveEmployees;
    }

    public void setTotalActiveEmployees(long totalActiveEmployees) {
        this.totalActiveEmployees = totalActiveEmployees;
    }

    public BigDecimal getMonthlyGrossPayroll() {
        return monthlyGrossPayroll;
    }

    public void setMonthlyGrossPayroll(BigDecimal monthlyGrossPayroll) {
        this.monthlyGrossPayroll = monthlyGrossPayroll;
    }

    public BigDecimal getMonthlyTotalDeductions() {
        return monthlyTotalDeductions;
    }

    public void setMonthlyTotalDeductions(BigDecimal monthlyTotalDeductions) {
        this.monthlyTotalDeductions = monthlyTotalDeductions;
    }

    public BigDecimal getMonthlyNetDisbursed() {
        return monthlyNetDisbursed;
    }

    public void setMonthlyNetDisbursed(BigDecimal monthlyNetDisbursed) {
        this.monthlyNetDisbursed = monthlyNetDisbursed;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }
}
