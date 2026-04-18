package com.tacomall.apiadmin.vo.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 统计项视图对象：用于仪表盘图表展示。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniStatItemVo {
    private String label;
    private Long value;
}
