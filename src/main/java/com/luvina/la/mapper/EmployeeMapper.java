/**
 * Copyright(C) 2026 Luvina Software
 * EmployeeMapper.java, 09/04/2026 tranledat
 */
package com.luvina.la.mapper;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.entity.Employee;
import com.luvina.la.payload.response.EmployeeResponse;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.constants.AppConstants;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Lớp Mapper chịu trách nhiệm chuyển đổi (mapping) dữ liệu nhân viên.
 * Giúp tách biệt dữ liệu Database (Entity) và dữ liệu truyền tải (DTO/Request/Response).
 * 
 * @author tranledat
 */
@Component
public class EmployeeMapper {

    /**
     * Chuyển đổi từ EmployeeRequest (dữ liệu từ Frontend) sang Employee Entity.
     * Dùng trong luồng Thêm mới hoặc Cập nhật nhân viên.
     * 
     * @param request Đối tượng EmployeeRequest từ Client
     * @return Đối tượng Employee Entity
     * @throws java.text.ParseException Nếu định dạng ngày tháng không đúng
     */
    public Employee mapRequestToEmployeeEntity(EmployeeRequest request) throws java.text.ParseException {
        if (request == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_FORMAT);
        Employee entity = new Employee();
        
        entity.setEmployeeId(request.getEmployeeId());
        entity.setEmployeeLoginId(request.getEmployeeLoginId());
        entity.setEmployeeName(request.getEmployeeName());
        entity.setEmployeeNameKana(request.getEmployeeNameKana());
        entity.setEmployeeBirthDate(sdf.parse(request.getEmployeeBirthDate()));
        entity.setEmployeeEmail(request.getEmployeeEmail());
        entity.setEmployeeTelephone(request.getEmployeeTelephone());
        entity.setDepartmentId(Long.parseLong(request.getDepartmentId()));
        
        return entity;
    }

    /**
     * Chuyển đổi kết quả thô (Object[]) từ Native Query sang đối tượng EmployeeDTO.
     * Dùng trong luồng Tìm kiếm và hiển thị danh sách nhân viên.
     * 
     * @param row Mảng Object chứa dữ liệu một dòng kết quả từ SQL
     * @return Đối tượng EmployeeDTO
     */
    public EmployeeDTO mapRowToEmployeeDTO(Object[] row) {
        if (row == null) return null;
        EmployeeDTO dto = new EmployeeDTO();
        
        dto.setEmployeeId(((Number) row[0]).longValue());
        dto.setEmployeeName((String) row[1]);
        dto.setEmployeeBirthDate((Date) row[2]);
        dto.setDepartmentName((String) row[3]);
        dto.setEmployeeEmail((String) row[4]);
        dto.setEmployeeTelephone((String) row[5]);
        dto.setCertificationName((String) row[6]);
        dto.setEndDate((Date) row[7]);
        dto.setScore((BigDecimal) row[8]);
        
        return dto;
    }

    /**
     * Chuyển đổi từ Employee Entity sang EmployeeResponse.
     * Dùng cho API trả về chi tiết thông tin một nhân viên.
     * 
     * @param entity Đối tượng Employee Entity
     * @return Đối tượng EmployeeResponse
     */
    public EmployeeResponse mapToEmployeeResponse(Employee entity) {
        if (entity == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_FORMAT);
        
        return EmployeeResponse.builder()
                .employeeId(entity.getEmployeeId())
                .employeeLoginId(entity.getEmployeeLoginId())
                .employeeName(entity.getEmployeeName())
                .employeeNameKana(entity.getEmployeeNameKana())
                .employeeBirthDate(sdf.format(entity.getEmployeeBirthDate()))
                .employeeEmail(entity.getEmployeeEmail())
                .employeeTelephone(entity.getEmployeeTelephone())
                .departmentId(String.valueOf(entity.getDepartmentId()))
                .build();
    }
}
