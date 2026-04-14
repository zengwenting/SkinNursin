package com.tacomall.apima.service;

import java.util.List;

import com.tacomall.apima.dto.ApiSkinTestRequest;
import com.tacomall.apima.entity.SkinTest;

public interface SkinTestService {

    SkinTest create(ApiSkinTestRequest request);

    List<SkinTest> history(Integer userId);
}
