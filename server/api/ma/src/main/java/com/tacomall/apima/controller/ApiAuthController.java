package com.tacomall.apima.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.apima.common.ApiResponse;
import com.tacomall.apima.dto.ApiLoginRequest;
import com.tacomall.apima.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody ApiLoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
