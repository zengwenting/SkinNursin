package com.tacomall.apiadmin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tacomall.apiadmin.entity.SysConfig;
import com.tacomall.apiadmin.mapper.SysConfigMapper;
import com.tacomall.apiadmin.service.SysConfigService;
import com.tacomall.common.json.ResponseJson;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public ResponseJson<Map<String, String>> getConfigList() {
        ResponseJson<Map<String, String>> responseJson = new ResponseJson<>();
        List<SysConfig> configList = sysConfigMapper.selectList(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getIsDelete, 0)
        );
        Map<String, String> configMap = new HashMap<>();
        for (SysConfig config : configList) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        responseJson.setData(configMap);
        responseJson.ok();
        return responseJson;
    }

    @Override
    public ResponseJson<String> updateConfig(Map<String, String> configMap) {
        ResponseJson<String> responseJson = new ResponseJson<>();
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            SysConfig config = sysConfigMapper.selectOne(
                    new LambdaQueryWrapper<SysConfig>()
                            .eq(SysConfig::getConfigKey, entry.getKey())
                            .eq(SysConfig::getIsDelete, 0)
            );
            if (config != null) {
                config.setConfigValue(entry.getValue());
                sysConfigMapper.updateById(config);
            }
        }
        responseJson.setData("配置更新成功");
        responseJson.ok();
        return responseJson;
    }
}
