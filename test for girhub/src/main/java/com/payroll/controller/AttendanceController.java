package com.payroll.controller;

import com.payroll.dto.AttendanceDTO;
import com.payroll.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAttendance(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long employeeId) {

        if (employeeId != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByEmployeeId(employeeId));
        }
        if (month != null && year != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByMonthAndYear(month, year));
        }
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @PostMapping
    public ResponseEntity<AttendanceDTO> recordAttendance(@Valid @RequestBody AttendanceDTO dto) {
        AttendanceDTO created = attendanceService.recordAttendance(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(@PathVariable Long id, @Valid @RequestBody AttendanceDTO dto) {
        AttendanceDTO updated = attendanceService.updateAttendance(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
