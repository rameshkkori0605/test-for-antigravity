package com.payroll.repository;

import com.payroll.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
    List<Attendance> findByMonthAndYear(Integer month, Integer year);
    List<Attendance> findByEmployeeIdOrderByYearDescMonthDesc(Long employeeId);
    boolean existsByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
}
