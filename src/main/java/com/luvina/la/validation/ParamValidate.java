/**
 * Copyright(C) 2026 - Luvina
 * [ParamValidate.java], 24/04/2026 [tranledat]
 */
package com.luvina.la.validation;

import com.luvina.la.config.Constants;
import com.luvina.la.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Validate và chuẩn hóa tham số cho màn hình ADM002.
 * @author tranledat
 */
@Component
public class ParamValidate {

    private static final String SORT_ASC = "ASC";
    private static final String SORT_DESC = "DESC";

    /**
     * Kiểm tra toàn bộ tham số cho API ADM002.
     * @param ordName Thứ tự sắp xếp theo tên.
     * @param ordCert Thứ tự sắp xếp theo chứng chỉ.
     * @param ordEnd Thứ tự sắp xếp theo ngày hết hạn.
     * @param offset Vị trí bắt đầu lấy dữ liệu.
     * @param limit Số lượng bản ghi tối đa.
     * @param deptId ID phòng ban.
     * @throws CustomException Nếu tham số không hợp lệ.
     */
    public void checkValidateParams(String ordName, String ordCert, String ordEnd, String offset, String limit, String deptId) {
        if (!isValidSortOrder(ordName)
                || !isValidSortOrder(ordCert)
                || !isValidSortOrder(ordEnd)) {
            throw new CustomException(Constants.CODE_ER021);
        }

        if (!isValidNonNegativeInteger(offset)) {
            throw new CustomException(Constants.CODE_ER008, "offset");
        }

        if (!isValidPositiveInteger(limit)) {
            throw new CustomException(Constants.CODE_ER008, "limit");
        }

        if (!isValidPositiveInteger(deptId)) {
            throw new CustomException(Constants.CODE_ER008, "departmentId");
        }
    }

    /**
     * Kiểm tra chuỗi sắp xếp có thuộc ASC/DESC hay để trống.
     */
    public boolean isValidSortOrder(String value) {
        String normalizedValue = normalizeText(value);
        return normalizedValue == null
                || SORT_ASC.equalsIgnoreCase(normalizedValue)
                || SORT_DESC.equalsIgnoreCase(normalizedValue);
    }

    /**
     * Kiểm tra giá trị số nguyên dương (lớn hơn 0) hoặc để trống.
     */
    public boolean isValidPositiveInteger(String value) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            return true;
        }
        try {
            return Integer.parseInt(normalizedValue) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Kiểm tra giá trị số nguyên không âm (lớn hơn hoặc bằng 0) hoặc để trống.
     */
    public boolean isValidNonNegativeInteger(String value) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            return true;
        }
        try {
            return Integer.parseInt(normalizedValue) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Chuyển chuỗi sang số nguyên, nếu rỗng thì lấy giá trị mặc định.
     */
    public Integer parseIntegerOrDefault(String value, int defaultValue) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            return defaultValue;
        }
        return Integer.parseInt(normalizedValue);
    }

    /**
     * Chuyển chuỗi sang số long, null nếu rỗng.
     */
    public Long parseLongOrNull(String value) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            return null;
        }
        return Long.parseLong(normalizedValue);
    }

    /**
     * Chuyển chuỗi sắp xếp sang dạng hoa (ASC/DESC), null nếu không có giá trị.
     */
    public String normalizeSortOrder(String value) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            return null;
        }
        return normalizedValue.toLowerCase(Locale.ROOT);
    }

    /**
     * Chuyển chuỗi sắp xếp sang dạng thường (asc/desc), nếu null dùng giá trị mặc định.
     */
    public String normalizeSortOrderOrDefault(String value, String defaultValue) {
        String normalizedValue = normalizeSortOrder(value);
        if (normalizedValue == null) {
            return defaultValue;
        }
        return normalizedValue;
    }

    /**
     * Chuẩn hóa chuỗi đầu vào cho các tham số text.
     */
    public String normalizeInput(String value) {
        return normalizeText(value);
    }

    /**
     * Chuẩn hóa chuỗi đầu vào bằng cách trim và đổi chuỗi rỗng thành null.
     */
    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
