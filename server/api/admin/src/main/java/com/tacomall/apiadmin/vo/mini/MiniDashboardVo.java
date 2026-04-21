package com.tacomall.apiadmin.vo.mini;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniDashboardVo {
    private Long userCount;
    private Long skinTestCount;
    private Long checkinCount;
    private List<MiniStatItemVo> skinTypeDistribution;
    private List<MiniStatItemVo> recentCheckinTrend;
    private List<MiniUserItemVo> recentUsers;
    private List<MiniSkinTestItemVo> recentSkinTests;
    private List<MiniCheckinItemVo> recentCheckins;
}
