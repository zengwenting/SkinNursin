package com.tacomall.apima.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiAiIngredientVo {

    private String name;

    private String effect;

    private String caution;

    private String matchTip;
}
