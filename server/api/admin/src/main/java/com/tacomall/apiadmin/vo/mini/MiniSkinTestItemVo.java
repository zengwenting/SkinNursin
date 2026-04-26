package com.tacomall.apiadmin.vo.mini;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniSkinTestItemVo {
    private Integer id;
    private Integer userId;
    private String nickname;
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
    private LocalDateTime createTime;
}
