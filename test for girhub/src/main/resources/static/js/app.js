/**
 * PayMaster Pro - Robust Client-Side Controller
 * Supports real-time Spring Boot 3 REST APIs with local fallback resilience
 */

let allEmployees = [];
let employeeModalInstance = null;
let attendanceModalInstance = null;
let toastInstance = null;
let isBackendConnected = false;

// Mock local state in case backend is starting or offline
let mockEmployees = [
    {
        id: 1,
        empCode: "EMP-1001",
        fullName: "Alexander Pierce",
        email: "alex.pierce@company.com",
        phone: "+1 (555) 234-5678",
        department: "Engineering",
        designation: "Lead Backend Architect",
        joiningDate: "2021-03-15",
        bankAccountNumber: "9876543210123",
        ifscCode: "HDFC0001234",
        panNumber: "ABCDE1234F",
        basicSalary: 95000.00,
        hra: 38000.00,
        specialAllowances: 15000.00,
        active: true
    },
    {
        id: 2,
        empCode: "EMP-1002",
        fullName: "Sophia Rodriguez",
        email: "sophia.rodriguez@company.com",
        phone: "+1 (555) 345-6789",
        department: "Product Design",
        designation: "Senior UI/UX Designer",
        joiningDate: "2022-06-01",
        bankAccountNumber: "8765432109876",
        ifscCode: "ICIC0005678",
        panNumber: "BCDEF2345G",
        basicSalary: 75000.00,
        hra: 30000.00,
        specialAllowances: 12000.00,
        active: true
    },
    {
        id: 3,
        empCode: "EMP-1003",
        fullName: "Marcus Chen",
        email: "marcus.chen@company.com",
        phone: "+1 (555) 456-7890",
        department: "Human Resources",
        designation: "HR Operations Manager",
        joiningDate: "2020-11-20",
        bankAccountNumber: "7654321098765",
        ifscCode: "SBIN0009876",
        panNumber: "CDEFG3456H",
        basicSalary: 60000.00,
        hra: 24000.00,
        specialAllowances: 8000.00,
        active: true
    },
    {
        id: 4,
        empCode: "EMP-1004",
        fullName: "Priya Sharma",
        email: "priya.sharma@company.com",
        phone: "+1 (555) 567-8901",
        department: "Finance & Accounts",
        designation: "Senior Financial Analyst",
        joiningDate: "2023-01-10",
        bankAccountNumber: "6543210987654",
        ifscCode: "KKBK0004321",
        panNumber: "DEFGH4567J",
        basicSalary: 68000.00,
        hra: 27200.00,
        specialAllowances: 10000.00,
        active: true
    }
];

let mockAttendance = [
    { id: 1, employeeId: 1, employeeName: "Alexander Pierce", empCode: "EMP-1001", month: 11, year: 2026, totalWorkingDays: 22, daysPresent: 22, unpaidLeaves: 0, overtimeHours: 10.0 },
    { id: 2, employeeId: 2, employeeName: "Sophia Rodriguez", empCode: "EMP-1002", month: 11, year: 2026, totalWorkingDays: 22, daysPresent: 20, unpaidLeaves: 2, overtimeHours: 4.5 },
    { id: 3, employeeId: 3, employeeName: "Marcus Chen", empCode: "EMP-1003", month: 11, year: 2026, totalWorkingDays: 22, daysPresent: 21, unpaidLeaves: 1, overtimeHours: 0.0 },
    { id: 4, employeeId: 4, employeeName: "Priya Sharma", empCode: "EMP-1004", month: 11, year: 2026, totalWorkingDays: 22, daysPresent: 22, unpaidLeaves: 0, overtimeHours: 6.0 }
];

let mockPayrollRecords = [
    {
        id: 1, employeeId: 1, empCode: "EMP-1001", employeeName: "Alexander Pierce", email: "alex.pierce@company.com",
        department: "Engineering", designation: "Lead Backend Architect", bankAccountNumber: "9876543210123", ifscCode: "HDFC0001234", panNumber: "ABCDE1234F",
        month: 11, year: 2026, monthName: "November", basicSalary: 95000.00, hra: 38000.00, specialAllowances: 15000.00, overtimePay: 8100.00,
        grossSalary: 156100.00, lopDeduction: 0.00, pfDeduction: 11400.00, ptDeduction: 200.00, tdsDeduction: 15200.00, totalDeductions: 26800.00,
        netSalary: 129300.00, netSalaryInWords: "One Lakh Twenty Nine Thousand Three Hundred Rupees Only", totalWorkingDays: 22, daysPresent: 22, unpaidLeaves: 0, overtimeHours: 10.0,
        paymentStatus: "PROCESSED", processedDate: "2026-11-22"
    },
    {
        id: 2, employeeId: 2, empCode: "EMP-1002", employeeName: "Sophia Rodriguez", email: "sophia.rodriguez@company.com",
        department: "Product Design", designation: "Senior UI/UX Designer", bankAccountNumber: "8765432109876", ifscCode: "ICIC0005678", panNumber: "BCDEF2345G",
        month: 11, year: 2026, monthName: "November", basicSalary: 75000.00, hra: 30000.00, specialAllowances: 12000.00, overtimePay: 2875.00,
        grossSalary: 119875.00, lopDeduction: 10897.72, pfDeduction: 9000.00, ptDeduction: 200.00, tdsDeduction: 7850.00, totalDeductions: 27947.72,
        netSalary: 91927.28, netSalaryInWords: "Ninety One Thousand Nine Hundred Twenty Seven Rupees and Twenty Eight Paise Only", totalWorkingDays: 22, daysPresent: 20, unpaidLeaves: 2, overtimeHours: 4.5,
        paymentStatus: "PROCESSED", processedDate: "2026-11-22"
    }
];

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Initialize Modals and Toasts
    const empModalEl = document.getElementById('employeeModal');
    if (empModalEl) employeeModalInstance = new bootstrap.Modal(empModalEl);

    const attModalEl = document.getElementById('attendanceModal');
    if (attModalEl) attendanceModalInstance = new bootstrap.Modal(attModalEl);

    const toastEl = document.getElementById('liveToast');
    if (toastEl) toastInstance = new bootstrap.Toast(toastEl, { delay: 4000 });

    // 2. Set current date string
    const now = new Date();
    const dateStr = now.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' });
    const curDateEl = document.getElementById('currentDateStr');
    if (curDateEl) curDateEl.textContent = dateStr;

    // Set default month/year values
    const curMonth = (now.getMonth() + 1).toString();
    const curYear = now.getFullYear().toString();

    ['dashMonthSelect', 'attMonthFilter', 'payrollMonthSelect', 'payslipMonthFilter'].forEach(id => {
        const el = document.getElementById(id);
        if (el && id !== 'payslipMonthFilter') el.value = curMonth;
    });

    ['dashYearSelect', 'attYearFilter', 'payrollYearInput', 'payslipYearFilter'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = curYear;
    });

    // 3. Authenticate and verify backend status
    await checkAuth();

    // 4. Initial loads
    await loadDashboardSummary();
    await loadEmployees();
});

// ================= AUTHENTICATION & CONNECTIVITY =================
async function checkAuth() {
    try {
        const res = await fetch('/api/auth/me');
        if (res.ok) {
            const user = await res.json();
            if (user.authenticated) {
                isBackendConnected = true;
                updateStatusBadge(true);
                setUserDisplay(user.fullName, user.role);
                return;
            }
        }
    } catch (e) {
        // Backend offline or running in standalone static preview
    }

    // Check session storage
    const cached = sessionStorage.getItem('payroll_user');
    if (cached) {
        try {
            const user = JSON.parse(cached);
            isBackendConnected = false;
            updateStatusBadge(false);
            setUserDisplay(user.fullName || 'System Administrator', user.role || 'ROLE_ADMIN');
            return;
        } catch (e) {}
    }

    // Default to admin preview mode if direct page visit
    setUserDisplay('System Administrator', 'ROLE_ADMIN');
    updateStatusBadge(false);
}

function updateStatusBadge(online) {
    const badge = document.getElementById('backendStatusBadge');
    const text = document.getElementById('backendStatusText');
    if (!badge || !text) return;

    if (online) {
        badge.className = 'badge bg-success-subtle text-success border border-success-subtle py-2 px-3 small';
        text.innerHTML = '<i class="bi bi-check-circle-fill me-1"></i> Spring Boot Connected (localhost:8080)';
    } else {
        badge.className = 'badge bg-warning-subtle text-warning-emphasis border border-warning-subtle py-2 px-3 small';
        text.innerHTML = '<i class="bi bi-lightning-charge-fill me-1"></i> Live Interactive Preview';
    }
}

function setUserDisplay(name, role) {
    const nameEl = document.getElementById('userDisplayName');
    if (nameEl) nameEl.textContent = name || 'Administrator';
    const roleEl = document.getElementById('userRoleBadge');
    if (roleEl) roleEl.textContent = role || 'ROLE_ADMIN';
}

async function logout() {
    try {
        if (isBackendConnected) {
            await fetch('/api/auth/logout', { method: 'POST' });
        }
    } catch (e) {}
    sessionStorage.removeItem('payroll_user');
    window.location.href = 'login.html';
}

// ================= TOAST NOTIFICATIONS =================
function showToast(message, isError = false) {
    const toastEl = document.getElementById('liveToast');
    const toastMsgEl = document.getElementById('toastMessage');
    if (!toastEl || !toastMsgEl) return;

    toastEl.classList.remove('bg-success', 'bg-danger', 'bg-primary');
    toastEl.classList.add(isError ? 'bg-danger' : 'bg-success');
    toastMsgEl.textContent = message;

    if (toastInstance) toastInstance.show();
}

// ================= NAVIGATION VIEW SWITCHING =================
function switchView(viewId, navElement) {
    document.querySelectorAll('.view-panel').forEach(p => p.classList.add('d-none'));
    const target = document.getElementById(viewId);
    if (target) target.classList.remove('d-none');

    document.querySelectorAll('.nav-link-custom').forEach(link => link.classList.remove('active'));
    if (navElement) navElement.classList.add('active');

    const titles = {
        dashboardView: 'Executive Overview',
        employeesView: 'Employee Management',
        attendanceView: 'Attendance & Leaves',
        payrollView: 'Run Payroll Calculation',
        payslipsView: 'Disbursed Payslip Archive'
    };
    const titleEl = document.getElementById('pageTitle');
    if (titleEl) titleEl.textContent = titles[viewId] || 'Dashboard';

    if (viewId === 'dashboardView') loadDashboardSummary();
    if (viewId === 'employeesView') loadEmployees();
    if (viewId === 'attendanceView') loadAttendanceList();
    if (viewId === 'payrollView') populatePayrollEmployeeDropdown();
    if (viewId === 'payslipsView') loadPayslipArchive();
}

function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) sidebar.classList.toggle('open');
}

function formatINR(val) {
    return '₹ ' + Number(val || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// ================= 1. DASHBOARD & KPIS =================
async function loadDashboardSummary() {
    const month = document.getElementById('dashMonthSelect')?.value || (new Date().getMonth() + 1);
    const year = document.getElementById('dashYearSelect')?.value || new Date().getFullYear();

    if (isBackendConnected) {
        try {
            const res = await fetch(`/api/payroll/summary?month=${month}&year=${year}`);
            if (res.ok) {
                const data = await res.json();
                document.getElementById('kpiActiveEmployees').textContent = data.totalActiveEmployees || 0;
                document.getElementById('kpiGrossSalary').textContent = formatINR(data.monthlyGrossPayroll);
                document.getElementById('kpiTotalDeductions').textContent = formatINR(data.monthlyTotalDeductions);
                document.getElementById('kpiNetDisbursed').textContent = formatINR(data.monthlyNetDisbursed);
                loadRecentPayroll(month, year);
                return;
            }
        } catch (e) {}
    }

    // Local Mock Calculation
    const activeEmps = mockEmployees.filter(e => e.active);
    const periodRecords = mockPayrollRecords.filter(r => r.month == month && r.year == year);
    const gross = periodRecords.reduce((sum, r) => sum + Number(r.grossSalary || 0), 0);
    const deductions = periodRecords.reduce((sum, r) => sum + Number(r.totalDeductions || 0), 0);
    const net = periodRecords.reduce((sum, r) => sum + Number(r.netSalary || 0), 0);

    document.getElementById('kpiActiveEmployees').textContent = activeEmps.length;
    document.getElementById('kpiGrossSalary').textContent = formatINR(gross);
    document.getElementById('kpiTotalDeductions').textContent = formatINR(deductions);
    document.getElementById('kpiNetDisbursed').textContent = formatINR(net);

    loadRecentPayroll(month, year);
}

async function loadRecentPayroll(month, year) {
    const tbody = document.getElementById('recentPayrollTableBody');
    if (!tbody) return;

    let list = [];
    if (isBackendConnected) {
        try {
            const res = await fetch(`/api/payroll?month=${month}&year=${year}`);
            if (res.ok) list = await res.json();
        } catch (e) {}
    }

    if (!isBackendConnected || list.length === 0) {
        list = mockPayrollRecords.filter(r => r.month == month && r.year == year);
    }

    if (list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted"><i class="bi bi-info-circle me-1"></i> No payroll generated for Month ${month}/${year}. Click "Run New Payroll" to disburse.</td></tr>`;
        return;
    }

    tbody.innerHTML = list.map(item => `
        <tr>
            <td><span class="badge bg-light text-dark border font-monospace">${escapeHtml(item.empCode)}</span></td>
            <td>
                <div class="fw-semibold text-dark">${escapeHtml(item.employeeName)}</div>
                <small class="text-muted">${escapeHtml(item.email)}</small>
            </td>
            <td>${escapeHtml(item.department)}</td>
            <td class="font-monospace text-success fw-semibold">${formatINR(item.grossSalary)}</td>
            <td class="font-monospace text-danger">${formatINR(item.totalDeductions)}</td>
            <td class="font-monospace fw-bold text-primary">${formatINR(item.netSalary)}</td>
            <td><span class="badge bg-success-subtle text-success border border-success-subtle">${item.paymentStatus}</span></td>
            <td class="text-end">
                <a href="payslip.html?id=${item.id}" target="_blank" class="btn btn-sm btn-outline-primary py-1 px-2">
                    <i class="bi bi-file-earmark-pdf me-1"></i> Payslip
                </a>
            </td>
        </tr>
    `).join('');
}

// ================= 2. EMPLOYEE CRUD =================
async function loadEmployees() {
    const tbody = document.getElementById('employeeTableBody');
    if (!tbody) return;

    if (isBackendConnected) {
        try {
            const res = await fetch('/api/employees');
            if (res.ok) {
                allEmployees = await res.json();
                renderEmployeeTable(allEmployees);
                populateEmployeeDropdowns();
                return;
            }
        } catch (e) {}
    }

    // Mock local store
    allEmployees = [...mockEmployees];
    renderEmployeeTable(allEmployees);
    populateEmployeeDropdowns();
}

function renderEmployeeTable(list) {
    const tbody = document.getElementById('employeeTableBody');
    if (!tbody) return;

    if (list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">No employees registered yet. Click "Add Employee" to create one.</td></tr>`;
        return;
    }

    tbody.innerHTML = list.map(emp => {
        const monthlyCtc = Number(emp.basicSalary || 0) + Number(emp.hra || 0) + Number(emp.specialAllowances || 0);
        return `
            <tr>
                <td><span class="badge bg-light text-dark border font-monospace">${escapeHtml(emp.empCode)}</span></td>
                <td>
                    <div class="fw-semibold text-dark">${escapeHtml(emp.fullName)}</div>
                    <small class="text-muted"><i class="bi bi-envelope me-1"></i>${escapeHtml(emp.email)}</small>
                </td>
                <td>
                    <div>${escapeHtml(emp.department)}</div>
                    <small class="text-muted">${escapeHtml(emp.designation)}</small>
                </td>
                <td class="font-monospace">${formatINR(emp.basicSalary)}</td>
                <td class="font-monospace text-muted">${formatINR(Number(emp.hra) + Number(emp.specialAllowances))}</td>
                <td class="font-monospace fw-bold text-primary">${formatINR(monthlyCtc)}</td>
                <td>
                    ${emp.active 
                        ? `<span class="badge-active"><i class="bi bi-check-circle me-1"></i>Active</span>`
                        : `<span class="badge-inactive"><i class="bi bi-dash-circle me-1"></i>Inactive</span>`}
                </td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-secondary me-1" onclick="openEditEmployeeModal(${emp.id})" title="Edit Profile">
                        <i class="bi bi-pencil"></i>
                    </button>
                    ${emp.active
                        ? `<button class="btn btn-sm btn-outline-warning me-1" onclick="toggleEmployeeStatus(${emp.id}, false)" title="Deactivate">
                            <i class="bi bi-person-x"></i>
                           </button>`
                        : `<button class="btn btn-sm btn-outline-success me-1" onclick="toggleEmployeeStatus(${emp.id}, true)" title="Activate">
                            <i class="bi bi-person-check"></i>
                           </button>`}
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteEmployee(${emp.id})" title="Delete Employee">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

function filterEmployees() {
    const q = (document.getElementById('employeeSearchInput')?.value || '').toLowerCase();
    const filtered = allEmployees.filter(e => 
        e.fullName.toLowerCase().includes(q) ||
        e.empCode.toLowerCase().includes(q) ||
        e.department.toLowerCase().includes(q) ||
        e.email.toLowerCase().includes(q)
    );
    renderEmployeeTable(filtered);
}

function openAddEmployeeModal() {
    document.getElementById('employeeModalTitle').textContent = 'Add New Employee';
    document.getElementById('employeeForm').reset();
    document.getElementById('empId').value = '';
    document.getElementById('empActive').checked = true;
    document.getElementById('empJoiningDate').value = new Date().toISOString().split('T')[0];
    if (employeeModalInstance) employeeModalInstance.show();
}

function openEditEmployeeModal(id) {
    const emp = allEmployees.find(e => e.id === id);
    if (!emp) return;

    document.getElementById('employeeModalTitle').textContent = 'Edit Employee Profile';
    document.getElementById('empId').value = emp.id;
    document.getElementById('empCode').value = emp.empCode;
    document.getElementById('empFullName').value = emp.fullName;
    document.getElementById('empEmail').value = emp.email;
    document.getElementById('empPhone').value = emp.phone;
    document.getElementById('empDepartment').value = emp.department;
    document.getElementById('empDesignation').value = emp.designation;
    document.getElementById('empJoiningDate').value = emp.joiningDate;
    document.getElementById('empBankAcc').value = emp.bankAccountNumber;
    document.getElementById('empIfsc').value = emp.ifscCode;
    document.getElementById('empPan').value = emp.panNumber;
    document.getElementById('empBasicSalary').value = emp.basicSalary;
    document.getElementById('empHra').value = emp.hra;
    document.getElementById('empSpecialAllowances').value = emp.specialAllowances;
    document.getElementById('empActive').checked = emp.active;

    if (employeeModalInstance) employeeModalInstance.show();
}

async function saveEmployee(e) {
    e.preventDefault();
    const id = document.getElementById('empId').value;
    const btn = document.getElementById('btnSaveEmployee');
    btn.disabled = true;

    const payload = {
        empCode: document.getElementById('empCode').value.trim(),
        fullName: document.getElementById('empFullName').value.trim(),
        email: document.getElementById('empEmail').value.trim(),
        phone: document.getElementById('empPhone').value.trim(),
        department: document.getElementById('empDepartment').value.trim(),
        designation: document.getElementById('empDesignation').value.trim(),
        joiningDate: document.getElementById('empJoiningDate').value,
        bankAccountNumber: document.getElementById('empBankAcc').value.trim(),
        ifscCode: document.getElementById('empIfsc').value.trim(),
        panNumber: document.getElementById('empPan').value.trim(),
        basicSalary: parseFloat(document.getElementById('empBasicSalary').value),
        hra: parseFloat(document.getElementById('empHra').value),
        specialAllowances: parseFloat(document.getElementById('empSpecialAllowances').value || 0),
        active: document.getElementById('empActive').checked
    };

    if (isBackendConnected) {
        try {
            const url = id ? `/api/employees/${id}` : '/api/employees';
            const method = id ? 'PUT' : 'POST';

            const res = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const errData = await res.json();
                throw new Error(errData.message || 'Operation failed');
            }

            showToast(id ? 'Employee updated successfully!' : 'Employee registered successfully!');
            if (employeeModalInstance) employeeModalInstance.hide();
            await loadEmployees();
            await loadDashboardSummary();
            return;
        } catch (err) {
            showToast(err.message, true);
        } finally {
            btn.disabled = false;
        }
    }

    // Local Mock update
    if (id) {
        const idx = mockEmployees.findIndex(e => e.id == id);
        if (idx !== -1) mockEmployees[idx] = { ...mockEmployees[idx], ...payload };
        showToast('Employee profile updated (Preview Mode)!');
    } else {
        const newId = Date.now();
        mockEmployees.push({ id: newId, ...payload });
        showToast('New employee registered (Preview Mode)!');
    }

    if (employeeModalInstance) employeeModalInstance.hide();
    await loadEmployees();
    await loadDashboardSummary();
    btn.disabled = false;
}

async function toggleEmployeeStatus(id, activate) {
    const action = activate ? 'activate' : 'deactivate';
    if (!confirm(`Are you sure you want to ${action} this employee?`)) return;

    if (isBackendConnected) {
        try {
            const res = await fetch(`/api/employees/${id}/${action}`, { method: 'PATCH' });
            if (!res.ok) throw new Error(`Failed to ${action} employee.`);
            showToast(`Employee ${action}d successfully.`);
            await loadEmployees();
            await loadDashboardSummary();
            return;
        } catch (err) {
            showToast(err.message, true);
        }
    }

    const emp = mockEmployees.find(e => e.id === id);
    if (emp) emp.active = activate;
    showToast(`Employee ${action}d successfully (Preview Mode).`);
    await loadEmployees();
    await loadDashboardSummary();
}

async function deleteEmployee(id) {
    if (!confirm('Are you sure you want to permanently delete this employee?')) return;

    if (isBackendConnected) {
        try {
            const res = await fetch(`/api/employees/${id}`, { method: 'DELETE' });
            if (!res.ok) throw new Error('Failed to delete employee.');
            showToast('Employee deleted successfully.');
            await loadEmployees();
            await loadDashboardSummary();
            return;
        } catch (err) {
            showToast(err.message, true);
        }
    }

    mockEmployees = mockEmployees.filter(e => e.id !== id);
    showToast('Employee deleted successfully (Preview Mode).');
    await loadEmployees();
    await loadDashboardSummary();
}

function populateEmployeeDropdowns() {
    populatePayrollEmployeeDropdown();
    populateAttendanceEmployeeDropdown();
}

// ================= 3. ATTENDANCE & LEAVES =================
async function loadAttendanceList() {
    const month = document.getElementById('attMonthFilter')?.value;
    const year = document.getElementById('attYearFilter')?.value;
    const tbody = document.getElementById('attendanceTableBody');
    if (!tbody) return;

    let list = [];
    if (isBackendConnected) {
        try {
            const res = await fetch(`/api/attendance?month=${month}&year=${year}`);
            if (res.ok) list = await res.json();
        } catch (e) {}
    }

    if (!isBackendConnected || list.length === 0) {
        list = mockAttendance.filter(a => a.month == month && a.year == year);
    }

    if (list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">No attendance recorded for Month ${month}/${year}. Click "Record Attendance" to add.</td></tr>`;
        return;
    }

    tbody.innerHTML = list.map(item => `
        <tr>
            <td>
                <div class="fw-semibold text-dark">${escapeHtml(item.employeeName)}</div>
                <small class="badge bg-light text-dark border font-monospace">${escapeHtml(item.empCode)}</small>
            </td>
            <td><span class="badge bg-secondary-subtle text-secondary font-monospace">${item.month}/${item.year}</span></td>
            <td><span class="fw-semibold">${item.totalWorkingDays}</span> Days</td>
            <td><span class="badge bg-success-subtle text-success">${item.daysPresent} Present</span></td>
            <td>
                ${item.unpaidLeaves > 0 
                    ? `<span class="badge bg-danger-subtle text-danger">${item.unpaidLeaves} LOP Days</span>`
                    : `<span class="text-muted">0</span>`}
            </td>
            <td>
                ${item.overtimeHours > 0 
                    ? `<span class="badge bg-primary-subtle text-primary">${item.overtimeHours} hrs</span>`
                    : `<span class="text-muted">0.0</span>`}
            </td>
            <td class="text-end">
                <button class="btn btn-sm btn-outline-danger" onclick="deleteAttendance(${item.id})" title="Delete Record">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function populateAttendanceEmployeeDropdown() {
    const select = document.getElementById('attEmployeeSelect');
    if (!select) return;

    const activeList = allEmployees.filter(e => e.active);
    select.innerHTML = '<option value="">-- Choose Employee --</option>' +
        activeList.map(e => `<option value="${e.id}">${escapeHtml(e.empCode)} - ${escapeHtml(e.fullName)} (${escapeHtml(e.department)})</option>`).join('');
}

function openRecordAttendanceModal() {
    document.getElementById('attendanceForm').reset();
    document.getElementById('attId').value = '';
    const curMonth = (new Date().getMonth() + 1).toString();
    const curYear = new Date().getFullYear().toString();
    document.getElementById('attMonthSelect').value = curMonth;
    document.getElementById('attYearInput').value = curYear;
    document.getElementById('attTotalWorkingDays').value = 22;
    document.getElementById('attDaysPresent').value = 22;
    document.getElementById('attUnpaidLeaves').value = 0;
    document.getElementById('attOvertimeHours').value = 0;
    populateAttendanceEmployeeDropdown();
    if (attendanceModalInstance) attendanceModalInstance.show();
}

async function saveAttendance(e) {
    e.preventDefault();
    const btn = document.getElementById('btnSaveAttendance');
    btn.disabled = true;

    const empId = parseInt(document.getElementById('attEmployeeSelect').value);
    const emp = allEmployees.find(e => e.id === empId);

    const payload = {
        employeeId: empId,
        month: parseInt(document.getElementById('attMonthSelect').value),
        year: parseInt(document.getElementById('attYearInput').value),
        totalWorkingDays: parseInt(document.getElementById('attTotalWorkingDays').value),
        daysPresent: parseInt(document.getElementById('attDaysPresent').value),
        unpaidLeaves: parseInt(document.getElementById('attUnpaidLeaves').value || 0),
        overtimeHours: parseFloat(document.getElementById('attOvertimeHours').value || 0)
    };

    if (isBackendConnected) {
        try {
            const res = await fetch('/api/attendance', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.message || 'Failed to save attendance.');
            }

            showToast('Attendance logged successfully!');
            if (attendanceModalInstance) attendanceModalInstance.hide();
            await loadAttendanceList();
            return;
        } catch (err) {
            showToast(err.message, true);
        } finally {
            btn.disabled = false;
        }
    }

    // Mock local attendance save
    mockAttendance.push({
        id: Date.now(),
        employeeId: empId,
        employeeName: emp ? emp.fullName : 'Employee',
        empCode: emp ? emp.empCode : 'EMP-000',
        ...payload
    });
    showToast('Attendance logged (Preview Mode)!');
    if (attendanceModalInstance) attendanceModalInstance.hide();
    await loadAttendanceList();
    btn.disabled = false;
}

async function deleteAttendance(id) {
    if (!confirm('Are you sure you want to delete this attendance record?')) return;

    if (isBackendConnected) {
        try {
            const res = await fetch(`/api/attendance/${id}`, { method: 'DELETE' });
            if (!res.ok) throw new Error('Failed to delete attendance record.');
            showToast('Attendance record removed.');
            await loadAttendanceList();
            return;
        } catch (err) {
            showToast(err.message, true);
        }
    }

    mockAttendance = mockAttendance.filter(a => a.id !== id);
    showToast('Attendance record removed (Preview Mode).');
    await loadAttendanceList();
}

// ================= 4. RUN PAYROLL ENGINE =================
function populatePayrollEmployeeDropdown() {
    const select = document.getElementById('payrollEmployeeSelect');
    if (!select) return;

    const activeList = allEmployees.filter(e => e.active);
    select.innerHTML = '<option value="">⚡ All Active Employees (Batch Processing)</option>' +
        activeList.map(e => `<option value="${e.id}">${escapeHtml(e.empCode)} - ${escapeHtml(e.fullName)}</option>`).join('');
}

async function handleRunPayroll(e) {
    e.preventDefault();
    const btn = document.getElementById('btnRunPayroll');
    const spinner = document.getElementById('payrollSpinner');
    const empSelectVal = document.getElementById('payrollEmployeeSelect').value;
    const month = parseInt(document.getElementById('payrollMonthSelect').value);
    const year = parseInt(document.getElementById('payrollYearInput').value);

    const payload = {
        employeeId: empSelectVal ? parseInt(empSelectVal) : null,
        month: month,
        year: year
    };

    btn.disabled = true;
    spinner.classList.remove('d-none');

    if (isBackendConnected) {
        try {
            const res = await fetch('/api/payroll/generate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await res.json();
            if (!res.ok) throw new Error(data.message || 'Failed to generate payroll.');

            const count = Array.isArray(data) ? data.length : 1;
            showToast(`Disbursed payroll for ${count} employee(s)!`);
            switchView('payslipsView', document.querySelector('[onclick*=\'payslipsView\']'));
            await loadDashboardSummary();
            return;
        } catch (err) {
            showToast(err.message, true);
        } finally {
            btn.disabled = false;
            spinner.classList.add('d-none');
        }
    }

    // Local Mock Calculation
    const targetEmployees = payload.employeeId 
        ? mockEmployees.filter(e => e.id === payload.employeeId)
        : mockEmployees.filter(e => e.active);

    let generatedCount = 0;
    targetEmployees.forEach(emp => {
        // check duplicate
        const exists = mockPayrollRecords.some(r => r.employeeId === emp.id && r.month === month && r.year === year);
        if (exists && payload.employeeId) {
            showToast(`Payroll already processed for ${emp.fullName} for this period.`, true);
            return;
        }
        if (exists) return;

        const att = mockAttendance.find(a => a.employeeId === emp.id && a.month === month && a.year === year);
        const totalDays = att ? att.totalWorkingDays : 22;
        const unpaidLeaves = att ? att.unpaidLeaves : 0;
        const otHours = att ? att.overtimeHours : 0;

        const hourlyRate = (emp.basicSalary / (totalDays * 8));
        const overtimePay = hourlyRate * otHours * 1.5;
        const grossSalary = emp.basicSalary + emp.hra + emp.specialAllowances + overtimePay;

        const lopDeduction = (grossSalary / totalDays) * unpaidLeaves;
        const pfDeduction = emp.basicSalary * 0.12;
        const ptDeduction = 200.00;
        const annualGross = grossSalary * 12;
        const annualTax = Math.max(0, (annualGross - 300000) * 0.10); // simplified progressive preview
        const tdsDeduction = annualTax / 12;

        const totalDeductions = lopDeduction + pfDeduction + ptDeduction + tdsDeduction;
        const netSalary = Math.max(0, grossSalary - totalDeductions);

        mockPayrollRecords.unshift({
            id: Date.now() + Math.floor(Math.random() * 1000),
            employeeId: emp.id,
            empCode: emp.empCode,
            employeeName: emp.fullName,
            email: emp.email,
            department: emp.department,
            designation: emp.designation,
            bankAccountNumber: emp.bankAccountNumber,
            ifscCode: emp.ifscCode,
            panNumber: emp.panNumber,
            month: month,
            year: year,
            monthName: getMonthName(month),
            basicSalary: emp.basicSalary,
            hra: emp.hra,
            specialAllowances: emp.specialAllowances,
            overtimePay: overtimePay,
            grossSalary: grossSalary,
            lopDeduction: lopDeduction,
            pfDeduction: pfDeduction,
            ptDeduction: ptDeduction,
            tdsDeduction: tdsDeduction,
            totalDeductions: totalDeductions,
            netSalary: netSalary,
            netSalaryInWords: "Disbursed via Direct Bank Deposit",
            totalWorkingDays: totalDays,
            daysPresent: totalDays - unpaidLeaves,
            unpaidLeaves: unpaidLeaves,
            overtimeHours: otHours,
            paymentStatus: "PROCESSED",
            processedDate: new Date().toISOString().split('T')[0]
        });
        generatedCount++;
    });

    btn.disabled = false;
    spinner.classList.add('d-none');

    if (generatedCount > 0) {
        showToast(`Disbursed payroll for ${generatedCount} employee(s) (Preview Mode)!`);
        switchView('payslipsView', document.querySelector('[onclick*=\'payslipsView\']'));
        await loadDashboardSummary();
    } else {
        showToast('All target employees already have payroll generated for this period.', true);
    }
}

// ================= 5. PAYSLIP ARCHIVE =================
async function loadPayslipArchive() {
    const month = document.getElementById('payslipMonthFilter')?.value;
    const year = document.getElementById('payslipYearFilter')?.value;
    const tbody = document.getElementById('payslipArchiveTableBody');
    if (!tbody) return;

    let list = [];
    if (isBackendConnected) {
        let url = '/api/payroll';
        if (month && year) url += `?month=${month}&year=${year}`;
        try {
            const res = await fetch(url);
            if (res.ok) list = await res.json();
        } catch (e) {}
    }

    if (!isBackendConnected || list.length === 0) {
        list = mockPayrollRecords.filter(r => {
            if (month && r.month != month) return false;
            if (year && r.year != year) return false;
            return true;
        });
    }

    if (list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">No payslip records found for the selected filter.</td></tr>`;
        return;
    }

    tbody.innerHTML = list.map(item => `
        <tr>
            <td><span class="badge bg-light text-dark border font-monospace">${escapeHtml(item.empCode)}</span></td>
            <td>
                <div class="fw-semibold text-dark">${escapeHtml(item.employeeName)}</div>
                <small class="text-muted">${escapeHtml(item.department)} • ${escapeHtml(item.designation)}</small>
            </td>
            <td><span class="badge bg-primary-subtle text-primary font-monospace">${item.monthName || getMonthName(item.month)} ${item.year}</span></td>
            <td class="font-monospace text-success fw-semibold">${formatINR(item.grossSalary)}</td>
            <td class="font-monospace text-danger">${formatINR(item.totalDeductions)}</td>
            <td class="font-monospace fw-bolder text-primary">${formatINR(item.netSalary)}</td>
            <td><small class="text-muted">${item.processedDate || '-'}</small></td>
            <td class="text-end">
                <a href="payslip.html?id=${item.id}" target="_blank" class="btn btn-sm btn-outline-primary py-1 px-2" title="View & Print Official Payslip">
                    <i class="bi bi-printer me-1"></i> View / Print
                </a>
                <button class="btn btn-sm btn-outline-danger ms-1 py-1 px-2" onclick="deletePayrollRecord(${item.id})" title="Delete Record">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

async function deletePayrollRecord(id) {
    if (!confirm('Are you sure you want to delete this payroll record?')) return;

    if (isBackendConnected) {
        try {
            const res = await fetch(`/api/payroll/${id}`, { method: 'DELETE' });
            if (!res.ok) throw new Error('Failed to delete payroll record.');
            showToast('Payroll record deleted.');
            await loadPayslipArchive();
            await loadDashboardSummary();
            return;
        } catch (err) {
            showToast(err.message, true);
        }
    }

    mockPayrollRecords = mockPayrollRecords.filter(r => r.id !== id);
    showToast('Payroll record deleted (Preview Mode).');
    await loadPayslipArchive();
    await loadDashboardSummary();
}

function getMonthName(m) {
    const months = ["January","February","March","April","May","June","July","August","September","October","November","December"];
    return months[parseInt(m) - 1] || "Month";
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
