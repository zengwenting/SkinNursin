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
public class MiniUserItemVo {
    private Integer id;
    private String nickname;
    private String account;
    private Integer age;
    private String gender;
    private String skinType;
    private String skinGoal;
    private String bio;
    private Long skinTestCount;
    private Long checkinCount;
    private Long cosmeticCount;
    private String lastSkinTestDate;
    private String lastCheckinDate;
    private LocalDateTime createTime;
}
