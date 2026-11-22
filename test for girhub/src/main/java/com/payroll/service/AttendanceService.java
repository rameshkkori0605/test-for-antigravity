package com.payroll.service;

import com.payroll.dto.AttendanceDTO;
import com.payroll.exception.DuplicateResourceException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.model.Attendance;
import com.payroll.model.Employee;
import com.payroll.repository.AttendanceRepository;
import com.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByMonthAndYear(Integer month, Integer year) {
        return attendanceRepository.findByMonthAndYear(month, year).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByEmployeeId(Long employeeId) {
        return attendanceRepository.findByEmployeeIdOrderByYearDescMonthDesc(employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceDTO recordAttendance(AttendanceDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        if (attendanceRepository.existsByEmployeeIdAndMonthAndYear(dto.getEmployeeId(), dto.getMonth(), dto.getYear())) {
            throw new DuplicateResourceException(
                    String.format("Attendance record already exists for %s for %d/%d", employee.getFullName(), dto.getMonth(), dto.getYear())
            );
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setMonth(dto.getMonth());
        attendance.setYear(dto.getYear());
        attendance.setTotalWorkingDays(dto.getTotalWorkingDays());
        attendance.setDaysPresent(dto.getDaysPresent());
        attendance.setUnpaidLeaves(dto.getUnpaidLeaves() != null ? dto.getUnpaidLeaves() : 0);
        attendance.setOvertimeHours(dto.getOvertimeHours() != null ? dto.getOvertimeHours() : java.math.BigDecimal.ZERO);

        Attendance saved = attendanceRepository.save(attendance);
        return mapToDTO(saved);
    }

    @Transactional
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO dto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        attendance.setTotalWorkingDays(dto.getTotalWorkingDays());
        attendance.setDaysPresent(dto.getDaysPresent());
        attendance.setUnpaidLeaves(dto.getUnpaidLeaves() != null ? dto.getUnpaidLeaves() : 0);
        attendance.setOvertimeHours(dto.getOvertimeHours() != null ? dto.getOvertimeHours() : java.math.BigDecimal.ZERO);

        Attendance updated = attendanceRepository.save(attendance);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    public AttendanceDTO mapToDTO(Attendance att) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(att.getId());
        dto.setEmployeeId(att.getEmployee().getId());
        dto.setEmployeeName(att.getEmployee().getFullName());
        dto.setEmpCode(att.getEmployee().getEmpCode());
        dto.setMonth(att.getMonth());
        dto.setYear(att.getYear());
        dto.setTotalWorkingDays(att.getTotalWorkingDays());
        dto.setDaysPresent(att.getDaysPresent());
        dto.setUnpaidLeaves(att.getUnpaidLeaves());
        dto.setOvertimeHours(att.getOvertimeHours());
        return dto;
    }
}
