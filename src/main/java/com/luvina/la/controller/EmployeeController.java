/**
 * Copyright(C) 2026 Luvina
 * [EmployeeController.java], 13/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.EmployeeListResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.ValidateParamADM002;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller quan ly API tim kiem va lay danh sach nhan vien.
 *
 * @author tranledat
 */
@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private static final String CODE_SUCCESS = String.valueOf(HttpStatus.OK.value());
    private static final String CODE_SYSTEM_ERROR = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
    private static final String ERROR_SORT_ORDER = "ER021";
    private static final String ERROR_PAGE_NOT_FOUND = "ER022";
    private static final String ERROR_SYSTEM = "ER023";
    private static final String ERROR_NUMERIC = "ER018";
    private static final String OFFSET_PARAM_KEY = "employee.validation.offset.param";
    private static final String LIMIT_PARAM_KEY = "employee.validation.limit.param";
    private static final String DEPARTMENT_ID_PARAM_KEY = "employee.validation.department-id.param";
    private static final String DEFAULT_SORT_EMPLOYEE_NAME = "asc";
    private static final String DEFAULT_SORT_CERTIFICATION_NAME = "desc";
    private static final String DEFAULT_SORT_END_DATE = "asc";
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 5;

    private final EmployeeService employeeService;
    private final MessageSource messageSource;
    private final ValidateParamADM002 validateParamADM002;

    /**
     * API chinh cua ADM002 de tra danh sach nhan vien.
     * Method nay co 4 nhiem vu:
     * - Nhan va validate cac query param tu request.
     * - Chuan hoa gia tri dau vao va gan gia tri mac dinh cho sort/paging.
     * - Goi service de dem tong ban ghi va lay danh sach nhan vien.
     * - Tra ve response thanh cong hoac response loi dung format cua man hinh.
     *
     * @param employeeName Ten nhan vien dung de tim kiem.
     * @param departmentId Ma phong ban dung de loc.
     * @param ordEmployeeName Thu tu sap xep theo ten nhan vien (ASC/DESC).
     * @param ordCertificationName Thu tu sap xep theo ten chung chi (ASC/DESC).
     * @param ordEndDate Thu tu sap xep theo ngay het han chung chi (ASC/DESC).
     * @param offset Vi tri bat dau ban ghi.
     * @param limit So luong ban ghi tren mot trang.
     * @return Ket qua danh sach nhan vien hoac thong tin loi validate/he thong.
     */
    @GetMapping({"/employees"})
    public EmployeeListResponse getEmployees(
            @RequestParam(value = "employee_name", required = false) String employeeName,
            @RequestParam(value = "department_id", required = false) String departmentId,
            @RequestParam(value = "ord_employee_name", required = false) String ordEmployeeName,
            @RequestParam(value = "ord_certification_name", required = false) String ordCertificationName,
            @RequestParam(value = "ord_end_date", required = false) String ordEndDate,
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit) {
        EmployeeListResponse response;

        try {
            // Validate cac tham so sort. Neu client gui gia tri khong phai ASC/DESC
            // thi dung xu ly ngay va tra loi nghiep vu ER021.
            if (!validateParamADM002.isValidSortOrder(ordEmployeeName)
                    || !validateParamADM002.isValidSortOrder(ordCertificationName)
                    || !validateParamADM002.isValidSortOrder(ordEndDate)) {
                response = buildErrorResponse(ERROR_SORT_ORDER);
            // Offset phai la so nguyen khong am vi dung de tinh vi tri bat dau lay du lieu.
            } else if (!validateParamADM002.isValidNonNegativeInteger(offset)) {
                response = buildNumericErrorResponse(getMessage(OFFSET_PARAM_KEY));
            // Limit phai la so nguyen duong vi day la so ban ghi tren mot trang.
            } else if (!validateParamADM002.isValidPositiveInteger(limit)) {
                response = buildNumericErrorResponse(getMessage(LIMIT_PARAM_KEY));
            // department_id neu co truyen len thi phai la so nguyen duong hop le.
            } else if (!validateParamADM002.isValidPositiveInteger(departmentId)) {
                response = buildNumericErrorResponse(getMessage(DEPARTMENT_ID_PARAM_KEY));
            } else {
                // Chuan hoa input tim kiem:
                // - trim khoang trang
                // - doi chuoi rong thanh null
                // - parse cac tham so so ve dung kieu du lieu
                // - gan gia tri sort/paging mac dinh neu client khong truyen len
                String normalizedEmployeeName = validateParamADM002.normalizeInput(employeeName);
                Long normalizedDepartmentId = validateParamADM002.parseLongOrNull(departmentId);
                Integer normalizedOffset = validateParamADM002.parseIntegerOrDefault(offset, DEFAULT_OFFSET);
                Integer normalizedLimit = validateParamADM002.parseIntegerOrDefault(limit, DEFAULT_LIMIT);
                String normalizedOrdEmployeeName = validateParamADM002.normalizeSortOrderOrDefault(
                        ordEmployeeName,
                        DEFAULT_SORT_EMPLOYEE_NAME
                );
                String normalizedOrdCertificationName = validateParamADM002.normalizeSortOrderOrDefault(
                        ordCertificationName,
                        DEFAULT_SORT_CERTIFICATION_NAME
                );
                String normalizedOrdEndDate = validateParamADM002.normalizeSortOrderOrDefault(
                        ordEndDate,
                        DEFAULT_SORT_END_DATE
                );

                // Dem tong so nhan vien thoa dieu kien tim kiem de phuc vu phan trang.
                Long totalRecords = employeeService.countEmployeesWithFilter(normalizedEmployeeName, normalizedDepartmentId);
                List<EmployeeDTO> employees = new ArrayList<>();

                if (totalRecords > 0) {
                    // Neu offset lon hon hoac bang tong so ban ghi thi nguoi dung dang yeu cau
                    // mot trang khong ton tai, can tra loi ER022.
                    if (normalizedOffset.longValue() >= totalRecords) {
                        response = buildErrorResponse(ERROR_PAGE_NOT_FOUND);
                    } else {
                        // Lay du lieu danh sach nhan vien theo dieu kien tim kiem, sort va paging
                        // sau do dong goi thanh response thanh cong cho frontend.
                        employees = employeeService.getListEmployee(
                                normalizedEmployeeName,
                                normalizedDepartmentId,
                                normalizedOrdEmployeeName,
                                normalizedOrdCertificationName,
                                normalizedOrdEndDate,
                                normalizedLimit,
                                normalizedOffset
                        );
                        response = buildSuccessResponse(totalRecords, employees);
                    }
                } else {
                    // Khong co du lieu van tra response thanh cong, nhung danh sach rong
                    // de frontend hien thi dung trang thai "khong tim thay".
                    response = buildSuccessResponse(totalRecords, employees);
                }
            }
        } catch (Exception e) {
            // Bat moi loi runtime khong mong muon trong qua trinh xu ly
            // va quy ve response loi he thong chung.
            response = buildSystemErrorResponse();
        }

        return response;
    }

    /**
     * Tao response thanh cong cho API danh sach nhan vien.
     * Method nay chi dong goi du lieu theo dung contract tra ve cho frontend:
     * - code = 200
     * - totalRecords = tong so nhan vien sau khi filter
     * - employees = du lieu hien thi tren bang ADM002
     * - params = danh sach rong vi khong co loi
     *
     * @param totalRecords Tong so ban ghi tim duoc.
     * @param employees Danh sach nhan vien.
     * @return Response thanh cong.
     */
    private EmployeeListResponse buildSuccessResponse(Long totalRecords, List<EmployeeDTO> employees) {
        EmployeeListResponse response = new EmployeeListResponse();
        response.setCode(CODE_SUCCESS);
        response.setTotalRecords(totalRecords);
        response.setEmployees(employees);
        response.setParams(new ArrayList<>());
        return response;
    }

    /**
     * Tao nhanh response loi nghiep vu khi code tra ve va message code la cung mot gia tri.
     * Duoc dung cho cac loi nhu ER021, ER022.
     *
     * @param errorCode Ma loi nghiep vu.
     * @return Response loi.
     */
    private EmployeeListResponse buildErrorResponse(String errorCode) {
        return buildErrorResponse(errorCode, errorCode, new ArrayList<>());
    }

    /**
     * Tao response loi validate so ER018 va gan them ten tham so bi sai.
     * Vi du: offset, limit, department_id.
     *
     * @param param Ten tham so bi loi.
     * @return Response loi ER018.
     */
    private EmployeeListResponse buildNumericErrorResponse(String param) {
        List<String> params = new ArrayList<>();
        params.add(param);
        return buildErrorResponse(ERROR_NUMERIC, ERROR_NUMERIC, params);
    }

    /**
     * Tao response loi he thong chung khi co exception khong mong muon.
     * Method nay duoc goi trong khoi catch cua API getEmployees().
     *
     * @return Response loi he thong.
     */
    private EmployeeListResponse buildSystemErrorResponse() {
        return buildErrorResponse(CODE_SYSTEM_ERROR, ERROR_SYSTEM, new ArrayList<>());
    }

    /**
     * Ham dung chung de dong goi response loi.
     * Method nay se:
     * - Gan code HTTP nghiep vu cho response body.
     * - Resolve noi dung message tu MessageSource theo Locale.JAPANESE.
     * - Gan danh sach params de frontend hoac message template co the su dung.
     *
     * @param code Code tra ve.
     * @param messageCode Key message trong resources.
     * @param params Danh sach tham so message.
     * @return Response loi.
     */
    private EmployeeListResponse buildErrorResponse(String code, String messageCode, List<String> params) {
        EmployeeListResponse response = new EmployeeListResponse();
        Object[] messageArgs = params.isEmpty() ? null : params.toArray();
        response.setCode(code);
        response.setMessage(messageSource.getMessage(messageCode, messageArgs, messageCode, Locale.JAPANESE));
        response.setParams(params);
        return response;
    }

    /**
     * Resolve ten tham so hien thi tu file message theo key.
     * Method nay duoc dung khi tao loi ER018 de dua ra ten tham so dung voi man hinh/spec.
     *
     * @param key Key cua message.
     * @return Noi dung message da resolve.
     */
    private String getMessage(String key) {
        return messageSource.getMessage(key, null, key, Locale.ROOT);
    }
}
