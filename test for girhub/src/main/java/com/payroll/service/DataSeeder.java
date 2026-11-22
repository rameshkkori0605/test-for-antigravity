package com.payroll.service;

import com.payroll.model.Attendance;
import com.payroll.model.Employee;
import com.payroll.model.User;
import com.payroll.repository.AttendanceRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollService payrollService;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      EmployeeRepository employeeRepository,
                      AttendanceRepository attendanceRepository,
                      PayrollService payrollService,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.payrollService = payrollService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Default Admin User: admin@payroll.com / admin123
        if (!userRepository.existsByEmail("admin@payroll.com")) {
            User admin = new User(
                    "admin@payroll.com",
                    passwordEncoder.encode("admin123"),
                    "System Administrator",
                    "ROLE_ADMIN",
                    true
            );
            userRepository.save(admin);
            System.out.println(">>> [DataSeeder] Seeded default admin account: admin@payroll.com / admin123");
        }

        // 2. Seed Sample Employees if table is empty
        if (employeeRepository.count() == 0) {
            Employee emp1 = new Employee();
            emp1.setEmpCode("EMP-1001");
            emp1.setFullName("Alexander Pierce");
            emp1.setEmail("alex.pierce@company.com");
            emp1.setPhone("+1 (555) 234-5678");
            emp1.setDepartment("Engineering");
            emp1.setDesignation("Lead Backend Architect");
            emp1.setJoiningDate(LocalDate.of(2021, 3, 15));
            emp1.setBankAccountNumber("9876543210123");
            emp1.setIfscCode("HDFC0001234");
            emp1.setPanNumber("ABCDE1234F");
            emp1.setBasicSalary(new BigDecimal("95000.00"));
            emp1.setHra(new BigDecimal("38000.00"));
            emp1.setSpecialAllowances(new BigDecimal("15000.00"));
            emp1.setActive(true);
            emp1 = employeeRepository.save(emp1);

            Employee emp2 = new Employee();
            emp2.setEmpCode("EMP-1002");
            emp2.setFullName("Sophia Rodriguez");
            emp2.setEmail("sophia.rodriguez@company.com");
            emp2.setPhone("+1 (555) 345-6789");
            emp2.setDepartment("Product Design");
            emp2.setDesignation("Senior UI/UX Designer");
            emp2.setJoiningDate(LocalDate.of(2022, 6, 1));
            emp2.setBankAccountNumber("8765432109876");
            emp2.setIfscCode("ICIC0005678");
            emp2.setPanNumber("BCDEF2345G");
            emp2.setBasicSalary(new BigDecimal("75000.00"));
            emp2.setHra(new BigDecimal("30000.00"));
            emp2.setSpecialAllowances(new BigDecimal("12000.00"));
            emp2.setActive(true);
            emp2 = employeeRepository.save(emp2);

            Employee emp3 = new Employee();
            emp3.setEmpCode("EMP-1003");
            emp3.setFullName("Marcus Chen");
            emp3.setEmail("marcus.chen@company.com");
            emp3.setPhone("+1 (555) 456-7890");
            emp3.setDepartment("Human Resources");
            emp3.setDesignation("HR Operations Manager");
            emp3.setJoiningDate(LocalDate.of(2020, 11, 20));
            emp3.setBankAccountNumber("7654321098765");
            emp3.setIfscCode("SBIN0009876");
            emp3.setPanNumber("CDEFG3456H");
            emp3.setBasicSalary(new BigDecimal("60000.00"));
            emp3.setHra(new BigDecimal("24000.00"));
            emp3.setSpecialAllowances(new BigDecimal("8000.00"));
            emp3.setActive(true);
            emp3 = employeeRepository.save(emp3);

            Employee emp4 = new Employee();
            emp4.setEmpCode("EMP-1004");
            emp4.setFullName("Priya Sharma");
            emp4.setEmail("priya.sharma@company.com");
            emp4.setPhone("+1 (555) 567-8901");
            emp4.setDepartment("Finance & Accounts");
            emp4.setDesignation("Senior Financial Analyst");
            emp4.setJoiningDate(LocalDate.of(2023, 1, 10));
            emp4.setBankAccountNumber("6543210987654");
            emp4.setIfscCode("KKBK0004321");
            emp4.setPanNumber("DEFGH4567J");
            emp4.setBasicSalary(new BigDecimal("68000.00"));
            emp4.setHra(new BigDecimal("27200.00"));
            emp4.setSpecialAllowances(new BigDecimal("10000.00"));
            emp4.setActive(true);
            emp4 = employeeRepository.save(emp4);

            System.out.println(">>> [DataSeeder] Seeded 4 sample active employees.");

            // 3. Seed Sample Attendance for Current Month
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();

            seedAttendance(emp1, currentMonth, currentYear, 22, 22, 0, new BigDecimal("10.00"));
            seedAttendance(emp2, currentMonth, currentYear, 22, 20, 2, new BigDecimal("4.50"));
            seedAttendance(emp3, currentMonth, currentYear, 22, 21, 1, BigDecimal.ZERO);
            seedAttendance(emp4, currentMonth, currentYear, 22, 22, 0, new BigDecimal("6.00"));

            System.out.println(">>> [DataSeeder] Seeded sample attendance records.");

            // 4. Generate initial payroll records for current month
            com.payroll.dto.PayrollGenerateRequest generateReq = new com.payroll.dto.PayrollGenerateRequest(null, currentMonth, currentYear);
            payrollService.generatePayroll(generateReq);
            System.out.println(">>> [DataSeeder] Auto-generated initial payroll for month " + currentMonth + "/" + currentYear);
        }
    }

    private void seedAttendance(Employee employee, int month, int year, int totalDays, int present, int leaves, BigDecimal otHours) {
        Attendance att = new Attendance();
        att.setEmployee(employee);
        att.setMonth(month);
        att.setYear(year);
        att.setTotalWorkingDays(totalDays);
        att.setDaysPresent(present);
        att.setUnpaidLeaves(leaves);
        att.setOvertimeHours(otHours);
        attendanceRepository.save(att);
    }
}
