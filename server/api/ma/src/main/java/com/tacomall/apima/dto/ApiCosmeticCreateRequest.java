package com.tacomall.apima.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ApiCosmeticCreateRequest {

    private Integer userId;

    private String name;

    private String brand;

    private String category;

    private String imageUrl;

    private LocalDate productionDate;

    private LocalDate expireDate;

    private String effectTag;

    private String ingredient;

    private String usePeriod;

    private String note;
}
