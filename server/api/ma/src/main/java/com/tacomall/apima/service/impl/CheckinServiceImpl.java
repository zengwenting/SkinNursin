package com.tacomall.apima.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tacomall.apima.dto.ApiCheckinCreateRequest;
import com.tacomall.apima.entity.Checkin;
import com.tacomall.apima.entity.CheckinItem;
import com.tacomall.apima.entity.CosmeticItem;
import com.tacomall.apima.mapper.CheckinItemMapper;
import com.tacomall.apima.mapper.CheckinMapper;
import com.tacomall.apima.mapper.CosmeticItemMapper;
import com.tacomall.apima.service.CheckinService;
import com.tacomall.apima.vo.ApiCheckinCalendarVo;
import com.tacomall.apima.vo.ApiCheckinDetailVo;
import com.tacomall.apima.vo.ApiCheckinTodayVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements CheckinService {

    private final CheckinMapper checkinMapper;
    private final CheckinItemMapper checkinItemMapper;
    private final CosmeticItemMapper cosmeticItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiCheckinDetailVo create(ApiCheckinCreateRequest request) {
        Integer userId = request.getUserId() == null ? 1 : request.getUserId();
        LocalDate today = LocalDate.now();
        Checkin existing = checkinMapper.selectOne(new QueryWrapper<Checkin>().lambda()
                .eq(Checkin::getUserId, userId)
                .eq(Checkin::getCheckinDate, today)
                .eq(Checkin::getIsDelete, 0));
        if (existing != null) {
            throw new IllegalStateException("today already checked in");
        }

        Checkin checkin = Checkin.builder()
                .userId(userId)
                .checkinDate(today)

                .isDelete(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        checkinMapper.insert(checkin);

        List<CheckinItem> items = new ArrayList<>();
        List<Integer> cosmeticIds = request.getCosmeticIds() == null ? Collections.emptyList() : request.getCosmeticIds();
        if (!cosmeticIds.isEmpty()) {
            List<CosmeticItem> cosmetics = cosmeticItemMapper.selectBatchIds(cosmeticIds);
            int order = 1;
            for (CosmeticItem cosmetic : cosmetics) {
                CheckinItem item = CheckinItem.builder()
                        .checkinId(checkin.getId())
                        .cosmeticId(cosmetic.getId())
                        .cosmeticName(cosmetic.getName())
                        .cosmeticImage(cosmetic.getImageUrl())
                        .category(cosmetic.getCategory())
                        .stepOrder(order++)
                        .isDelete(0)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                checkinItemMapper.insert(item);
                items.add(item);
            }
        }
        return ApiCheckinDetailVo.builder().checkin(checkin).items(items).build();
    }

    @Override
    public ApiCheckinCalendarVo calendar(Integer userId) {
        Integer targetUserId = userId == null ? 1 : userId;
        List<String> dates = checkinMapper.selectList(new QueryWrapper<Checkin>().lambda()
                .eq(Checkin::getUserId, targetUserId)
                .eq(Checkin::getIsDelete, 0)
                .orderByDesc(Checkin::getCheckinDate))
                .stream()
                .map(item -> item.getCheckinDate().toString())
                .collect(Collectors.toList());
        return ApiCheckinCalendarVo.builder().userId(targetUserId).dates(dates).build();
    }

    @Override
    public ApiCheckinDetailVo detail(Integer id, Integer userId, String date) {
        Checkin checkin;
        if (id != null) {
            checkin = checkinMapper.selectById(id);
        } else {
            Integer targetUserId = userId == null ? 1 : userId;
            checkin = checkinMapper.selectOne(new QueryWrapper<Checkin>().lambda()
                    .eq(Checkin::getUserId, targetUserId)
                    .eq(Checkin::getCheckinDate, LocalDate.parse(date))
                    .eq(Checkin::getIsDelete, 0));
        }
        if (checkin == null) {
            return ApiCheckinDetailVo.builder().checkin(null).items(Collections.emptyList()).build();
        }
        List<CheckinItem> items = checkinItemMapper.selectList(new QueryWrapper<CheckinItem>().lambda()
                .eq(CheckinItem::getCheckinId, checkin.getId())
                .eq(CheckinItem::getIsDelete, 0)
                .orderByAsc(CheckinItem::getStepOrder));
        return ApiCheckinDetailVo.builder().checkin(checkin).items(items).build();
    }

    @Override
    public ApiCheckinTodayVo today(Integer userId) {
        Integer targetUserId = userId == null ? 1 : userId;
        Checkin checkin = checkinMapper.selectOne(new QueryWrapper<Checkin>().lambda()
                .eq(Checkin::getUserId, targetUserId)
                .eq(Checkin::getCheckinDate, LocalDate.now())
                .eq(Checkin::getIsDelete, 0));
        if (checkin == null) {
            return ApiCheckinTodayVo.builder()
                    .checkedIn(false)
                    .checkin(null)
                    .items(Collections.emptyList())
                    .build();
        }
        List<CheckinItem> items = checkinItemMapper.selectList(new QueryWrapper<CheckinItem>().lambda()
                .eq(CheckinItem::getCheckinId, checkin.getId())
                .eq(CheckinItem::getIsDelete, 0)
                .orderByAsc(CheckinItem::getStepOrder));
        return ApiCheckinTodayVo.builder()
                .checkedIn(true)
                .checkin(checkin)
                .items(items)
                .build();
    }
}
