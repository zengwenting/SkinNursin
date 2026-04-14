package com.tacomall.apima.service;

import com.tacomall.apima.dto.ApiUserUpdateRequest;
import com.tacomall.apima.entity.UserProfile;

public interface UserProfileService {

    UserProfile info(Integer userId);

    UserProfile update(ApiUserUpdateRequest request);
}
