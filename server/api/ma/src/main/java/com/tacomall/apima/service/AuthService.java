package com.tacomall.apima.service;

import com.tacomall.apima.dto.ApiLoginRequest;
import com.tacomall.apima.entity.UserProfile;

public interface AuthService {

    UserProfile login(ApiLoginRequest request);
}
