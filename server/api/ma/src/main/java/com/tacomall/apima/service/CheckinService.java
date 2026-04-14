package com.tacomall.apima.service;

import com.tacomall.apima.dto.ApiCheckinCreateRequest;
import com.tacomall.apima.vo.ApiCheckinCalendarVo;
import com.tacomall.apima.vo.ApiCheckinDetailVo;
import com.tacomall.apima.vo.ApiCheckinTodayVo;

public interface CheckinService {

    ApiCheckinDetailVo create(ApiCheckinCreateRequest request);

    ApiCheckinCalendarVo calendar(Integer userId);

    ApiCheckinDetailVo detail(Integer id, Integer userId, String date);

    ApiCheckinTodayVo today(Integer userId);
}
