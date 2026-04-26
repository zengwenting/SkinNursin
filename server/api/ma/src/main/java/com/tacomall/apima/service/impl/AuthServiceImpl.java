package com.tacomall.apima.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacomall.apima.dto.ApiLoginRequest;
import com.tacomall.apima.entity.UserProfile;
import com.tacomall.apima.mapper.UserProfileMapper;
import com.tacomall.apima.service.AuthService;
import com.tacomall.apima.service.SysConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserProfileMapper userProfileMapper;
    private final SysConfigService sysConfigService;

    @Override
    public UserProfile login(ApiLoginRequest request) {
        String openid = null;
        
        try {
            Map<String, String> config = sysConfigService.getConfig();
            String appId = config.get("wx_app_id");
            String appSecret = config.get("wx_app_secret");
            
            if (appId != null && appSecret != null && request.getCode() != null) {
                openid = getOpenidFromWechat(appId, appSecret, request.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (openid == null || openid.isEmpty()) {
            openid = "temp_openid_" + System.currentTimeMillis();
        }
        
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getOpenid, openid);
        UserProfile userProfile = userProfileMapper.selectOne(wrapper);
        
        if (userProfile != null) {
            userProfile.setNickname(request.getNickname());
            userProfile.setAvatar(request.getAvatar());
            userProfile.setLastLoginTime(LocalDateTime.now());
            userProfileMapper.updateById(userProfile);
        } else {
            LambdaQueryWrapper<UserProfile> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.select(UserProfile::getId);
            long count = userProfileMapper.selectCount(countWrapper);
            String account = "USER2026" + String.format("%04d", (int) (count + 1));
            
            userProfile = UserProfile.builder()
                    .nickname(request.getNickname())
                    .avatar(request.getAvatar())
                    .account(account)
                    .openid(openid)
                    .lastLoginTime(LocalDateTime.now())
                    .isDelete(0)
                    .status(1)
                    .isSensitive(0)
                    .build();
            userProfileMapper.insert(userProfile);
        }
        
        return userProfile;
    }
    
    private String getOpenidFromWechat(String appId, String appSecret, String jsCode) throws Exception {
        String urlStr = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId 
                        + "&secret=" + appSecret 
                        + "&js_code=" + jsCode 
                        + "&grant_type=authorization_code";
        
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.toString());
        
        if (jsonNode.has("openid")) {
            return jsonNode.get("openid").asText();
        }
        
        return null;
    }
}
