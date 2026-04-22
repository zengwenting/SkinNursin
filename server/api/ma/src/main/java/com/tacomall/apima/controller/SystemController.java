package com.tacomall.apima.controller;

import com.tacomall.apima.common.ApiResponse;
import com.tacomall.apima.service.SysConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {

    private final SysConfigService sysConfigService;

    @GetMapping("/config")
    public ApiResponse<?> getConfig() {
        Map<String, String> config = sysConfigService.getConfig();
        return ApiResponse.success(config);
    }
}
