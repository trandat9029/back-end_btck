/**
 * Copyright(C) 2026 - Luvina
 * [EmployeeServiceImpl.java], 24/04/2026 tranledat
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
import com.luvina.la.config.Constants;
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
     * Lấy danh sách nhân viên cho ADM002 theo điều kiện tìm kiếm, sắp xếp và phân trang.
     * @param employeeName Tên nhân viên dùng để tìm kiếm.
     * @param departmentId Mã phòng ban dùng để lọc.
     * @param sortEmployeeName Thứ tự sắp xếp theo tên nhân viên.
     * @param sortCertificationName Thứ tự sắp xếp theo tên chứng chỉ.
     * @param sortEndDate Thứ tự sắp xếp theo ngày hết hạn chứng chỉ.
     * @param limit Số lượng bản ghi trên một trang.
     * @param offset Vị trí bắt đầu bản ghi.
     * @return Danh sách nhân viên.
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
                        row[3] != null ? ((Number) row[3]).longValue() : null,
                        (String) row[4],
                        (String) row[5],
                        (String) row[6],
                        (String) row[7],
                        (String) row[8],
                        (String) row[9],
                        convertSqlDateToLocalDate(row[10]),
                        row[11] != null ? ((Number) row[11]).doubleValue() : null
                ))
                .collect(Collectors.toList());
    }

    /**
     * Đếm tổng số lượng nhân viên thỏa mãn điều kiện lọc.
     * @param employeeName Tên nhân viên dùng để tìm kiếm.
     * @param departmentId Mã phòng ban dùng để lọc.
     * @return Tổng số lượng nhân viên.
     */
    @Override
    public Long countEmployeesWithFilter(String employeeName, Long departmentId) {
        return employeeRepository.countEmployeesWithFilter(
                escapeLikePattern(employeeName),
                departmentId
        );
    }

    /**
     * Kiểm tra xem Login ID đã tồn tại trong hệ thống chưa (trừ nhân viên hiện tại nếu đang update).
     * @param loginId Tên đăng nhập cần kiểm tra.
     * @param employeeId ID nhân viên hiện tại (null nếu là thêm mới).
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    @Override
    public boolean checkExistsEmployeeByLoginId(String loginId, Long employeeId) {
        return employeeRepository.findByEmployeeLoginId(loginId)
                .map(employee -> !employee.getEmployeeId().equals(employeeId))
                .orElse(false);
    }

    /**
     * Kiểm tra xem ID nhân viên có tồn tại trong hệ thống hay không.
     * @param employeeId ID nhân viên cần kiểm tra.
     * @return true nếu tồn tại, false nếu không.
     */
    @Override
    public boolean checkExistsEmployeeById(Long employeeId) {
        return employeeRepository.existsById(employeeId);
    }

    /**
     * Escape ký tự đặc biệt cho mệnh đề LIKE trong SQL.
     * @param value Chuỗi tìm kiếm đầu vào.
     * @return Chuỗi đã được escape cho mệnh đề LIKE.
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
     * Chuyển giá trị ngày trả về từ native query sang LocalDate.
     * @param obj Object có thể là java.sql.Date hoặc LocalDate.
     * @return LocalDate sau khi chuyển đổi.
     */
    private LocalDate convertSqlDateToLocalDate(Object obj) {
        if (obj == null) return null;
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
     * Thực hiện thêm mới nhân viên vào cơ sở dữ liệu.
     * @param request Đối tượng EmployeeRequest chứa thông tin nhân viên từ form nhập liệu.
     * @return ID của nhân viên vừa được thêm mới.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEmployee(EmployeeRequest request) {
        // 1. Thực hiện validate dữ liệu
        validateRequest(request);

        // 2. Khởi tạo Entity mới
        Employee employee = new Employee();
        employee.setEmployeeLoginId(request.getEmployeeLoginId());

        // 3. Map dữ liệu từ request sang entity và lưu
        mapRequestToEntity(request, employee);
        Employee persistedEmployee = employeeRepository.save(employee);

        // 4. Lưu chứng chỉ
        if (request.getCertificationId() != null) {
            Certification certification = certificationRepository.findById(request.getCertificationId())
                    .orElseThrow(() -> new CustomException(Constants.CODE_ER004, "certificationId"));

            EmployeeCertification employeeCertification = new EmployeeCertification();
            employeeCertification.setEmployee(persistedEmployee);
            employeeCertification.setCertification(certification);
            employeeCertification.setStartDate(LocalDate.parse(request.getCertificationStartDate(), dateFormatter));
            employeeCertification.setEndDate(LocalDate.parse(request.getCertificationEndDate(), dateFormatter));
            employeeCertification.setScore(new BigDecimal(request.getEmployeeCertificationScore()));

            employeeCertificationRepository.save(employeeCertification);
        }
        
        return persistedEmployee.getEmployeeId();
    }

    /**
     * Thực hiện cập nhật thông tin nhân viên vào cơ sở dữ liệu.
     * @param request Đối tượng EmployeeRequest chứa thông tin nhân viên cần cập nhật.
     * @return ID của nhân viên vừa được cập nhật.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateEmployee(EmployeeRequest request) {
        // 1. Thực hiện validate dữ liệu
        validateRequest(request);

        // 2. Tìm nhân viên hiện tại
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new CustomException(Constants.CODE_ER013, "ID"));

        // 3. Map dữ liệu từ request sang entity và lưu
        mapRequestToEntity(request, employee);
        Employee persistedEmployee = employeeRepository.save(employee);

        // 4. Xử lý chứng chỉ: Xóa cũ và thêm mới
        employeeCertificationRepository.deleteByEmployeeEmployeeId(request.getEmployeeId());
        if (request.getCertificationId() != null) {
            Certification certification = certificationRepository.findById(request.getCertificationId())
                    .orElseThrow(() -> new CustomException(Constants.CODE_ER004, "certificationId"));

            EmployeeCertification employeeCertification = new EmployeeCertification();
            employeeCertification.setEmployee(persistedEmployee);
            employeeCertification.setCertification(certification);
            employeeCertification.setStartDate(LocalDate.parse(request.getCertificationStartDate(), dateFormatter));
            employeeCertification.setEndDate(LocalDate.parse(request.getCertificationEndDate(), dateFormatter));
            employeeCertification.setScore(new BigDecimal(request.getEmployeeCertificationScore()));

            employeeCertificationRepository.save(employeeCertification);
        }
        
        return persistedEmployee.getEmployeeId();
    }

    /**
     * Thực hiện validate request và ném CustomException nếu có lỗi.
     * @param request Request cần validate.
     */
    private void validateRequest(EmployeeRequest request) {
        List<MessageResponse> errors = employeeValidate.validate(request);
        if (!errors.isEmpty()) {
            throw new CustomException(errors.get(0).getCode());
        }
    }

    /**
     * Map dữ liệu từ EmployeeRequest sang Entity Employee.
     * @param request Dữ liệu từ form.
     * @param employee Entity nhân viên cần gán giá trị.
     */
    private void mapRequestToEntity(EmployeeRequest request, Employee employee) {
        employee.setEmployeeName(request.getEmployeeName());
        employee.setEmployeeNameKana(request.getEmployeeNameKana());
        employee.setEmployeeBirthDate(LocalDate.parse(request.getEmployeeBirthDate(), dateFormatter));
        employee.setEmployeeEmail(request.getEmployeeEmail());
        employee.setEmployeeTelephone(request.getEmployeeTelephone());
        
        // Chỉ thiết lập mật khẩu và Login ID nếu là thêm mới nhân viên (ID chưa tồn tại)
        if (employee.getEmployeeId() == null) {
            employee.setEmployeeLoginId(request.getEmployeeLoginId());
            employee.setEmployeeLoginPassword(request.getEmployeeLoginPassword());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new CustomException(Constants.CODE_ER004, "departmentId"));
        employee.setDepartment(department);
    }

    /**
     * Thực hiện xóa thông tin nhân viên khỏi cơ sở dữ liệu dựa trên ID.
     * Trước khi xóa nhân viên, sẽ thực hiện xóa các bản ghi liên quan trong bảng chứng chỉ.
     * @param employeeId Mã ID của nhân viên cần thực hiện xóa.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomException(Constants.CODE_ER013, "ID"));

        employeeCertificationRepository.deleteByEmployeeEmployeeId(employeeId);
        employeeRepository.delete(employee);
    }


    /**
     * Lấy thông tin chi tiết của một nhân viên dựa trên mã ID nhân viên.
     * @param employeeId Mã ID của nhân viên cần lấy thông tin chi tiết.
     * @return EmployeeDetailResponse Đối tượng chứa toàn bộ thông tin chi tiết và danh sách chứng chỉ của nhân viên.
     */
    @Override
    public EmployeeDetailResponse getEmployeeById(Long employeeId) {
        // 1. Tìm nhân viên theo ID
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomException(Constants.CODE_ER013, "ID"));

        // 2. Khởi tạo đối tượng Response
        EmployeeDetailResponse employeeDetailResponse = new EmployeeDetailResponse();
        employeeDetailResponse.setCode("200");
        employeeDetailResponse.setEmployeeId(employee.getEmployeeId());
        employeeDetailResponse.setEmployeeName(employee.getEmployeeName());
        employeeDetailResponse.setEmployeeNameKana(employee.getEmployeeNameKana());
        employeeDetailResponse.setEmployeeBirthDate(employee.getEmployeeBirthDate().format(dateFormatter));
        employeeDetailResponse.setEmployeeEmail(employee.getEmployeeEmail());
        employeeDetailResponse.setEmployeeTelephone(employee.getEmployeeTelephone());
        employeeDetailResponse.setEmployeeLoginId(employee.getEmployeeLoginId());

        // Set Department info
        if (employee.getDepartment() != null) {
            employeeDetailResponse.setDepartmentId(employee.getDepartment().getDepartmentId());
            employeeDetailResponse.setDepartmentName(employee.getDepartment().getDepartmentName());
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

        employeeDetailResponse.setCertifications(certDetails);

        return employeeDetailResponse;
    }
}
