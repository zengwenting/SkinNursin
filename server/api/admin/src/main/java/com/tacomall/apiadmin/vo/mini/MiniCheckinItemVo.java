package com.tacomall.apiadmin.vo.mini;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.tacomall.apiadmin.entity.CheckinItem;

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

    private Long cosmeticCount;
    private List<CheckinItem> cosmetics;
    private LocalDateTime createTime;
}
