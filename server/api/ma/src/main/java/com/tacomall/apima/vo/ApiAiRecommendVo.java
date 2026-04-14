package com.tacomall.apima.vo;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiAiRecommendVo {

    private String skinType;

    private String reply;

    private List<String> routine;
}
