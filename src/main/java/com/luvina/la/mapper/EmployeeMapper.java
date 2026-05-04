package com.luvina.la.mapper;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.entity.Employee;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeDTO dto) {
        if (dto == null) return null;
        Employee entity = new Employee();
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setEmployeeName(dto.getEmployeeName());
        entity.setEmployeeNameKana(dto.getEmployeeNameKana());
        entity.setEmployeeBirthDate(dto.getEmployeeBirthDate());
        entity.setEmployeeEmail(dto.getEmployeeEmail());
        entity.setEmployeeTelephone(dto.getEmployeeTelephone());
        return entity;
    }

    public EmployeeDTO toDto(Employee entity) {
        if (entity == null) return null;
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setEmployeeName(entity.getEmployeeName());
        dto.setEmployeeNameKana(entity.getEmployeeNameKana());
        dto.setEmployeeBirthDate(entity.getEmployeeBirthDate());
        dto.setEmployeeEmail(entity.getEmployeeEmail());
        dto.setEmployeeTelephone(entity.getEmployeeTelephone());
        dto.setEmployeeLoginId(entity.getEmployeeLoginId());
        return dto;
    }
    
    public EmployeeDTO toDtoFromMap(Map<String, Object> map) {
        if (map == null) return null;
        EmployeeDTO dto = new EmployeeDTO();
        
        dto.setEmployeeId(map.get("employeeId") != null ? ((Number) map.get("employeeId")).longValue() : null);
        dto.setEmployeeName(map.get("employeeName") != null ? map.get("employeeName").toString() : null);
        dto.setEmployeeBirthDate((Date) map.get("employeeBirthDate"));
        dto.setDepartmentName(map.get("departmentName") != null ? map.get("departmentName").toString() : null);
        dto.setEmployeeEmail(map.get("employeeEmail") != null ? map.get("employeeEmail").toString() : null);
        dto.setEmployeeTelephone(map.get("employeeTelephone") != null ? map.get("employeeTelephone").toString() : null);
        dto.setCertificationName(map.get("certificationName") != null ? map.get("certificationName").toString() : null);
        dto.setEndDate((Date) map.get("endDate"));
        
        Object scoreObj = map.get("score");
        if (scoreObj instanceof BigDecimal) {
            dto.setScore((BigDecimal) scoreObj);
        } else if (scoreObj instanceof Number) {
            dto.setScore(BigDecimal.valueOf(((Number) scoreObj).doubleValue()));
        }

        return dto;
    }

    public List<EmployeeDTO> toList(Iterable<Employee> list) {
        if (list == null) return null;
        List<EmployeeDTO> dtoList = new ArrayList<>();
        for (Employee employee : list) {
            dtoList.add(toDto(employee));
        }
        return dtoList;
    }
}
