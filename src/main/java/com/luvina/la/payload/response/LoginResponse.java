/**
 * Copyright(C) 2026 Luvina
 * [LoginResponse.java], 23/04/2026 tranledat
 */
package com.luvina.la.payload.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Class chứa thông tin phản hồi sau khi đăng nhập.
 * @author tranledat
 */
@Data
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private Map<String, String> errors = new HashMap<>();

    /**
     * Khởi tạo response thành công với token.
     * @param accessToken Chuỗi JWT Token.
     */
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    /**
     * Khởi tạo response thất bại với danh sách lỗi.
     * @param errors Map chứa các thông báo lỗi.
     */
    public LoginResponse(Map<String, String> errors) {
        this.errors = errors;
    }

}
