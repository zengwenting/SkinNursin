package com.tacomall.apima.dto;

import java.util.List;

import lombok.Data;

@Data
public class ApiCheckinCreateRequest {

    private Integer userId;

    private String skinStatus;

    private Integer hydrationScore;

    private Integer oilinessScore;

    private Integer sensitivityScore;

    private String note;

    private List<Integer> cosmeticIds;
}
