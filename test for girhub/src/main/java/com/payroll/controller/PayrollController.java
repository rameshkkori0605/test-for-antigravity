package com.payroll.controller;

import com.payroll.dto.DashboardSummaryDTO;
import com.payroll.dto.PayrollGenerateRequest;
import com.payroll.dto.PayrollResponseDTO;
import com.payroll.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping
    public ResponseEntity<List<PayrollResponseDTO>> getPayrollRecords(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        if (month != null && year != null) {
            return ResponseEntity.ok(payrollService.getPayrollRecordsByMonthAndYear(month, year));
        }
        return ResponseEntity.ok(payrollService.getAllPayrollRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponseDTO> getPayrollById(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getPayrollById(id));
    }

    @PostMapping("/generate")
    public ResponseEntity<List<PayrollResponseDTO>> generatePayroll(@Valid @RequestBody PayrollGenerateRequest request) {
        List<PayrollResponseDTO> result = payrollService.generatePayroll(request);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(payrollService.getDashboardSummary(month, year));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayrollRecord(@PathVariable Long id) {
        payrollService.deletePayrollRecord(id);
        return ResponseEntity.noContent().build();
    }
}
