/**
 * Copyright(C) 2026 Luvina
 * [ValidatorUtils.java], 13/04/2026 tranledat
 */
package com.luvina.la.common.validate;

import com.luvina.la.constants.AppConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static com.luvina.la.constants.AppConstants.HALF_WIDTH_KATAKANA_PATTERN;

/**
 * Lớp tiện ích cung cấp các hàm validate dữ liệu dùng chung.
 * 
 * @author tranledat
 */
public class ValidatorUtils {

    /**
     * Kiểm tra chuỗi có trống hoặc null không.
     * 
     * @param value giá trị cần kiểm tra
     * @return true nếu trống, ngược lại false
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Kiểm tra chuỗi có vượt quá độ dài tối đa cho phép không.
     * 
     * @param value giá trị cần kiểm tra
     * @param max   độ dài tối đa
     * @return true nếu vượt quá, ngược lại false
     */
    public static boolean isMaxLength(String value, int max) {
        if (value == null)
            return false;
        return value.length() > max;
    }

    /**
     * Kiểm tra chuỗi có nằm trong khoảng độ dài cho phép không.
     * 
     * @param value giá trị cần kiểm tra
     * @param min   độ dài tối thiểu
     * @param max   độ dài tối đa
     * @return true nếu nằm ngoài khoảng, ngược lại false
     */
    public static boolean isInvalidLengthRange(String value, int min, int max) {
        if (value == null)
            return true;
        int len = value.length();
        return len < min || len > max;
    }



    /**
     * Kiểm tra chuỗi có chỉ chứa ký tự Katakana (Half-width) không.
     * 
     * @param value giá trị cần kiểm tra
     * @return true nếu chỉ chứa Katakana half-width, ngược lại false
     */
    public static boolean isKatakana(String value) {
        return value != null && HALF_WIDTH_KATAKANA_PATTERN.matcher(value).matches();
    }

    /**
     * Kiểm tra chuỗi có phải là số nguyên dương không.
     * 
     * @param value giá trị cần kiểm tra
     * @return true nếu là số nguyên dương, ngược lại false
     */
    public static boolean isPositiveNumber(String value) {
        if (value == null)
            return false;
        try {
            long num = Long.parseLong(value);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Kiểm tra chuỗi có chứa ký tự 1 byte (Half-width) không.
     * 
     * @param value giá trị cần kiểm tra
     * @return true nếu chỉ chứa ký tự 1 byte, ngược lại false
     */
    public static boolean isHalfSize(String value) {
        if (value == null)
            return false;
        // Kiểm tra không chứa ký tự multi-byte
        return value.length() == value.getBytes().length;
    }

    /**
     * Kiểm tra định dạng tên đăng nhập (a-z, A-Z, 0-9, _) và không bắt đầu bằng số.
     * 
     * @param value giá trị cần kiểm tra
     * @return true nếu hợp lệ, ngược lại false
     */
    public static boolean isValidLoginId(String value) {
        return Pattern.matches(AppConstants.REGEX_LOGIN_ID, value);
    }

    /**
     * Kiểm tra định dạng email theo yêu cầu dự án.
     * Có đủ @ và dấu ".", không đứng cạnh nhau, không đứng đầu.
     * 
     * @param email địa chỉ email
     * @return true nếu hợp lệ, ngược lại false
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        } else if (!email.contains(AppConstants.EMAIL_AT) || !email.contains(AppConstants.EMAIL_DOT)) {
            return false;
        } else if (email.startsWith(AppConstants.EMAIL_AT) || email.startsWith(AppConstants.EMAIL_DOT)) {
            return false;
        } else if (email.contains(AppConstants.EMAIL_AT_DOT) || email.contains(AppConstants.EMAIL_DOT_AT)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * So sánh hai ngày tháng.
     * 
     * @param startDate ngày bắt đầu
     * @param endDate   ngày kết thúc
     * @return true nếu endDate đứng trước startDate, ngược lại false
     */
    public static boolean isEndDateBeforeStartDate(String startDate, String endDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat.parse(endDate).before(simpleDateFormat.parse(startDate));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra giá trị sắp xếp có hợp lệ không (ASC hoặc DESC).
     * 
     * @param order giá trị sắp xếp
     * @return true nếu là ASC hoặc DESC, hoặc rỗng (coi như ASC). False nếu sai định dạng.
     */
    public static boolean isValidSortOrder(String order) {
        if (isEmpty(order)) {
            return true;
        }
        return "ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order);
    }
}
