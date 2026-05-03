/**
 * Copyright(C) 2026 - Luvina
 * [MessageResponse.java], 24/04/2026 [tranledat]
 */
package com.luvina.la.payload.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng chứa mã lỗi/thành công và các tham số đi kèm để hiển thị lên UI.
 * 
 * @author tranledat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String code;
    private List<String> params;
}
