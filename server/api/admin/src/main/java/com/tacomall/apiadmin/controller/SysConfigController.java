package com.tacomall.apiadmin.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.apiadmin.service.SysConfigService;
import com.tacomall.common.json.ResponseJson;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/skin-assistant/config/")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @PostMapping("list")
    public ResponseJson<?> getConfigList() {
        return sysConfigService.getConfigList();
    }

    @PostMapping("update")
    public ResponseJson<?> updateConfig(@RequestBody Map<String, String> configMap) {
        return sysConfigService.updateConfig(configMap);
    }
}
