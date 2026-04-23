/**
 * Copyright(C) 2026 Luvina
 * [LoginResponse.java], 23/04/2026 tranledat
 */
package com.luvina.la.payload.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private Map<String, String> errors = new HashMap<>();

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    public LoginResponse(Map<String, String> errors) {
        this.errors = errors;
    }

}
