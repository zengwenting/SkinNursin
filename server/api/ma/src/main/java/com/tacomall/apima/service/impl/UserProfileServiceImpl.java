package com.tacomall.apima.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tacomall.apima.dto.ApiUserUpdateRequest;
import com.tacomall.apima.entity.UserProfile;
import com.tacomall.apima.mapper.UserProfileMapper;
import com.tacomall.apima.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfile info(Integer userId) {
        Integer targetUserId = userId == null ? 1 : userId;
        // 更新最后登录时间
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UserProfile> wrapper = new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UserProfile>()
                .eq(UserProfile::getId, targetUserId)
                .set(UserProfile::getLastLoginTime, java.time.LocalDateTime.now());
        userProfileMapper.update(null, wrapper);
        return userProfileMapper.selectById(targetUserId);
    }

    @Override
    public UserProfile update(ApiUserUpdateRequest request) {
        Integer targetUserId = request.getId() == null ? 1 : request.getId();
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UserProfile> wrapper = new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UserProfile>()
                .eq(UserProfile::getId, targetUserId);
        
        if (request.getNickname() != null) {
            wrapper.set(UserProfile::getNickname, request.getNickname());
        }
        if (request.getAvatar() != null) {
            wrapper.set(UserProfile::getAvatar, request.getAvatar());
        }
        if (request.getAccount() != null) {
            wrapper.set(UserProfile::getAccount, request.getAccount());
        }

        if (request.getAge() != null) {
            wrapper.set(UserProfile::getAge, request.getAge());
        }
        if (request.getGender() != null) {
            wrapper.set(UserProfile::getGender, request.getGender());
        }
        if (request.getSkinType() != null) {
            wrapper.set(UserProfile::getSkinType, request.getSkinType());
        }
        if (request.getSkinGoal() != null) {
            wrapper.set(UserProfile::getSkinGoal, request.getSkinGoal());
        }
        if (request.getIsSensitive() != null) {
            wrapper.set(UserProfile::getIsSensitive, request.getIsSensitive());
        }
        if (request.getSensitiveSource() != null) {
            wrapper.set(UserProfile::getSensitiveSource, request.getSensitiveSource());
        }
        if (request.getStatus() != null) {
            wrapper.set(UserProfile::getStatus, request.getStatus());
        }
        if (request.getLastLoginTime() != null) {
            wrapper.set(UserProfile::getLastLoginTime, request.getLastLoginTime());
        }
        if (request.getRemindtime() != null) {
            wrapper.set(UserProfile::getRemindtime, request.getRemindtime());
        }
        if (request.getOnclock() != null) {
            wrapper.set(UserProfile::getOnclock, request.getOnclock());
        }
        
        userProfileMapper.update(null, wrapper);
        return userProfileMapper.selectById(targetUserId);
    }
}
