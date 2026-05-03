package com.luvina.la.mapper;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.Map;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper MAPPER = Mappers.getMapper( EmployeeMapper.class );

    Employee toEntity(EmployeeDTO dto);
    EmployeeDTO toDto(Employee entity);
    
    @Mapping(target = "employeeId", expression = "java(map.get(\"employeeId\") != null ? ((Number) map.get(\"employeeId\")).longValue() : null)")
    @Mapping(target = "employeeName", expression = "java(map.get(\"employeeName\") != null ? map.get(\"employeeName\").toString() : null)")
    @Mapping(target = "employeeBirthDate", expression = "java((java.util.Date) map.get(\"employeeBirthDate\"))")
    @Mapping(target = "departmentName", expression = "java(map.get(\"departmentName\") != null ? map.get(\"departmentName\").toString() : null)")
    @Mapping(target = "employeeEmail", expression = "java(map.get(\"employeeEmail\") != null ? map.get(\"employeeEmail\").toString() : null)")
    @Mapping(target = "employeeTelephone", expression = "java(map.get(\"employeeTelephone\") != null ? map.get(\"employeeTelephone\").toString() : null)")
    @Mapping(target = "certificationName", expression = "java(map.get(\"certificationName\") != null ? map.get(\"certificationName\").toString() : null)")
    @Mapping(target = "endDate", expression = "java((java.util.Date) map.get(\"endDate\"))")
    @Mapping(target = "score", expression = "java((java.math.BigDecimal) map.get(\"score\"))")
    EmployeeDTO toDtoFromMap(Map<String, Object> map);

    Iterable<EmployeeDTO> toList(Iterable<Employee> list);
}
