/**
 * Copyright(C) 2026 Luvina
 * [AuthController.java], 23/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.config.jwt.AuthUserDetails;
import com.luvina.la.config.jwt.JwtTokenProvider;
import com.luvina.la.config.jwt.UserDetailsServiceImpl;
import com.luvina.la.payload.request.LoginRequest;
import com.luvina.la.payload.response.LoginResponse;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các yêu cầu liên quan đến xác thực và đăng nhập.
 * 
 * @author tranledat
 */
@RestController
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * API Đăng nhập hệ thống.
     *
     * @param loginRequest thông tin đăng nhập từ client
     * @param request      đối tượng HttpServletRequest
     * @return Đối tượng LoginResponse chứa token hoặc thông báo lỗi
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.generateToken((AuthUserDetails) authentication.getPrincipal());
            return new LoginResponse(accessToken);
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            log.warn(ex.getMessage());
            errors.put("code", "100");
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            // Lỗi không xác định
            errors.put("code", "000");
        }
        return new LoginResponse(errors);
    }

    /**
     * API kiểm tra tính hợp lệ của Token hiện tại.
     * Thường dùng để frontend kiểm tra trạng thái login khi reload trang.
     *
     * @return Bản đồ chứa thông báo "Token is valid" nếu truy cập được vào đây
     */
    @RequestMapping("/test-auth")
    public Map<String, String> testAuth() {
        Map<String, String> testData = new HashMap<>();
        testData.put("msg", "Token is valid");
        return testData;
    }
}
