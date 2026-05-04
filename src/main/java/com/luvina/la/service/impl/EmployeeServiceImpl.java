/**
 * Copyright(C) 2026 Luvina
 * [EmployeeServiceImpl.java], 13/04/2026 tranledat
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp triển khai các nghiệp vụ liên quan đến nhân viên.
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
     * @param employeeName Tên nhân viên (hỗ trợ tìm kiếm partial)
     * @param departmentId ID phòng ban
     * @return Tổng số lượng nhân viên thỏa mãn điều kiện
     */
    @Override
    public Long getTotalRecords(String employeeName, Long departmentId) {
        String escapedName = CommonUtils.escapeLike(employeeName);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Map<String, Object>> page = employeeRepository.searchEmployees(escapedName, departmentId, pageable);
        return page.getTotalElements();
    }

    /**
     * Lấy danh sách nhân viên có phân trang, tìm kiếm và sắp xếp.
     * 
     * @param employeeName Tên nhân viên cần tìm
     * @param departmentId ID phòng ban
     * @param ordEmployeeName Hướng sắp xếp theo tên nhân viên (ASC/DESC)
     * @param ordCertificationName Hướng sắp xếp theo tên chứng chỉ
     * @param ordEndDate Hướng sắp xếp theo ngày kết thúc chứng chỉ
     * @param offset Vị trí bắt đầu lấy dữ liệu
     * @param limit Số lượng bản ghi tối đa trên một trang
     * @return Danh sách EmployeeDTO chứa thông tin nhân viên
     */
    @Override
    public List<EmployeeDTO> getEmployees(String employeeName, Long departmentId, String ordEmployeeName,
            String ordCertificationName, String ordEndDate, Integer offset, Integer limit) {

        int pageSize = (limit != null && limit > 0) ? limit : AppConstants.DEFAULT_PAGE_SIZE;
        int pageOffset = (offset != null) ? offset : 0;
        int pageNumber = pageOffset / pageSize;

        String escapedName = CommonUtils.escapeLike(employeeName);

        Sort.Direction dirName = CommonUtils.getDirection(ordEmployeeName);
        Sort.Direction dirCert = CommonUtils.getDirection(ordCertificationName);
        Sort.Direction dirEnd = CommonUtils.getDirection(ordEndDate);

        if (ordCertificationName != null && !ordCertificationName.isEmpty()) {
            dirCert = ordCertificationName.equalsIgnoreCase("ASC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        }

        Sort sort = JpaSort.unsafe(dirName, "employee_name")
                .and(JpaSort.unsafe(dirCert, "COALESCE(c.certification_level, 6)"))
                .and(JpaSort.unsafe(dirEnd, "ec.end_date"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        return employeeRepository.searchEmployees(escapedName, departmentId, pageable).getContent()
                .stream()
                .map(employeeMapper::toDtoFromMap)
                .collect(Collectors.toList());
    }

    /**
     * Thêm mới một nhân viên vào hệ thống.
     * 
     * @param employeeRequest Dữ liệu nhân viên cần thêm
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
            employee.setRole(1);

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
     * Lấy chi tiết thông tin một nhân viên theo ID.
     * 
     * @param employeeId ID nhân viên
     * @return EmployeeResponse chứa thông tin chi tiết
     * @throws BaseException Nếu không tìm thấy nhân viên
     */
    @Override
    public EmployeeResponse getEmployeeDetailById(Long employeeId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT);

        List<Map<String, Object>> detailRows = employeeRepository.findDetailById(employeeId);

        if (detailRows == null || detailRows.isEmpty()) {
            throw new BaseException(MessageCode.MSG_CODE_ER013, HttpStatus.NOT_FOUND);
        }

        Map<String, Object> firstRow = detailRows.get(0);
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setEmployeeId(((Number) firstRow.get("employeeId")).longValue());
        employeeResponse.setEmployeeLoginId((String) firstRow.get("employeeLoginId"));
        employeeResponse.setEmployeeName((String) firstRow.get("employeeName"));
        employeeResponse.setEmployeeNameKana((String) firstRow.get("employeeNameKana"));
        employeeResponse.setEmployeeBirthDate(simpleDateFormat.format((Date) firstRow.get("employeeBirthDate")));
        employeeResponse.setEmployeeEmail((String) firstRow.get("employeeEmail"));
        employeeResponse.setEmployeeTelephone((String) firstRow.get("employeeTelephone"));
        employeeResponse.setDepartmentId(firstRow.get("departmentId").toString());
        employeeResponse.setDepartmentName((String) firstRow.get("departmentName"));

        List<CertificationDTO> certs = new ArrayList<>();
        for (Map<String, Object> row : detailRows) {
            if (row.get("certificationId") != null) {
                CertificationDTO certificationDTO = new CertificationDTO();
                certificationDTO.setCertificationId(((Number) row.get("certificationId")).longValue());
                certificationDTO.setCertificationName((String) row.get("certificationName"));
                certificationDTO.setStartDate(simpleDateFormat.format((Date) row.get("startDate")));
                certificationDTO.setEndDate(simpleDateFormat.format((Date) row.get("endDate")));
                certificationDTO.setScore((BigDecimal) row.get("score"));
                certs.add(certificationDTO);
            }
        }

        employeeResponse.setCertifications(certs);
        employeeResponse.setCode(String.valueOf(HttpStatus.OK.value()));

        return employeeResponse;
    }

    /**
     * Xóa nhân viên khỏi hệ thống.
     * Đồng thời xóa tất cả các chứng chỉ liên quan của nhân viên đó.
     * 
     * @param employeeId ID của nhân viên cần xóa
     * @throws BaseException Nếu không tìm thấy nhân viên cần xóa
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new BaseException(MessageCode.MSG_CODE_ER014, HttpStatus.NOT_FOUND);
        }
        employeeCertificationRepository.deleteAllByEmployeeId(employeeId);
        employeeRepository.deleteById(employeeId);
    }

    /**
     * Cập nhật thông tin của nhân viên hiện có.
     * Quy trình: Cập nhật thông tin cơ bản -> Xóa chứng chỉ cũ -> Thêm chứng chỉ mới.
     * 
     * @param employeeRequest Đối tượng chứa dữ liệu cập nhật
     * @throws BaseException Nếu không tìm thấy nhân viên cần cập nhật
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployee(EmployeeRequest employeeRequest) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT);

        try {
            Employee employee = employeeRepository.findById(employeeRequest.getEmployeeId())
                    .orElseThrow(() -> new BaseException(MessageCode.MSG_CODE_ER013, HttpStatus.NOT_FOUND));

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
     * Kiểm tra sự tồn tại của Login ID để tránh trùng lặp.
     * 
     * @param loginId Login ID cần kiểm tra
     * @param employeeId ID của nhân viên hiện tại (để loại trừ khi update)
     * @return true nếu Login ID đã bị chiếm dụng, ngược lại false
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
