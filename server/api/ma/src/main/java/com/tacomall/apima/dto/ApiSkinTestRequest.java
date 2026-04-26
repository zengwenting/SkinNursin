package com.tacomall.apima.dto;

import lombok.Data;

@Data
public class ApiSkinTestRequest {

    private Integer userId;

    private String testDate;

    private String skinType;

    private Integer hydrationScore;

    private Integer oilinessScore;

    private Integer sensitivityScore;

    private Integer poreScore;

    private Integer blackheadScore;

    private Integer score;

    private String summary;

    private String advice;
}
