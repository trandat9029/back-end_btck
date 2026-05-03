package com.luvina.la.common.utils;

import org.springframework.data.domain.Sort;

public class CommonUtils {

    /**
     * Escape ký tự đặc biệt cho tìm kiếm LIKE.
     * 
     * @param input chuỗi đầu vào
     * @return chuỗi đã được escape
     */
    public static String escapeLike(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * Xác định hướng sắp xếp từ chuỗi đầu vào.
     * 
     * @param dirStr chuỗi đại diện hướng (ASC/DESC)
     * @return Đối tượng Sort.Direction
     */
    public static Sort.Direction getDirection(String dirStr) {
        if ("DESC".equalsIgnoreCase(dirStr)) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }
}
