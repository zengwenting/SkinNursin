package com.tacomall.apima.service.impl;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tacomall.apima.dto.ApiAiChatRequest;
import com.tacomall.apima.service.AiAssistantService;
import com.tacomall.apima.vo.ApiAiIngredientVo;
import com.tacomall.apima.vo.ApiAiRecommendVo;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    @Override
    public Object chat(ApiAiChatRequest request) {
        // Mock response only. Replace this block when a real AI service is introduced.
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reply", "建议加强补水护理，今晚可以使用保湿精华并搭配舒缓面霜。");
        result.put("tips", Arrays.asList("早晚温和清洁", "连续三天观察泛红变化", "白天注意防晒"));
        result.put("question", request == null ? null : request.getMessage());
        return result;
    }

    @Override
    public ApiAiIngredientVo ingredient(String name) {
        // Mock ingredient explanation for UI integration.
        String safeName = name == null || name.isBlank() ? "烟酰胺" : name;
        return ApiAiIngredientVo.builder()
                .name(safeName)
                .effect("帮助提亮肤色，并辅助改善皮肤屏障状态。")
                .caution("敏感期建议先低频使用，避免与强酸类产品叠加。")
                .matchTip("可搭配神经酰胺和玻尿酸做夜间修护。")
                .build();
    }

    @Override
    public ApiAiRecommendVo recommend(String skinType) {
        // Mock recommendation payload for UI integration.
        String safeSkinType = skinType == null || skinType.isBlank() ? "combination" : skinType;
        return ApiAiRecommendVo.builder()
                .skinType(safeSkinType)
                .reply("建议采用轻修护、强保湿、低刺激的护理节奏。")
                .routine(Arrays.asList("晨间洁面", "保湿精华", "修护乳霜", "日间防晒"))
                .build();
    }
}
