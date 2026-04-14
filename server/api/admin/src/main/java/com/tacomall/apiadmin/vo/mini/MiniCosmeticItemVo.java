package com.tacomall.apiadmin.vo.mini;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniCosmeticItemVo {
    private Integer id;
    private Integer userId;
    private String nickname;
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
    private Long usageCount;
    private LocalDateTime createTime;
}
