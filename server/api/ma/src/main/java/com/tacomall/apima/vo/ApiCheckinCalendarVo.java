package com.tacomall.apima.vo;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiCheckinCalendarVo {

    private Integer userId;

    private List<String> dates;
}
