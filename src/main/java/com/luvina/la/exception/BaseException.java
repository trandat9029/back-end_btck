/**
 * Copyright(C) 2026 Luvina Software
 * BaseException.java, 03/05/2026 tranledat
 */
package com.luvina.la.exception;

import com.luvina.la.payload.response.MessageResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp ngoại lệ cơ sở cho toàn bộ ứng dụng.
 */
@Getter
public class BaseException extends RuntimeException {
    /** Phản hồi lỗi chứa mã lỗi và danh sách tham số thông báo */
    private final MessageResponse messageResponse;
    
    /** Trạng thái HTTP trả về cho Client */
    private final HttpStatus httpStatus;

    /**
     * Khởi tạo ngoại lệ với đối tượng MessageResponse có sẵn.
     * 
     * @param messageResponse Đối tượng phản hồi lỗi
     * @param httpStatus Trạng thái HTTP
     */
    public BaseException(MessageResponse messageResponse, HttpStatus httpStatus) {
        super(messageResponse.getCode());
        this.messageResponse = messageResponse;
        this.httpStatus = httpStatus;
    }

    /**
     * Khởi tạo ngoại lệ chỉ với mã lỗi.
     * 
     * @param code Mã lỗi (MessageCode)
     * @param httpStatus Trạng thái HTTP
     */
    public BaseException(String code, HttpStatus httpStatus) {
        super(code);
        this.messageResponse = MessageResponse.builder()
                .code(code)
                .params(new ArrayList<>())
                .build();
        this.httpStatus = httpStatus;
    }

    /**
     * Khởi tạo ngoại lệ với mã lỗi và một tham số đi kèm cho thông báo.
     * 
     * @param code Mã lỗi (MessageCode)
     * @param param Tham số truyền vào thông báo lỗi (ví dụ: tên nhãn trường dữ liệu)
     * @param httpStatus Trạng thái HTTP
     */
    public BaseException(String code, String param, HttpStatus httpStatus) {
        super(code);
        this.messageResponse = MessageResponse.builder()
                .code(code)
                .params(List.of(param))
                .build();
        this.httpStatus = httpStatus;
    }
}
