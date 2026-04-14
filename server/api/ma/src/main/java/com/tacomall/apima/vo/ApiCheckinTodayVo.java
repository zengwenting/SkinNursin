package com.tacomall.apima.vo;

import java.util.List;

import com.tacomall.apima.entity.Checkin;
import com.tacomall.apima.entity.CheckinItem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiCheckinTodayVo {

    private Boolean checkedIn;

    private Checkin checkin;

    private List<CheckinItem> items;
}
