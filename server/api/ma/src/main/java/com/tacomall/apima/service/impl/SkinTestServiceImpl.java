package com.tacomall.apima.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tacomall.apima.dto.ApiSkinTestRequest;
import com.tacomall.apima.entity.SkinTest;
import com.tacomall.apima.mapper.SkinTestMapper;
import com.tacomall.apima.service.SkinTestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkinTestServiceImpl implements SkinTestService {

    private final SkinTestMapper skinTestMapper;

    @Override
    public SkinTest create(ApiSkinTestRequest request) {
        SkinTest skinTest = SkinTest.builder()
                .userId(request.getUserId() == null ? 1 : request.getUserId())
                .testDate(LocalDate.now())
                .skinType(request.getSkinType())
                .hydrationScore(request.getHydrationScore())
                .oilinessScore(request.getOilinessScore())
                .sensitivityScore(request.getSensitivityScore())
                .poreScore(request.getPoreScore())
                .blackheadScore(request.getBlackheadScore())
                .summary("本次检测显示屏障状态整体平稳，建议继续保持规律作息。")
                .advice("建议加强补水护理，减少过度清洁，并持续观察鼻翼与面颊区域。")
                .build();
        skinTestMapper.insert(skinTest);
        return skinTestMapper.selectById(skinTest.getId());
    }

    @Override
    public List<SkinTest> history(Integer userId) {
        return skinTestMapper.selectList(new QueryWrapper<SkinTest>().lambda()
                .eq(SkinTest::getUserId, userId == null ? 1 : userId)
                .eq(SkinTest::getIsDelete, 0)
                .orderByDesc(SkinTest::getTestDate, SkinTest::getCreateTime));
    }
}
