package com.tacomall.apima.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiCheckinCreateRequest {

    private Integer userId;

    private List<Integer> cosmeticIds;
}
