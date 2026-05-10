/**
 * Copyright(C) 2026 Luvina Software
 * CommonUtils.java, 09/04/2026 tranledat
 */
package com.luvina.la.common.utils;

import org.springframework.data.domain.Sort;

/**
 * Lớp tiện ích chứa các phương thức dùng chung cho toàn bộ hệ thống.
 * 
 * @author tranledat
 */
public final class CommonUtils {

    /**
     * Chặn việc khởi tạo đối tượng vì đây là lớp tiện ích.
     */
    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Escape các ký tự đặc biệt (%, _, !) trong chuỗi tìm kiếm LIKE để tránh lỗi SQL Injection
     * và đảm bảo tìm kiếm chính xác. Sử dụng ký tự '!' làm escape character.
     * 
     * @param input Chuỗi đầu vào cần escape
     * @return Chuỗi đã được escape, trả về input nếu input null hoặc rỗng
     */
    public static String escapeLike(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replace("!", "!!")
                    .replace("%", "!%")
                    .replace("_", "!_");
    }

    /**
     * Chuyển đổi chuỗi đại diện hướng sắp xếp (ASC/DESC) thành đối tượng Sort.Direction.
     * Mặc định trả về ASC nếu chuỗi đầu vào không phải "DESC".
     * 
     * @param dirStr Chuỗi đại diện hướng (không phân biệt hoa thường)
     * @return Đối tượng Sort.Direction tương ứng
     */
    public static Sort.Direction getDirection(String dirStr) {
        if ("DESC".equalsIgnoreCase(dirStr)) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }
}
