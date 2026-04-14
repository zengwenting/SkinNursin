package com.tacomall.apima.service;

import java.util.List;

import com.tacomall.apima.dto.ApiCosmeticCreateRequest;
import com.tacomall.apima.entity.CosmeticItem;

public interface CosmeticItemService {

    List<CosmeticItem> list(Integer userId, String category);

    CosmeticItem create(ApiCosmeticCreateRequest request);

    Boolean delete(Integer id);
}
