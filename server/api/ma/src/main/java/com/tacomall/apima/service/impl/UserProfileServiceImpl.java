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
        return userProfileMapper.selectById(targetUserId);
    }

    @Override
    public UserProfile update(ApiUserUpdateRequest request) {
        Integer targetUserId = request.getId() == null ? 1 : request.getId();
        userProfileMapper.update(null, new UpdateWrapper<UserProfile>().lambda()
                .eq(UserProfile::getId, targetUserId)
                .set(UserProfile::getNickname, request.getNickname())
                .set(UserProfile::getAvatar, request.getAvatar())
                .set(UserProfile::getAccount, request.getAccount())
                .set(UserProfile::getPassword, request.getPassword())
                .set(UserProfile::getAge, request.getAge())
                .set(UserProfile::getGender, request.getGender())
                .set(UserProfile::getSkinType, request.getSkinType())
                .set(UserProfile::getSkinGoal, request.getSkinGoal())
                .set(UserProfile::getBio, request.getBio()));
        return userProfileMapper.selectById(targetUserId);
    }
}
