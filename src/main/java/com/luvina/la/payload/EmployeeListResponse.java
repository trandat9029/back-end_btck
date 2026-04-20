package com.luvina.la.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeListResponse {

    private String code;
    private String message;
    private List<String> params = new ArrayList<>();
    private Long totalRecords;
    private List<EmployeeDTO> employees;
}
