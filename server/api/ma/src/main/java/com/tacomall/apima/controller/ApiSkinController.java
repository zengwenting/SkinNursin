package com.tacomall.apima.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.apima.common.ApiResponse;
import com.tacomall.apima.dto.ApiSkinTestRequest;
import com.tacomall.apima.service.SkinTestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/skin")
@RequiredArgsConstructor
public class ApiSkinController {

    private final SkinTestService skinTestService;

    @PostMapping("/test")
    public ApiResponse<?> test(@RequestBody ApiSkinTestRequest request) {
        return ApiResponse.success(skinTestService.create(request));
    }

    @GetMapping("/history")
    public ApiResponse<?> history(@RequestParam(value = "userId", required = false) Integer userId) {
        return ApiResponse.success(skinTestService.history(userId));
    }
}
