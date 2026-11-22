package com.payroll.service;

import com.payroll.dto.EmployeeDTO;
import com.payroll.exception.DuplicateResourceException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.model.Employee;
import com.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getActiveEmployees() {
        return employeeRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToDTO(employee);
    }

    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        if (employeeRepository.existsByEmpCode(dto.getEmpCode())) {
            throw new DuplicateResourceException("Employee code already exists: " + dto.getEmpCode());
        }
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Employee email already exists: " + dto.getEmail());
        }

        Employee employee = new Employee();
        mapToEntity(dto, employee);
        Employee saved = employeeRepository.save(employee);
        return mapToDTO(saved);
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (!employee.getEmpCode().equalsIgnoreCase(dto.getEmpCode()) && employeeRepository.existsByEmpCode(dto.getEmpCode())) {
            throw new DuplicateResourceException("Employee code already exists: " + dto.getEmpCode());
        }
        if (!employee.getEmail().equalsIgnoreCase(dto.getEmail()) && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Employee email already exists: " + dto.getEmail());
        }

        mapToEntity(dto, employee);
        Employee updated = employeeRepository.save(employee);
        return mapToDTO(updated);
    }

    @Transactional
    public void deactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    @Transactional
    public void activateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setActive(true);
        employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    public EmployeeDTO mapToDTO(Employee emp) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(emp.getId());
        dto.setEmpCode(emp.getEmpCode());
        dto.setFullName(emp.getFullName());
        dto.setEmail(emp.getEmail());
        dto.setPhone(emp.getPhone());
        dto.setDepartment(emp.getDepartment());
        dto.setDesignation(emp.getDesignation());
        dto.setJoiningDate(emp.getJoiningDate());
        dto.setBankAccountNumber(emp.getBankAccountNumber());
        dto.setIfscCode(emp.getIfscCode());
        dto.setPanNumber(emp.getPanNumber());
        dto.setBasicSalary(emp.getBasicSalary());
        dto.setHra(emp.getHra());
        dto.setSpecialAllowances(emp.getSpecialAllowances());
        dto.setActive(emp.isActive());
        return dto;
    }

    public void mapToEntity(EmployeeDTO dto, Employee emp) {
        emp.setEmpCode(dto.getEmpCode());
        emp.setFullName(dto.getFullName());
        emp.setEmail(dto.getEmail());
        emp.setPhone(dto.getPhone());
        emp.setDepartment(dto.getDepartment());
        emp.setDesignation(dto.getDesignation());
        emp.setJoiningDate(dto.getJoiningDate());
        emp.setBankAccountNumber(dto.getBankAccountNumber());
        emp.setIfscCode(dto.getIfscCode());
        emp.setPanNumber(dto.getPanNumber());
        emp.setBasicSalary(dto.getBasicSalary());
        emp.setHra(dto.getHra());
        emp.setSpecialAllowances(dto.getSpecialAllowances());
        emp.setActive(dto.isActive());
    }
}
