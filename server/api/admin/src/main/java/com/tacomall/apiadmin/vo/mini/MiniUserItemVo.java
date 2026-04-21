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
    private Boolean isSensitive;
    private String sensitiveSource;
    private String bio;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private Long skinTestCount;
    private Long checkinCount;
    private String lastSkinTestDate;
    private String lastCheckinDate;
    private LocalDateTime createTime;
}
