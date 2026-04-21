package com.tacomall.apiadmin.service;

import java.util.Map;

import com.tacomall.common.json.ResponseJson;

public interface SysConfigService {

    ResponseJson<Map<String, String>> getConfigList();

    ResponseJson<String> updateConfig(Map<String, String> configMap);
}
