package com.tacomall.apima.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.apima.common.ApiResponse;
import com.tacomall.apima.dto.ApiCheckinCreateRequest;
import com.tacomall.apima.service.CheckinService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class ApiCheckinController {

    private final CheckinService checkinService;

    @PostMapping("/create")
    public ApiResponse<?> create(@RequestBody ApiCheckinCreateRequest request) {
        try {
            return ApiResponse.success(checkinService.create(request));
        } catch (IllegalStateException ex) {
            return ApiResponse.failure("一天只能打卡一次");
        }
    }

    @GetMapping("/calendar")
    public ApiResponse<?> calendar(@RequestParam(value = "userId", required = false) Integer userId) {
        return ApiResponse.success(checkinService.calendar(userId));
    }

    @GetMapping("/detail")
    public ApiResponse<?> detail(@RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "date", required = false) String date) {
        return ApiResponse.success(checkinService.detail(id, userId, date));
    }

    @GetMapping("/today")
    public ApiResponse<?> today(@RequestParam(value = "userId", required = false) Integer userId) {
        return ApiResponse.success(checkinService.today(userId));
    }
}
