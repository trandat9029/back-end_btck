/**
 * Copyright(C) 2026 - Luvina
 * [EmployeeServiceImpl.java], 24/04/2026 [tranledat]
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.exception.CustomException;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.EmployeeDetailResponse;
import com.luvina.la.payload.response.EmployeeDetailResponse.EmployeeCertificationDetailResponse;
import com.luvina.la.payload.response.MessageResponse;
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
     * @param obj Object co the la java.sql.Date hoac LocalDate.
     * @return LocalDate sau khi chuyen doi.
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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * Thực hiện thêm mới nhân viên.
     * @param request Dữ liệu nhân viên từ form.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEmployee(EmployeeRequest request) {
        // 1. Thực hiện validate dữ liệu lại một lần nữa để đảm bảo tính toàn vẹn
        List<MessageResponse> errors = employeeValidate.validate(request);
        if (!errors.isEmpty()) {
            throw new CustomException(errors.get(0).getCode());
        }

        // 2. Khởi tạo đối tượng nhân viên mới
        Employee employee = new Employee();
            employee.setEmployeeLoginId(request.getEmployeeLoginId());
        }

        // 3. Chuyển đổi dữ liệu từ Request sang Entity Employee
        employee.setEmployeeName(request.getEmployeeName());
        employee.setEmployeeNameKana(request.getEmployeeNameKana());
        employee.setEmployeeBirthDate(LocalDate.parse(request.getEmployeeBirthDate(), dateFormatter));
        employee.setEmployeeEmail(request.getEmployeeEmail());
        employee.setEmployeeTelephone(request.getEmployeeTelephone());
        
        // Chỉ cập nhật password nếu request có gửi password mới (thường dùng cho add mới)
        if (request.getEmployeeLoginPassword() != null && !request.getEmployeeLoginPassword().isEmpty()) {
            employee.setEmployeeLoginPassword(request.getEmployeeLoginPassword());
        }

        // Set Department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new CustomException("ER004", "departmentId"));
        employee.setDepartment(department);

        // 4. Lưu thông tin Employee (Lưu xong sẽ có ID nếu là thêm mới)
        Employee savedEmployee = employeeRepository.save(employee);

        // 5. Lưu thông tin chứng chỉ liên quan
        saveCertification(savedEmployee, request);
    }

    /**
     * Hàm dùng chung để lưu thông tin chứng chỉ của nhân viên.
     * @param savedEmployee Thông tin nhân viên đã lưu.
     * @param request Dữ liệu từ form.
     */
    private void saveCertification(Employee savedEmployee, EmployeeRequest request) {
        if (request.getCertificationId() != null) {
            Certification certification = certificationRepository.findById(request.getCertificationId())
                    .orElseThrow(() -> new CustomException("ER004", "certificationId"));

            EmployeeCertification empCert = new EmployeeCertification();
            empCert.setEmployee(savedEmployee);
            empCert.setCertification(certification);
            empCert.setStartDate(LocalDate.parse(request.getCertificationStartDate(), dateFormatter));
            empCert.setEndDate(LocalDate.parse(request.getCertificationEndDate(), dateFormatter));
            empCert.setScore(new BigDecimal(request.getEmployeeCertificationScore()));

            employeeCertificationRepository.save(empCert);
        }
    }

    /**
     * Lấy thông tin chi tiết nhân viên theo ID.
     * @param employeeId ID của nhân viên.
     * @return Đối tượng chứa thông tin chi tiết nhân viên.
     */
    @Override
    public EmployeeDetailResponse getEmployeeById(Long employeeId) {
        // 1. Tìm nhân viên theo ID
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomException("ER013", "ID"));

        // 2. Khởi tạo đối tượng Response
        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setCode("200");
        response.setEmployeeId(employee.getEmployeeId());
        response.setEmployeeName(employee.getEmployeeName());
        response.setEmployeeNameKana(employee.getEmployeeNameKana());
        response.setEmployeeBirthDate(employee.getEmployeeBirthDate().format(dateFormatter));
        response.setEmployeeEmail(employee.getEmployeeEmail());
        response.setEmployeeTelephone(employee.getEmployeeTelephone());
        response.setEmployeeLoginId(employee.getEmployeeLoginId());

        // Set Department info
        if (employee.getDepartment() != null) {
            response.setDepartmentId(employee.getDepartment().getDepartmentId());
            response.setDepartmentName(employee.getDepartment().getDepartmentName());
        }

        // 3. Lấy danh sách chứng chỉ của nhân viên
        List<EmployeeCertification> certs = employeeCertificationRepository.findCertsByEmployeeId(employeeId);
        
        List<EmployeeCertificationDetailResponse> certDetails = certs.stream().map(c -> {
            EmployeeCertificationDetailResponse certDetail = new EmployeeCertificationDetailResponse();
            certDetail.setCertificationId(c.getCertification().getCertificationId());
            certDetail.setCertificationName(c.getCertification().getCertificationName());
            certDetail.setStartDate(c.getStartDate().format(dateFormatter));
            certDetail.setEndDate(c.getEndDate().format(dateFormatter));
            certDetail.setScore(c.getScore().intValue()); // Score trong Entity là BigDecimal, Response cần Integer
            return certDetail;
        }).collect(Collectors.toList());

        response.setCertifications(certDetails);

        return response;
    }
}
