/**
 * Copyright(C) 2026 Luvina Software
 * EmployeeServiceImpl.java, 09/04/2026 tranledat
 */
package com.luvina.la.service.impl;

import com.luvina.la.common.utils.CommonUtils;
import com.luvina.la.common.validate.ValidatorUtils;
import com.luvina.la.constants.AppConstants;
import com.luvina.la.constants.MessageCode;
import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.exception.BaseException;
import com.luvina.la.mapper.EmployeeMapper;
import com.luvina.la.payload.request.CertificationRequest;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.EmployeeResponse;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeCertificationRepository;
import com.luvina.la.repository.EmployeeRepository;
import com.luvina.la.service.EmployeeService;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp triển khai các nghiệp vụ liên quan đến nhân viên.
 * Sử dụng Native SQL để tối ưu hiệu năng truy vấn và xử lý logic nghiệp vụ phức tạp.
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
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;

    /**
     * Lấy tổng số bản ghi nhân viên dựa trên điều kiện tìm kiếm.
     * 
     * @param employeeName Tên nhân viên (hỗ trợ tìm kiếm partial/LIKE)
     * @param departmentId ID phòng ban cần lọc
     * @return Tổng số lượng nhân viên thỏa mãn điều kiện (Long)
     */
    @Override
    public Long getTotalRecords(String employeeName, Long departmentId) {
        String escapedName = CommonUtils.escapeLike(employeeName);
        return employeeRepository.countEmployees(escapedName, departmentId);
    }

    /**
     * Lấy danh sách nhân viên có phân trang, tìm kiếm và sắp xếp bằng Native SQL.
     * 
     * @param employeeName Tên nhân viên cần tìm
     * @param departmentId ID phòng ban
     * @param ordEmployeeName Hướng sắp xếp theo tên nhân viên (ASC/DESC)
     * @param ordCertificationName Hướng sắp xếp theo tên chứng chỉ
     * @param ordEndDate Hướng sắp xếp theo ngày kết thúc chứng chỉ
     * @param offset Vị trí bắt đầu lấy dữ liệu (phân trang)
     * @param limit Số lượng bản ghi tối đa trên một trang
     * @return Danh sách EmployeeDTO chứa thông tin nhân viên và chứng chỉ cao nhất (nếu có)
     */
    @Override
    public List<EmployeeDTO> getEmployees(String employeeName, Long departmentId, String ordEmployeeName,
            String ordCertificationName, String ordEndDate, Integer offset, Integer limit) {

        String escapedName = CommonUtils.escapeLike(employeeName);
        int pageSize = (limit != null && limit > 0) ? limit : AppConstants.DEFAULT_PAGE_SIZE;
        int pageOffset = (offset != null) ? offset : 0;

        List<Object[]> results = employeeRepository.getEmployees(
                escapedName,
                departmentId,
                ordEmployeeName,
                ordCertificationName,
                ordEndDate,
                pageOffset,
                pageSize
        );

        return results.stream()
                .map(this::mapToEmployeeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi kết quả thô (Object[]) từ Native Query sang đối tượng EmployeeDTO.
     * Thứ tự các phần tử trong mảng row PHẢI khớp chính xác với thứ tự các cột 
     * được SELECT trong câu truy vấn tại {@link com.luvina.la.repository.EmployeeRepository#getEmployees}.
     * 
     * @param row Mảng Object chứa dữ liệu một dòng kết quả từ SQL
     * @return Đối tượng EmployeeDTO đã được điền đầy đủ thông tin
     */
    private EmployeeDTO mapToEmployeeDTO(Object[] row) {
        EmployeeDTO dto = new EmployeeDTO();
        
        // e.employee_id (Cột 0)
        dto.setEmployeeId(((Number) row[0]).longValue());
        
        // e.employee_name (Cột 1)
        dto.setEmployeeName((String) row[1]);
        
        // e.employee_birth_date (Cột 2)
        dto.setEmployeeBirthDate((Date) row[2]);
        
        // d.department_name (Cột 3)
        dto.setDepartmentName((String) row[3]);
        
        // e.employee_email (Cột 4)
        dto.setEmployeeEmail((String) row[4]);
        
        // e.employee_telephone (Cột 5)
        dto.setEmployeeTelephone((String) row[5]);
        
        // c.certification_name (Cột 6)
        dto.setCertificationName((String) row[6]);
        
        // ec.end_date (Cột 7)
        dto.setEndDate((Date) row[7]);
        
        // ec.score (Cột 8)
        dto.setScore((BigDecimal) row[8]);
        
        return dto;
    }


    /**
     * Thêm mới một nhân viên vào hệ thống và lưu chứng chỉ đi kèm nếu có.
     * 
     * @param employeeRequest Dữ liệu nhân viên từ Client
     * @throws RuntimeException Nếu có lỗi xảy ra trong quá trình parse ngày tháng
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEmployee(EmployeeRequest employeeRequest) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT);

        try {
            Employee employee = new Employee();
            employee.setEmployeeLoginId(employeeRequest.getEmployeeLoginId());
            employee.setEmployeeName(employeeRequest.getEmployeeName());
            employee.setEmployeeNameKana(employeeRequest.getEmployeeNameKana());
            employee.setEmployeeBirthDate(simpleDateFormat.parse(employeeRequest.getEmployeeBirthDate()));
            employee.setEmployeeEmail(employeeRequest.getEmployeeEmail());
            employee.setEmployeeTelephone(employeeRequest.getEmployeeTelephone());
            employee.setDepartmentId(Long.parseLong(employeeRequest.getDepartmentId()));
            employee.setEmployeeLoginPassword(passwordEncoder.encode(employeeRequest.getEmployeeLoginPassword()));
            // Gán quyền mặc định là User khi tạo mới
            employee.setRole(AppConstants.ROLE_USER);

            employee = employeeRepository.save(employee);

            CertificationRequest certificationRequest = employeeRequest.getCertificationRequest();
            if (certificationRequest != null && !ValidatorUtils.isEmpty(certificationRequest.getCertificationId())) {
                EmployeeCertification employeeCertification = new EmployeeCertification();
                employeeCertification.setEmployeeId(employee.getEmployeeId());
                employeeCertification.setCertificationId(Long.parseLong(certificationRequest.getCertificationId()));
                employeeCertification.setStartDate(simpleDateFormat.parse(certificationRequest.getCertificationStartDate()));
                employeeCertification.setEndDate(simpleDateFormat.parse(certificationRequest.getCertificationEndDate()));
                employeeCertification.setScore(new BigDecimal(certificationRequest.getEmployeeCertificationScore()));

                employeeCertificationRepository.save(employeeCertification);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy chi tiết thông tin một nhân viên theo ID bao gồm cả danh sách chứng chỉ.
     * 
     * @param employeeId ID nhân viên cần lấy thông tin
     * @return EmployeeResponse chứa thông tin chi tiết cá nhân và chứng chỉ
     * @throws BaseException Nếu không tìm thấy nhân viên trong hệ thống (ER013)
     */
    @Override
    public EmployeeResponse getEmployeeDetailById(Long employeeId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BaseException(MessageCode.CODE_ER013, HttpStatus.NOT_FOUND));

        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setEmployeeLoginId(employee.getEmployeeLoginId());
        response.setEmployeeName(employee.getEmployeeName());
        response.setEmployeeNameKana(employee.getEmployeeNameKana());
        response.setEmployeeBirthDate(simpleDateFormat.format(employee.getEmployeeBirthDate()));
        response.setEmployeeEmail(employee.getEmployeeEmail());
        response.setEmployeeTelephone(employee.getEmployeeTelephone());
        response.setDepartmentId(String.valueOf(employee.getDepartmentId()));
        
        departmentRepository.findById(employee.getDepartmentId())
                .ifPresent(d -> response.setDepartmentName(d.getDepartmentName()));

        List<EmployeeCertification> certs = employeeCertificationRepository.findAllByEmployeeId(employeeId);
        List<CertificationDTO> certDtos = certs.stream().map(ec -> {
            CertificationDTO dto = new CertificationDTO();
            dto.setCertificationId(ec.getCertificationId());
            certificationRepository.findById(ec.getCertificationId())
                    .ifPresent(c -> dto.setCertificationName(c.getCertificationName()));
            dto.setStartDate(simpleDateFormat.format(ec.getStartDate()));
            dto.setEndDate(simpleDateFormat.format(ec.getEndDate()));
            dto.setScore(ec.getScore());
            return dto;
        }).collect(Collectors.toList());

        response.setCertifications(certDtos);
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        return response;
    }

    /**
     * Xóa nhân viên khỏi hệ thống và xóa các chứng chỉ liên quan.
     * 
     * @param employeeId ID của nhân viên cần xóa
     * @throws BaseException Nếu không tìm thấy nhân viên để xóa (ER014)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BaseException(MessageCode.CODE_ER014, HttpStatus.NOT_FOUND));

        // Không cho phép xóa tài khoản quản trị viên
        if (AppConstants.ROLE_ADMIN == employee.getRole()) {
            throw new BaseException(MessageCode.CODE_ER023, HttpStatus.FORBIDDEN);
        }

        employeeCertificationRepository.deleteAllByEmployeeId(employeeId);
        employeeRepository.deleteById(employeeId);
    }

    /**
     * Cập nhật thông tin của nhân viên hiện có và quản lý lại danh sách chứng chỉ.
     * 
     * @param employeeRequest Đối tượng chứa dữ liệu cập nhật từ Client
     * @throws BaseException Nếu không tìm thấy nhân viên (ER013)
     * @throws RuntimeException Nếu có lỗi định dạng ngày tháng
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployee(EmployeeRequest employeeRequest) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT);

        try {
            Employee employee = employeeRepository.findById(employeeRequest.getEmployeeId())
                    .orElseThrow(() -> new BaseException(MessageCode.CODE_ER013, HttpStatus.NOT_FOUND));

            employee.setEmployeeName(employeeRequest.getEmployeeName());
            employee.setEmployeeNameKana(employeeRequest.getEmployeeNameKana());
            employee.setEmployeeBirthDate(simpleDateFormat.parse(employeeRequest.getEmployeeBirthDate()));
            employee.setEmployeeEmail(employeeRequest.getEmployeeEmail());
            employee.setEmployeeTelephone(employeeRequest.getEmployeeTelephone());
            employee.setDepartmentId(Long.parseLong(employeeRequest.getDepartmentId()));

            String password = employeeRequest.getEmployeeLoginPassword();
            if (!ValidatorUtils.isEmpty(password)) {
                employee.setEmployeeLoginPassword(passwordEncoder.encode(password));
            }

            employeeRepository.save(employee);

            employeeCertificationRepository.deleteAllByEmployeeId(employee.getEmployeeId());

            CertificationRequest certificationRequest = employeeRequest.getCertificationRequest();
            if (certificationRequest != null && !ValidatorUtils.isEmpty(certificationRequest.getCertificationId())) {
                EmployeeCertification employeeCertification = new EmployeeCertification();
                employeeCertification.setEmployeeId(employee.getEmployeeId());
                employeeCertification.setCertificationId(Long.parseLong(certificationRequest.getCertificationId()));
                employeeCertification.setStartDate(simpleDateFormat.parse(certificationRequest.getCertificationStartDate()));
                employeeCertification.setEndDate(simpleDateFormat.parse(certificationRequest.getCertificationEndDate()));
                employeeCertification.setScore(new BigDecimal(certificationRequest.getEmployeeCertificationScore()));

                employeeCertificationRepository.save(employeeCertification);
            }
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Kiểm tra sự tồn tại của nhân viên theo ID.
     * 
     * @param employeeId ID cần kiểm tra
     * @return true nếu tồn tại, ngược lại false
     */
    @Override
    public boolean checkExistsEmployeeById(Long employeeId) {
        return employeeRepository.existsById(employeeId);
    }

    /**
     * Kiểm tra sự tồn tại của Login ID để tránh trùng lặp khi thêm mới hoặc cập nhật.
     * 
     * @param loginId Login ID cần kiểm tra
     * @param employeeId ID của nhân viên hiện tại (null nếu thêm mới)
     * @return true nếu Login ID đã tồn tại cho nhân viên khác, ngược lại false
     */
    @Override
    public boolean checkExistsEmployeeByLoginId(String loginId, Long employeeId) {
        Optional<Employee> existing = employeeRepository.findByEmployeeLoginId(loginId);
        if (existing.isPresent()) {
            return employeeId == null || !existing.get().getEmployeeId().equals(employeeId);
        }
        return false;
    }
}
