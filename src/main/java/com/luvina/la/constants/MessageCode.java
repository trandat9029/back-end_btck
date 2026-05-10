/**
 * Copyright(C) 2026 Luvina Software
 * MessageCode.java, 09/04/2026 tranledat
 */
package com.luvina.la.constants;

/**
 * Định nghĩa các mã thông báo (Message Code) được sử dụng trong hệ thống.
 * Bao gồm các mã lỗi (ER) và mã thông báo thành công (MSG).
 * 
 * @author tranledat
 */
public final class MessageCode {

    /**
     * Chặn việc khởi tạo đối tượng vì đây là lớp hằng số.
     */
    private MessageCode() {
        throw new IllegalStateException("Utility class");
    }

    /** Lỗi bắt buộc nhập */
    public static final String CODE_ER001 = "ER001";
    /** Lỗi không chọn phòng ban */
    public static final String CODE_ER002 = "ER002";
    /** Lỗi trùng lặp Login ID */
    public static final String CODE_ER003 = "ER003";
    /** Lỗi không tồn tại bản ghi trong DB */
    public static final String CODE_ER004 = "ER004";
    /** Lỗi sai định dạng Email / Định dạng ngày */
    public static final String CODE_ER005 = "ER005";
    /** Lỗi vượt quá độ dài tối đa */
    public static final String CODE_ER006 = "ER006";
    /** Lỗi ngoài khoảng độ dài cho phép (mật khẩu) */
    public static final String CODE_ER007 = "ER007";
    /** Lỗi chỉ được chứa ký tự half-size */
    public static final String CODE_ER008 = "ER008";
    /** Lỗi chỉ được chứa ký tự Katakana */
    public static final String CODE_ER009 = "ER009";
    
    /** Lỗi không tìm thấy phòng ban */
    public static final String CODE_ER011 = "ER011";
    /** Lỗi ngày kết thúc trước ngày bắt đầu */
    public static final String CODE_ER012 = "ER012";
    /** Lỗi không tìm thấy dữ liệu */
    public static final String CODE_ER013 = "ER013";
    /** Lỗi không tìm thấy dữ liệu (chung) */
    public static final String CODE_ER014 = "ER014";
    /** Lỗi hệ thống */
    public static final String CODE_ER015 = "ER015";
    
    /** Lỗi số nguyên dương */
    public static final String CODE_ER018 = "ER018";
    /** Lỗi định dạng Login ID không hợp lệ */
    public static final String CODE_ER019 = "ER019";
    /** Lỗi đăng nhập thất bại / Sort order không hợp lệ */
    public static final String CODE_ER021 = "ER021";
    /** Lỗi quyền truy cập */
    public static final String CODE_ER023 = "ER023";

    /** Thông báo thêm thành công */
    public static final String CODE_MSG001 = "MSG001";
    /** Thông báo cập nhật thành công */
    public static final String CODE_MSG002 = "MSG002";
    /** Thông báo xóa thành công */
    public static final String CODE_MSG003 = "MSG003";
}
