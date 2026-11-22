package com.payroll.service;

import com.payroll.dto.DashboardSummaryDTO;
import com.payroll.dto.PayrollGenerateRequest;
import com.payroll.dto.PayrollResponseDTO;
import com.payroll.exception.BadRequestException;
import com.payroll.exception.DuplicateResourceException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.model.Attendance;
import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.repository.AttendanceRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PayrollService {

    private static final BigDecimal PF_RATE = new BigDecimal("0.12");
    private static final BigDecimal PROFESSIONAL_TAX = new BigDecimal("200.00");
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.50");

    private final PayrollRecordRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final NumberToWordsConverter numberToWordsConverter;

    public PayrollService(PayrollRecordRepository payrollRepository,
                          EmployeeRepository employeeRepository,
                          AttendanceRepository attendanceRepository,
                          NumberToWordsConverter numberToWordsConverter) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.numberToWordsConverter = numberToWordsConverter;
    }

    @Transactional(readOnly = true)
    public List<PayrollResponseDTO> getAllPayrollRecords() {
        return payrollRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayrollResponseDTO> getPayrollRecordsByMonthAndYear(Integer month, Integer year) {
        return payrollRepository.findByMonthAndYear(month, year).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PayrollResponseDTO getPayrollById(Long id) {
        PayrollRecord record = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found with id: " + id));
        return mapToDTO(record);
    }

    @Transactional
    public List<PayrollResponseDTO> generatePayroll(PayrollGenerateRequest request) {
        int month = request.getMonth();
        int year = request.getYear();

        List<Employee> targetEmployees;
        if (request.getEmployeeId() != null) {
            Employee emp = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));
            if (!emp.isActive()) {
                throw new BadRequestException("Cannot generate payroll for inactive employee: " + emp.getFullName());
            }
            targetEmployees = List.of(emp);
        } else {
            targetEmployees = employeeRepository.findByActiveTrue();
            if (targetEmployees.isEmpty()) {
                throw new BadRequestException("No active employees found to generate payroll.");
            }
        }

        List<PayrollResponseDTO> generatedList = new ArrayList<>();

        for (Employee employee : targetEmployees) {
            if (payrollRepository.existsByEmployeeIdAndMonthAndYear(employee.getId(), month, year)) {
                if (request.getEmployeeId() != null) {
                    throw new DuplicateResourceException(
                            String.format("Payroll already generated for %s for %02d/%d", employee.getFullName(), month, year)
                    );
                } else {
                    // Skip employee if generating in batch and already processed
                    continue;
                }
            }

            // Fetch attendance or construct default full month
            Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeIdAndMonthAndYear(employee.getId(), month, year);
            int totalWorkingDays = attendanceOpt.map(Attendance::getTotalWorkingDays).orElse(30);
            int daysPresent = attendanceOpt.map(Attendance::getDaysPresent).orElse(30);
            int unpaidLeaves = attendanceOpt.map(Attendance::getUnpaidLeaves).orElse(0);
            BigDecimal overtimeHours = attendanceOpt.map(Attendance::getOvertimeHours).orElse(BigDecimal.ZERO);

            PayrollRecord record = calculatePayroll(employee, month, year, totalWorkingDays, unpaidLeaves, overtimeHours);
            PayrollRecord saved = payrollRepository.save(record);
            generatedList.add(mapToDTO(saved));
        }

        if (generatedList.isEmpty() && request.getEmployeeId() == null) {
            throw new BadRequestException(String.format("All active employees already have payroll generated for %02d/%d", month, year));
        }

        return generatedList;
    }

    /**
     * Java Financial Calculation Engine using strict java.math.BigDecimal
     */
    public PayrollRecord calculatePayroll(Employee employee, int month, int year,
                                          int totalWorkingDays, int unpaidLeaves, BigDecimal overtimeHours) {

        BigDecimal basicSalary = employee.getBasicSalary().setScale(2, RoundingMode.HALF_UP);
        BigDecimal hra = employee.getHra().setScale(2, RoundingMode.HALF_UP);
        BigDecimal specialAllowances = employee.getSpecialAllowances().setScale(2, RoundingMode.HALF_UP);

        // 1. Overtime Pay: (Basic / (Total Working Days * 8)) * Overtime Hours * 1.5
        BigDecimal overtimePay = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (overtimeHours != null && overtimeHours.compareTo(BigDecimal.ZERO) > 0 && totalWorkingDays > 0) {
            BigDecimal totalWorkingHours = BigDecimal.valueOf((long) totalWorkingDays * 8);
            BigDecimal hourlyRate = basicSalary.divide(totalWorkingHours, 4, RoundingMode.HALF_UP);
            overtimePay = hourlyRate.multiply(overtimeHours).multiply(OVERTIME_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
        }

        // 2. Gross Salary = Basic + HRA + Special Allowances + Overtime Pay
        BigDecimal grossSalary = basicSalary.add(hra).add(specialAllowances).add(overtimePay).setScale(2, RoundingMode.HALF_UP);

        // 3. LOP Deduction = (Gross Salary / Total Working Days) * Unpaid Leaves
        BigDecimal lopDeduction = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (unpaidLeaves > 0 && totalWorkingDays > 0) {
            BigDecimal perDayGross = grossSalary.divide(BigDecimal.valueOf(totalWorkingDays), 4, RoundingMode.HALF_UP);
            lopDeduction = perDayGross.multiply(BigDecimal.valueOf(unpaidLeaves)).setScale(2, RoundingMode.HALF_UP);
        }

        // 4. Provident Fund (PF) = 12% of Basic Salary
        BigDecimal pfDeduction = basicSalary.multiply(PF_RATE).setScale(2, RoundingMode.HALF_UP);

        // 5. Professional Tax (PT) = Fixed statutory slab (e.g. 200.00)
        BigDecimal ptDeduction = PROFESSIONAL_TAX.setScale(2, RoundingMode.HALF_UP);

        // 6. Income Tax (TDS) = Progressive tax calculation based on annual gross
        BigDecimal annualGross = grossSalary.multiply(new BigDecimal("12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal annualTax = calculateProgressiveAnnualTDS(annualGross);
        BigDecimal tdsDeduction = annualTax.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);

        // 7. Total Deductions = LOP + PF + PT + TDS
        BigDecimal totalDeductions = lopDeduction.add(pfDeduction).add(ptDeduction).add(tdsDeduction).setScale(2, RoundingMode.HALF_UP);

        // 8. Net In-Hand Salary = Gross Salary - Total Deductions
        BigDecimal netSalary = grossSalary.subtract(totalDeductions).setScale(2, RoundingMode.HALF_UP);
        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            netSalary = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        PayrollRecord record = new PayrollRecord();
        record.setEmployee(employee);
        record.setMonth(month);
        record.setYear(year);
        record.setBasicSalary(basicSalary);
        record.setHra(hra);
        record.setSpecialAllowances(specialAllowances);
        record.setOvertimePay(overtimePay);
        record.setGrossSalary(grossSalary);
        record.setLopDeduction(lopDeduction);
        record.setPfDeduction(pfDeduction);
        record.setPtDeduction(ptDeduction);
        record.setTdsDeduction(tdsDeduction);
        record.setTotalDeductions(totalDeductions);
        record.setNetSalary(netSalary);
        record.setPaymentStatus("PROCESSED");
        record.setProcessedDate(LocalDate.now());

        return record;
    }

    /**
     * Progressive Annual Income Tax (TDS) Slab:
     * - Up to 300,000: Nil (0%)
     * - 300,001 to 600,000: 5%
     * - 600,001 to 900,000: 10%
     * - 900,001 to 1,200,000: 15%
     * - 1,200,001 to 1,500,000: 20%
     * - Above 1,500,000: 30%
     */
    private BigDecimal calculateProgressiveAnnualTDS(BigDecimal annualGross) {
        if (annualGross.compareTo(new BigDecimal("300000")) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal slab1Max = new BigDecimal("300000");
        BigDecimal slab2Max = new BigDecimal("600000");
        BigDecimal slab3Max = new BigDecimal("900000");
        BigDecimal slab4Max = new BigDecimal("1200000");
        BigDecimal slab5Max = new BigDecimal("1500000");

        if (annualGross.compareTo(slab1Max) > 0) {
            BigDecimal taxable = annualGross.min(slab2Max).subtract(slab1Max);
            tax = tax.add(taxable.multiply(new BigDecimal("0.05")));
        }
        if (annualGross.compareTo(slab2Max) > 0) {
            BigDecimal taxable = annualGross.min(slab3Max).subtract(slab2Max);
            tax = tax.add(taxable.multiply(new BigDecimal("0.10")));
        }
        if (annualGross.compareTo(slab3Max) > 0) {
            BigDecimal taxable = annualGross.min(slab4Max).subtract(slab3Max);
            tax = tax.add(taxable.multiply(new BigDecimal("0.15")));
        }
        if (annualGross.compareTo(slab4Max) > 0) {
            BigDecimal taxable = annualGross.min(slab5Max).subtract(slab4Max);
            tax = tax.add(taxable.multiply(new BigDecimal("0.20")));
        }
        if (annualGross.compareTo(slab5Max) > 0) {
            BigDecimal taxable = annualGross.subtract(slab5Max);
            tax = tax.add(taxable.multiply(new BigDecimal("0.30")));
        }

        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDTO getDashboardSummary(Integer month, Integer year) {
        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        long activeEmployees = employeeRepository.countByActiveTrue();
        BigDecimal gross = payrollRepository.sumGrossSalaryByMonthAndYear(month, year);
        BigDecimal deductions = payrollRepository.sumTotalDeductionsByMonthAndYear(month, year);
        BigDecimal net = payrollRepository.sumNetSalaryByMonthAndYear(month, year);

        return new DashboardSummaryDTO(
                activeEmployees,
                gross != null ? gross : BigDecimal.ZERO,
                deductions != null ? deductions : BigDecimal.ZERO,
                net != null ? net : BigDecimal.ZERO,
                month,
                year
        );
    }

    @Transactional
    public void deletePayrollRecord(Long id) {
        if (!payrollRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payroll record not found with id: " + id);
        }
        payrollRepository.deleteById(id);
    }

    public PayrollResponseDTO mapToDTO(PayrollRecord record) {
        PayrollResponseDTO dto = new PayrollResponseDTO();
        dto.setId(record.getId());
        dto.setEmployeeId(record.getEmployee().getId());
        dto.setEmpCode(record.getEmployee().getEmpCode());
        dto.setEmployeeName(record.getEmployee().getFullName());
        dto.setEmail(record.getEmployee().getEmail());
        dto.setDepartment(record.getEmployee().getDepartment());
        dto.setDesignation(record.getEmployee().getDesignation());
        dto.setBankAccountNumber(record.getEmployee().getBankAccountNumber());
        dto.setIfscCode(record.getEmployee().getIfscCode());
        dto.setPanNumber(record.getEmployee().getPanNumber());

        dto.setMonth(record.getMonth());
        dto.setYear(record.getYear());
        dto.setMonthName(Month.of(record.getMonth()).getDisplayName(TextStyle.FULL, Locale.ENGLISH));

        dto.setBasicSalary(record.getBasicSalary());
        dto.setHra(record.getHra());
        dto.setSpecialAllowances(record.getSpecialAllowances());
        dto.setOvertimePay(record.getOvertimePay());
        dto.setGrossSalary(record.getGrossSalary());

        dto.setLopDeduction(record.getLopDeduction());
        dto.setPfDeduction(record.getPfDeduction());
        dto.setPtDeduction(record.getPtDeduction());
        dto.setTdsDeduction(record.getTdsDeduction());
        dto.setTotalDeductions(record.getTotalDeductions());

        dto.setNetSalary(record.getNetSalary());
        dto.setNetSalaryInWords(numberToWordsConverter.convert(record.getNetSalary()));

        // Lookup attendance for payslip breakdown
        Optional<Attendance> attOpt = attendanceRepository.findByEmployeeIdAndMonthAndYear(
                record.getEmployee().getId(), record.getMonth(), record.getYear()
        );
        dto.setTotalWorkingDays(attOpt.map(Attendance::getTotalWorkingDays).orElse(30));
        dto.setDaysPresent(attOpt.map(Attendance::getDaysPresent).orElse(30));
        dto.setUnpaidLeaves(attOpt.map(Attendance::getUnpaidLeaves).orElse(0));
        dto.setOvertimeHours(attOpt.map(Attendance::getOvertimeHours).orElse(BigDecimal.ZERO));

        dto.setPaymentStatus(record.getPaymentStatus());
        dto.setProcessedDate(record.getProcessedDate());

        return dto;
    }
}
