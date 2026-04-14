package com.tacomall.apima.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.apima.common.ApiResponse;
import com.tacomall.apima.dto.ApiAiChatRequest;
import com.tacomall.apima.service.AiAssistantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ApiAiController {

    private final AiAssistantService aiAssistantService;

    /**
     * Mock AI conversation endpoint.
     * The response is fixed fake data for frontend integration only.
     */
    @PostMapping("/chat")
    public ApiResponse<?> chat(@RequestBody ApiAiChatRequest request) {
        return ApiResponse.success(aiAssistantService.chat(request));
    }

    /**
     * Mock ingredient analysis endpoint.
     * Keep this contract stable until a real AI service is connected.
     */
    @GetMapping("/ingredient")
    public ApiResponse<?> ingredient(@RequestParam(value = "name", required = false) String name) {
        return ApiResponse.success(aiAssistantService.ingredient(name));
    }

    /**
     * Mock skincare recommendation endpoint.
     * Returns fixed suggestions without calling any external model API.
     */
    @GetMapping("/recommend")
    public ApiResponse<?> recommend(@RequestParam(value = "skinType", required = false) String skinType) {
        return ApiResponse.success(aiAssistantService.recommend(skinType));
    }
}
