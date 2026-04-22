package com.tacomall.apima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tacomall.apima.entity.SysConfig;
import com.tacomall.apima.mapper.SysConfigMapper;
import com.tacomall.apima.service.SysConfigService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public Map<String, String> getConfig() {
        List<SysConfig> configList = sysConfigMapper.selectList(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getIsDelete, 0)
        );

        Map<String, String> configMap = new HashMap<>();
        for (SysConfig config : configList) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }

        return configMap;
    }
}
