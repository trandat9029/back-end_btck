package com.luvina.la.validation;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Validate va chuan hoa tham so cho API ADM002.
 *
 * @author tranledat
 */
@Component
public class ValidateParamADM002 {

    private static final String SORT_ASC = "ASC";
    private static final String SORT_DESC = "DESC";

    /**
     * Kiem tra chuoi sap xep co thuoc ASC/DESC hay de trong.
     *
     * @param value Gia tri can kiem tra.
     * @return true neu hop le, false neu khong hop le.
     */
    public boolean isValidSortOrder(String value) {
        String normalizedValue = normalizeText(value);
        return normalizedValue == null
                || SORT_ASC.equalsIgnoreCase(normalizedValue)
                || SORT_DESC.equalsIgnoreCase(normalizedValue);
    }

    /**
     * Kiem tra gia tri so nguyen duong (lon hon 0) hoac de trong.
     *
     * @param value Gia tri can kiem tra.
     * @return true neu hop le, false neu khong hop le.
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
     * Kiem tra gia tri so nguyen khong am (lon hon hoac bang 0) hoac de trong.
     *
     * @param value Gia tri can kiem tra.
     * @return true neu hop le, false neu khong hop le.
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
     * Chuyen chuoi sang so nguyen, neu rong thi lay gia tri mac dinh.
     *
     * @param value Chuoi dau vao.
     * @param defaultValue Gia tri mac dinh khi dau vao rong.
     * @return So nguyen tu dau vao hoac gia tri mac dinh.
     * @throws NumberFormatException Nem loi neu chuoi khong the parse thanh so.
     */
    public Integer parseIntegerOrDefault(String value, int defaultValue) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            return defaultValue;
        }
        return Integer.parseInt(normalizedValue);
    }

    /**
     * Chuyen chuoi sang so long, null neu rong.
     *
     * @param value Chuoi dau vao.
     * @return Gia tri long da parse hoac null.
     */
    public Long parseLongOrNull(String value) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            return null;
        }
        return Long.parseLong(normalizedValue);
    }

    /**
     * Chuyen chuoi sap xep sang dang hoa (ASC/DESC), null neu khong co gia tri.
     *
     * @param value Chuoi sap xep dau vao.
     * @return Gia tri ASC/DESC hoa hoac null.
     */
    public String normalizeSortOrder(String value) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            return null;
        }
        return normalizedValue.toLowerCase(Locale.ROOT);
    }

    /**
     * Chuyen chuoi sap xep sang dang thuong (asc/desc), neu null dung gia tri mac dinh.
     *
     * @param value Chuoi sap xep dau vao.
     * @param defaultValue Gia tri mac dinh khi dau vao null/rong.
     * @return Gia tri sap xep da chuan hoa.
     */
    public String normalizeSortOrderOrDefault(String value, String defaultValue) {
        String normalizedValue = normalizeSortOrder(value);
        if (normalizedValue == null) {
            return defaultValue;
        }
        return normalizedValue;
    }

    /**
     * Chuan hoa chuoi dau vao cho cac tham so text.
     *
     * @param value Chuoi dau vao.
     * @return Chuoi da trim, hoac null neu rong.
     */
    public String normalizeInput(String value) {
        return normalizeText(value);
    }

    /**
     * Chuan hoa chuoi dau vao bang cach trim va doi chuoi rong thanh null.
     *
     * @param value Chuoi dau vao.
     * @return Chuoi da chuan hoa hoac null neu khong co gia tri.
     */
    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
