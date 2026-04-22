package com.tacomall.apima.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tacomall.apima.dto.ApiLoginRequest;
import com.tacomall.apima.entity.UserProfile;
import com.tacomall.apima.mapper.UserProfileMapper;
import com.tacomall.apima.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfile login(ApiLoginRequest request) {
        // 这里简化处理，直接使用前端传来的 openid
        // 实际项目中应该调用微信接口，使用 code 换取 openid
        String openid = request.getOpenid();
        
        // 根据 openid 查询用户
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getOpenid, openid);
        UserProfile userProfile = userProfileMapper.selectOne(wrapper);
        
        if (userProfile != null) {
            // 用户存在，更新信息
            userProfile.setNickname(request.getNickname());
            userProfile.setAvatar(request.getAvatar());
            userProfile.setLastLoginTime(LocalDateTime.now());
            userProfileMapper.updateById(userProfile);
        } else {
            // 用户不存在，创建新用户
            // 生成 account 编号，从 USER20260001 开始
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
}
