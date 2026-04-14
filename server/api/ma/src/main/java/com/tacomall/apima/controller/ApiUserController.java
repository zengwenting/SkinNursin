package com.tacomall.apima.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.apima.common.ApiResponse;
import com.tacomall.apima.dto.ApiUserUpdateRequest;
import com.tacomall.apima.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ApiUserController {

    private final UserProfileService userProfileService;

    @GetMapping("/info")
    public ApiResponse<?> info(@RequestParam(value = "userId", required = false) Integer userId) {
        return ApiResponse.success(userProfileService.info(userId));
    }

    @PostMapping("/update")
    public ApiResponse<?> update(@RequestBody ApiUserUpdateRequest request) {
        return ApiResponse.success(userProfileService.update(request));
    }
}
