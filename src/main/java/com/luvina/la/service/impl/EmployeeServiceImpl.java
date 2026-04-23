/**
 * Copyright(C) 2026 Luvina
 * [EmployeeServiceImpl.java], 23/04/2026 tranledat
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeCertificationRepository;
import com.luvina.la.repository.EmployeeRepository;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.EmployeeValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp triển khai các phương thức xử lý nghiệp vụ liên quan đến nhân viên.
 * Thực hiện các thao tác với Database thông qua các Repository.
 *
 * @author tranledat
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;
    private final EmployeeCertificationRepository employeeCertificationRepository;
    private final EmployeeValidate employeeValidate;

    /**
     * Lay danh sach nhan vien cho ADM002 theo dieu kien tim kiem, sap xep va phan trang.
     * Luong xu ly cua method nay:
     * - Escape cac ky tu dac biet trong chuoi tim kiem de dung an toan voi LIKE.
     * - Goi repository de thuc thi native query.
     * - Mapping tung dong Object[] tra ve thanh EmployeeDTO de controller co the tra JSON.
     *
     * @param employeeName Ten nhan vien dung de tim kiem.
     * @param departmentId Ma phong ban dung de loc.
     * @param sortEmployeeName Thu tu sap xep theo ten nhan vien.
     * @param sortCertificationName Thu tu sap xep theo ten chung chi.
     * @param sortEndDate Thu tu sap xep theo ngay het han chung chi.
     * @param limit So luong ban ghi tren mot trang.
     * @param offset Vi tri bat dau ban ghi.
     * @return Danh sach nhan vien.
     */
    @Override
    public List<EmployeeDTO> getListEmployee(
            String employeeName,
            Long departmentId,
            String sortEmployeeName,
            String sortCertificationName,
            String sortEndDate,
            Integer limit,
            Integer offset) {
        String escapedEmployeeName = escapeLikePattern(employeeName);
        List<Object[]> rows = employeeRepository.getListEmployee(
                escapedEmployeeName,
                departmentId,
                sortEmployeeName,
                sortCertificationName,
                sortEndDate,
                limit,
                offset);

        return rows.stream()
                .map(row -> new EmployeeDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        convertSqlDateToLocalDate(row[2]),
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        (String) row[6],
                        convertSqlDateToLocalDate(row[7]),
                        row[8] != null ? ((Number) row[8]).doubleValue() : null
                ))
                .collect(Collectors.toList());
    }

    /**
     * Dem tong so nhan vien thoa dieu kien tim kiem.
     * Method nay duoc tach rieng de controller tinh duoc tong so ban ghi cho paging
     * truoc khi lay danh sach chi tiet.
     *
     * @param employeeName Ten nhan vien dung de tim kiem.
     * @param departmentId Ma phong ban dung de loc.
     * @return Tong so nhan vien thoa dieu kien.
     */
    @Override
    public Long countEmployeesWithFilter(String employeeName, Long departmentId) {
        return employeeRepository.countEmployeesWithFilter(
                escapeLikePattern(employeeName),
                departmentId
        );
    }

    /**
     * Kiểm tra xem tài khoản (loginId) đã tồn tại trong hệ thống chưa.
     * Logic: Tìm nhân viên theo loginId, nếu thấy và ID khác với ID hiện tại thì coi là trùng.
     *
     * @param loginId Tên tài khoản cần kiểm tra.
     * @param employeeId ID của nhân viên hiện tại để loại trừ (khi update).
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    @Override
    public boolean checkExistsEmployeeByLoginId(String loginId, Long employeeId) {
        return employeeRepository.findByEmployeeLoginId(loginId)
                .map(employee -> !employee.getEmployeeId().equals(employeeId))
                .orElse(false);
    }

    /**
     * Escape ky tu dac biet cho menh de LIKE trong SQL.
     * Muc dich:
     * - Neu nguoi dung nhap % hoac _ thi phai tim nhu ky tu thuong, khong phai wildcard.
     * - Neu chuoi co dau \ thi cung phai duoc escape dung de tranh sai cu phap.
     *
     * @param value Chuoi tim kiem dau vao.
     * @return Chuoi da duoc escape cho menh de LIKE.
     */
    private String escapeLikePattern(String value) {
        if (value == null) {
            return null;
        }

        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * Chuyen gia tri ngay tra ve tu native query sang LocalDate.
     * Method nay duoc dung khi mapping Object[] sang EmployeeDTO vi native query
     * co the tra ve kieu java.sql.Date hoac da la LocalDate tuy theo JDBC driver.
     *
     * @param obj Object co the la java.sql.Date hoac LocalDate.
     * @return LocalDate sau khi chuyen doi, hoac null neu khong chuyen doi duoc.
     */
    private LocalDate convertSqlDateToLocalDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return ((Date) obj).toLocalDate();
        }
        if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        }
        return null;
    }

    /**
     * Thực hiện thêm mới nhân viên.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEmployee(EmployeeRequest request) {
        // 1. Re-validate dữ liệu (Double check)
        List<EmployeeValidate.ErrorItem> errors = employeeValidate.validate(request);
        if (!errors.isEmpty()) {
            throw new RuntimeException("Validation failed: " + errors.get(0).code);
        }

        // 2. Chuyển đổi DTO sang Entity Employee (Tạo mới)
        Employee employee = new Employee();
        employee.setEmployeeLoginId(request.getEmployeeLoginId());
        employee.setEmployeeName(request.getEmployeeName());
        employee.setEmployeeNameKana(request.getEmployeeNameKana());
        employee.setEmployeeBirthDate(LocalDate.parse(request.getEmployeeBirthDate()));
        employee.setEmployeeEmail(request.getEmployeeEmail());
        employee.setEmployeeTelephone(request.getEmployeeTelephone());
        employee.setEmployeeLoginPassword(request.getEmployeeLoginPassword());

        // Set Department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));
        employee.setDepartment(department);

        // 3. Lưu thông tin Employee
        Employee savedEmployee = employeeRepository.save(employee);

        // 4. Lưu thông tin Chứng chỉ (nếu có chọn)
        saveCertification(savedEmployee, request);
    }

    /**
     * Hàm dùng chung để lưu thông tin chứng chỉ của nhân viên.
     */
    private void saveCertification(Employee savedEmployee, EmployeeRequest request) {
        if (request.getCertificationId() != null && !request.getCertificationId().toString().isEmpty()) {
            Certification certification = certificationRepository.findById(request.getCertificationId())
                    .orElseThrow(() -> new RuntimeException("Certification not found"));

            EmployeeCertification empCert = new EmployeeCertification();
            empCert.setEmployee(savedEmployee);
            empCert.setCertification(certification);
            empCert.setStartDate(LocalDate.parse(request.getCertificationStartDate()));
            empCert.setEndDate(LocalDate.parse(request.getCertificationEndDate()));
            empCert.setScore(new BigDecimal(request.getEmployeeCertificationScore()));

            employeeCertificationRepository.save(empCert);
        }
    }
}
