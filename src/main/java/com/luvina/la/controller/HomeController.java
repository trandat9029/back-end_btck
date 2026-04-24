/**
 * Copyright(C) 2026 - Luvina
 * [HomeController.java], 24/04/2026 [tranledat]
 */
package com.luvina.la.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các yêu cầu cơ bản của hệ thống.
 * @author tranledat
 */
@RestController
public class HomeController {

    /**
     * Phương thức mặc định hiển thị thông báo chào mừng.
     * @return Chuỗi thông báo chào mừng.
     */
    @RequestMapping("/")
    public String index() {
        return "Welcome to Employee service";
    }

}
