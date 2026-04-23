/**
 * Copyright(C) 2026 Luvina
 * [MessageResponse.java], 23/04/2026 tranledat
 */
package com.luvina.la.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MessageResponse {

    private String code;
    private List<String> params;
}
