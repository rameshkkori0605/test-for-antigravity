package com.payroll.repository;

import com.payroll.model.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, Long> {
    Optional<PayrollRecord> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
    List<PayrollRecord> findByMonthAndYear(Integer month, Integer year);
    List<PayrollRecord> findByEmployeeIdOrderByYearDescMonthDesc(Long employeeId);
    boolean existsByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    @Query("SELECT COALESCE(SUM(p.grossSalary), 0) FROM PayrollRecord p WHERE p.month = :month AND p.year = :year")
    BigDecimal sumGrossSalaryByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT COALESCE(SUM(p.totalDeductions), 0) FROM PayrollRecord p WHERE p.month = :month AND p.year = :year")
    BigDecimal sumTotalDeductionsByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT COALESCE(SUM(p.netSalary), 0) FROM PayrollRecord p WHERE p.month = :month AND p.year = :year")
    BigDecimal sumNetSalaryByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);
}
