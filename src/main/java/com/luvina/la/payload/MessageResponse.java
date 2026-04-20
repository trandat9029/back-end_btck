package com.luvina.la.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MessageResponse {

    private String code;
    private List<String> params;
}
