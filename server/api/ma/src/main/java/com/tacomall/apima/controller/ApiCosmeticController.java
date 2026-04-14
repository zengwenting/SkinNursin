package com.tacomall.apima.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.apima.common.ApiResponse;
import com.tacomall.apima.dto.ApiCosmeticCreateRequest;
import com.tacomall.apima.dto.ApiCosmeticDeleteRequest;
import com.tacomall.apima.service.CosmeticItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cosmetic")
@RequiredArgsConstructor
public class ApiCosmeticController {

    private final CosmeticItemService cosmeticItemService;

    @GetMapping("/list")
    public ApiResponse<?> list(@RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "category", required = false) String category) {
        return ApiResponse.success(cosmeticItemService.list(userId, category));
    }

    @PostMapping("/create")
    public ApiResponse<?> create(@RequestBody ApiCosmeticCreateRequest request) {
        return ApiResponse.success(cosmeticItemService.create(request));
    }

    @PostMapping("/delete")
    public ApiResponse<?> delete(@RequestBody ApiCosmeticDeleteRequest request) {
        return ApiResponse.success(cosmeticItemService.delete(request.getId()));
    }
}
