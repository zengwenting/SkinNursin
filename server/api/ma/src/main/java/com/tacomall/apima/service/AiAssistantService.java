package com.tacomall.apima.service;

import com.tacomall.apima.dto.ApiAiChatRequest;
import com.tacomall.apima.vo.ApiAiIngredientVo;
import com.tacomall.apima.vo.ApiAiRecommendVo;

public interface AiAssistantService {

    Object chat(ApiAiChatRequest request);

    ApiAiIngredientVo ingredient(String name);

    ApiAiRecommendVo recommend(String skinType);
}
