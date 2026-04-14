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
public class MiniCheckinItemVo {
    private Integer id;
    private Integer userId;
    private String nickname;
    private LocalDate checkinDate;
    private String skinStatus;
    private Integer hydrationScore;
    private Integer oilinessScore;
    private Integer sensitivityScore;
    private String note;
    private Long cosmeticCount;
    private LocalDateTime createTime;
}
