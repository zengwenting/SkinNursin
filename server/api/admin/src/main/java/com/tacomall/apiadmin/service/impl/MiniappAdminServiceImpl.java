package com.tacomall.apiadmin.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tacomall.apiadmin.entity.Checkin;
import com.tacomall.apiadmin.entity.CheckinItem;
import com.tacomall.apiadmin.entity.SkinTest;
import com.tacomall.apiadmin.entity.UserProfile;
import com.tacomall.apiadmin.mapper.CheckinItemMapper;
import com.tacomall.apiadmin.mapper.CheckinMapper;
import com.tacomall.apiadmin.mapper.SkinTestMapper;
import com.tacomall.apiadmin.mapper.UserProfileMapper;
import com.tacomall.apiadmin.service.MiniappAdminService;
import com.tacomall.apiadmin.vo.mini.MiniCheckinItemVo;
import com.tacomall.apiadmin.vo.mini.MiniDashboardVo;
import com.tacomall.apiadmin.vo.mini.MiniSkinTestItemVo;
import com.tacomall.apiadmin.vo.mini.MiniStatItemVo;
import com.tacomall.apiadmin.vo.mini.MiniUserItemVo;
import com.tacomall.common.json.ResponseJson;
import com.tacomall.common.json.ResponsePageJson;

import cn.hutool.core.util.ObjectUtil;

@Service
public class MiniappAdminServiceImpl implements MiniappAdminService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private SkinTestMapper skinTestMapper;

    @Autowired
    private CheckinMapper checkinMapper;

    @Autowired
    private CheckinItemMapper checkinItemMapper;

    @Override
    public ResponseJson<MiniDashboardVo> dashboard() {
        ResponseJson<MiniDashboardVo> responseJson = new ResponseJson<>();

        List<UserProfile> recentUsers = userProfileMapper.selectList(
                new LambdaQueryWrapper<UserProfile>()
                        .eq(UserProfile::getIsDelete, 0)
                        .orderByDesc(UserProfile::getCreateTime)
                        .last("limit 5"));
        List<SkinTest> recentSkinTests = skinTestMapper.selectList(
                new LambdaQueryWrapper<SkinTest>()
                        .eq(SkinTest::getIsDelete, 0)
                        .orderByDesc(SkinTest::getTestDate)
                        .orderByDesc(SkinTest::getCreateTime)
                        .last("limit 5"));
        List<Checkin> recentCheckins = checkinMapper.selectList(
                new LambdaQueryWrapper<Checkin>()
                        .eq(Checkin::getIsDelete, 0)
                        .orderByDesc(Checkin::getCheckinDate)
                        .orderByDesc(Checkin::getCreateTime)
                        .last("limit 5"));

        Map<Integer, UserProfile> userMap = getUserMap(
                java.util.stream.Stream.concat(
                        recentSkinTests.stream().map(SkinTest::getUserId),
                        recentCheckins.stream().map(Checkin::getUserId))
                        .collect(Collectors.toSet()));

        responseJson.setData(MiniDashboardVo.builder()
                .userCount(userProfileMapper.selectCount(new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getIsDelete, 0)))
                .skinTestCount(skinTestMapper.selectCount(new LambdaQueryWrapper<SkinTest>().eq(SkinTest::getIsDelete, 0)))
                .checkinCount(checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>().eq(Checkin::getIsDelete, 0)))
                .skinTypeDistribution(buildSkinTypeDistribution())
                .recentCheckinTrend(buildRecentCheckinTrend())
                .recentUsers(recentUsers.stream().map(this::toUserItem).collect(Collectors.toList()))
                .recentSkinTests(recentSkinTests.stream().map(item -> toSkinTestItem(item, userMap)).collect(Collectors.toList()))
                .recentCheckins(recentCheckins.stream().map(item -> toCheckinItem(item, userMap)).collect(Collectors.toList()))
                .build());
        responseJson.ok();
        return responseJson;
    }

    @Override
    public ResponsePageJson<List<MiniUserItemVo>> userPage(Integer pageIndex, Integer pageSize, JSONObject json) {
        ResponsePageJson<List<MiniUserItemVo>> response = new ResponsePageJson<>();
        JSONObject query = json == null ? null : json.getJSONObject("query");
        Page<UserProfile> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getIsDelete, 0)
                .orderByDesc(UserProfile::getCreateTime);
        if (ObjectUtil.isNotEmpty(query)) {
            if (ObjectUtil.isNotEmpty(query.getString("keyword"))) {
                String keyword = query.getString("keyword");
                wrapper.and(q -> q.like(UserProfile::getNickname, keyword)
                        .or().like(UserProfile::getAccount, keyword)
                        .or().like(UserProfile::getSkinGoal, keyword));
            }
            if (ObjectUtil.isNotEmpty(query.getString("skinType"))) {
                wrapper.eq(UserProfile::getSkinType, query.getString("skinType"));
            }
        }
        IPage<UserProfile> result = userProfileMapper.selectPage(page, wrapper);
        response.setData(result.getRecords().stream().map(this::toUserItem).collect(Collectors.toList()));
        response.buildPage(result.getCurrent(), result.getSize(), result.getTotal());
        response.ok();
        return response;
    }

    @Override
    public ResponseJson<MiniUserItemVo> userInfo(Integer id) {
        ResponseJson<MiniUserItemVo> responseJson = new ResponseJson<>();
        UserProfile item = userProfileMapper.selectById(id);
        responseJson.setData(item == null ? null : toUserItem(item));
        responseJson.ok();
        return responseJson;
    }

    @Override
    public ResponseJson<String> userUpdate(Integer id, JSONObject json) {
        ResponseJson<String> responseJson = new ResponseJson<>();
        LambdaUpdateWrapper<UserProfile> wrapper = new LambdaUpdateWrapper<UserProfile>()
                .eq(UserProfile::getId, id);
        
        if (json.containsKey("nickname")) {
            wrapper.set(UserProfile::getNickname, json.getString("nickname"));
        }
        if (json.containsKey("account")) {
            wrapper.set(UserProfile::getAccount, json.getString("account"));
        }
        if (json.containsKey("age")) {
            wrapper.set(UserProfile::getAge, json.getInteger("age"));
        }
        if (json.containsKey("gender")) {
            wrapper.set(UserProfile::getGender, json.getString("gender"));
        }
        if (json.containsKey("skinType")) {
            wrapper.set(UserProfile::getSkinType, json.getString("skinType"));
        }
        if (json.containsKey("skinGoal")) {
            wrapper.set(UserProfile::getSkinGoal, json.getString("skinGoal"));
        }
        if (json.containsKey("isSensitive")) {
            wrapper.set(UserProfile::getIsSensitive, json.getBoolean("isSensitive"));
        }
        if (json.containsKey("sensitiveSource")) {
            wrapper.set(UserProfile::getSensitiveSource, json.getString("sensitiveSource"));
        }
        if (json.containsKey("bio")) {
            wrapper.set(UserProfile::getBio, json.getString("bio"));
        }
        if (json.containsKey("status")) {
            wrapper.set(UserProfile::getStatus, json.getInteger("status"));
        }
        if (json.containsKey("lastLoginTime")) {
            wrapper.set(UserProfile::getLastLoginTime, json.getObject("lastLoginTime", LocalDateTime.class));
        }
        
        userProfileMapper.update(null, wrapper);
        responseJson.setData("用户信息更新成功");
        responseJson.ok();
        return responseJson;
    }

    @Override
    public ResponsePageJson<List<MiniSkinTestItemVo>> skinTestPage(Integer pageIndex, Integer pageSize, JSONObject json) {
        ResponsePageJson<List<MiniSkinTestItemVo>> response = new ResponsePageJson<>();
        JSONObject query = json == null ? null : json.getJSONObject("query");
        Page<SkinTest> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<SkinTest> wrapper = new LambdaQueryWrapper<SkinTest>()
                .eq(SkinTest::getIsDelete, 0)
                .orderByDesc(SkinTest::getTestDate)
                .orderByDesc(SkinTest::getCreateTime);
        if (ObjectUtil.isNotEmpty(query)) {
            if (ObjectUtil.isNotEmpty(query.getString("keyword"))) {
                String keyword = query.getString("keyword");
                Set<Integer> userIds = findUserIdsByKeyword(keyword);
                wrapper.and(q -> q.like(SkinTest::getSummary, keyword)
                        .or().like(SkinTest::getAdvice, keyword)
                        .or().in(!userIds.isEmpty(), SkinTest::getUserId, userIds));
            }
            if (ObjectUtil.isNotEmpty(query.getString("skinType"))) {
                wrapper.eq(SkinTest::getSkinType, query.getString("skinType"));
            }
        }
        IPage<SkinTest> result = skinTestMapper.selectPage(page, wrapper);
        Map<Integer, UserProfile> userMap = getUserMap(result.getRecords().stream().map(SkinTest::getUserId).collect(Collectors.toSet()));
        response.setData(result.getRecords().stream().map(item -> toSkinTestItem(item, userMap)).collect(Collectors.toList()));
        response.buildPage(result.getCurrent(), result.getSize(), result.getTotal());
        response.ok();
        return response;
    }

    @Override
    public ResponseJson<MiniSkinTestItemVo> skinTestInfo(Integer id) {
        ResponseJson<MiniSkinTestItemVo> responseJson = new ResponseJson<>();
        SkinTest item = skinTestMapper.selectById(id);
        Map<Integer, UserProfile> userMap = item == null ? Collections.emptyMap() : getUserMap(Set.of(item.getUserId()));
        responseJson.setData(item == null ? null : toSkinTestItem(item, userMap));
        responseJson.ok();
        return responseJson;
    }

    @Override
    public ResponseJson<String> skinTestUpdate(Integer id, JSONObject json) {
        ResponseJson<String> responseJson = new ResponseJson<>();
        skinTestMapper.update(null, new LambdaUpdateWrapper<SkinTest>()
                .eq(SkinTest::getId, id)
                .set(SkinTest::getSkinType, json.getString("skinType"))
                .set(SkinTest::getHydrationScore, json.getInteger("hydrationScore"))
                .set(SkinTest::getOilinessScore, json.getInteger("oilinessScore"))
                .set(SkinTest::getSensitivityScore, json.getInteger("sensitivityScore"))
                .set(SkinTest::getPoreScore, json.getInteger("poreScore"))
                .set(SkinTest::getBlackheadScore, json.getInteger("blackheadScore"))
                .set(SkinTest::getSummary, json.getString("summary"))
                .set(SkinTest::getAdvice, json.getString("advice")));
        responseJson.setData("肤质测试记录更新成功");
        responseJson.ok();
        return responseJson;
    }

    @Override
    public ResponsePageJson<List<MiniCheckinItemVo>> checkinPage(Integer pageIndex, Integer pageSize, JSONObject json) {
        ResponsePageJson<List<MiniCheckinItemVo>> response = new ResponsePageJson<>();
        JSONObject query = json == null ? null : json.getJSONObject("query");
        Page<Checkin> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<Checkin> wrapper = new LambdaQueryWrapper<Checkin>()
                .eq(Checkin::getIsDelete, 0)
                .orderByDesc(Checkin::getCheckinDate)
                .orderByDesc(Checkin::getCreateTime);
        if (ObjectUtil.isNotEmpty(query) && ObjectUtil.isNotEmpty(query.getString("keyword"))) {
            String keyword = query.getString("keyword");
            Set<Integer> userIds = findUserIdsByKeyword(keyword);
            wrapper.and(q -> q.like(Checkin::getSkinStatus, keyword)
                    .or().like(Checkin::getNote, keyword)
                    .or().in(!userIds.isEmpty(), Checkin::getUserId, userIds));
        }
        IPage<Checkin> result = checkinMapper.selectPage(page, wrapper);
        Map<Integer, UserProfile> userMap = getUserMap(result.getRecords().stream().map(Checkin::getUserId).collect(Collectors.toSet()));
        response.setData(result.getRecords().stream().map(item -> toCheckinItem(item, userMap)).collect(Collectors.toList()));
        response.buildPage(result.getCurrent(), result.getSize(), result.getTotal());
        response.ok();
        return response;
    }

    @Override
    public ResponseJson<MiniCheckinItemVo> checkinInfo(Integer id) {
        ResponseJson<MiniCheckinItemVo> responseJson = new ResponseJson<>();
        Checkin item = checkinMapper.selectById(id);
        Map<Integer, UserProfile> userMap = item == null ? Collections.emptyMap() : getUserMap(Set.of(item.getUserId()));
        responseJson.setData(item == null ? null : toCheckinItem(item, userMap));
        responseJson.ok();
        return responseJson;
    }

    @Override
    public ResponseJson<String> checkinUpdate(Integer id, JSONObject json) {
        ResponseJson<String> responseJson = new ResponseJson<>();
        checkinMapper.update(null, new LambdaUpdateWrapper<Checkin>()
                .eq(Checkin::getId, id)
                .set(Checkin::getSkinStatus, json.getString("skinStatus"))
                .set(Checkin::getHydrationScore, json.getInteger("hydrationScore"))
                .set(Checkin::getOilinessScore, json.getInteger("oilinessScore"))
                .set(Checkin::getSensitivityScore, json.getInteger("sensitivityScore"))
                .set(Checkin::getNote, json.getString("note")));
        responseJson.setData("护理打卡记录更新成功");
        responseJson.ok();
        return responseJson;
    }

    private Set<Integer> findUserIdsByKeyword(String keyword) {
        return userProfileMapper.selectList(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getIsDelete, 0)
                .and(q -> q.like(UserProfile::getNickname, keyword).or().like(UserProfile::getAccount, keyword)))
                .stream()
                .map(UserProfile::getId)
                .collect(Collectors.toSet());
    }

    private Map<Integer, UserProfile> getUserMap(Set<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userProfileMapper.selectBatchIds(userIds).stream()
                .filter(item -> item.getIsDelete() != null && item.getIsDelete() == 0)
                .collect(Collectors.toMap(UserProfile::getId, Function.identity(), (left, right) -> left));
    }

    private MiniUserItemVo toUserItem(UserProfile item) {
        Long skinTestCount = skinTestMapper.selectCount(new LambdaQueryWrapper<SkinTest>().eq(SkinTest::getIsDelete, 0).eq(SkinTest::getUserId, item.getId()));
        Long checkinCount = checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>().eq(Checkin::getIsDelete, 0).eq(Checkin::getUserId, item.getId()));
        SkinTest lastSkinTest = skinTestMapper.selectOne(new LambdaQueryWrapper<SkinTest>().eq(SkinTest::getIsDelete, 0).eq(SkinTest::getUserId, item.getId()).orderByDesc(SkinTest::getTestDate).last("limit 1"));
        Checkin lastCheckin = checkinMapper.selectOne(new LambdaQueryWrapper<Checkin>().eq(Checkin::getIsDelete, 0).eq(Checkin::getUserId, item.getId()).orderByDesc(Checkin::getCheckinDate).last("limit 1"));
        return MiniUserItemVo.builder()
                .id(item.getId())
                .nickname(item.getNickname())
                .account(item.getAccount())
                .age(item.getAge())
                .gender(item.getGender())
                .skinType(item.getSkinType())
                .skinGoal(item.getSkinGoal())
                .isSensitive(item.getIsSensitive())
                .sensitiveSource(item.getSensitiveSource())
                .bio(item.getBio())
                .status(item.getStatus())
                .lastLoginTime(item.getLastLoginTime())
                .skinTestCount(skinTestCount)
                .checkinCount(checkinCount)
                .lastSkinTestDate(lastSkinTest == null || lastSkinTest.getTestDate() == null ? null : lastSkinTest.getTestDate().format(DATE_FORMATTER))
                .lastCheckinDate(lastCheckin == null || lastCheckin.getCheckinDate() == null ? null : lastCheckin.getCheckinDate().format(DATE_FORMATTER))
                .createTime(item.getCreateTime())
                .build();
    }

    private MiniSkinTestItemVo toSkinTestItem(SkinTest item, Map<Integer, UserProfile> userMap) {
        UserProfile user = userMap.get(item.getUserId());
        return MiniSkinTestItemVo.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .nickname(user == null ? "-" : user.getNickname())
                .testDate(item.getTestDate())
                .skinType(item.getSkinType())
                .hydrationScore(item.getHydrationScore())
                .oilinessScore(item.getOilinessScore())
                .sensitivityScore(item.getSensitivityScore())
                .poreScore(item.getPoreScore())
                .blackheadScore(item.getBlackheadScore())
                .summary(item.getSummary())
                .advice(item.getAdvice())
                .createTime(item.getCreateTime())
                .build();
    }

    private MiniCheckinItemVo toCheckinItem(Checkin item, Map<Integer, UserProfile> userMap) {
        UserProfile user = userMap.get(item.getUserId());
        Long cosmeticCount = checkinItemMapper.selectCount(new LambdaQueryWrapper<CheckinItem>().eq(CheckinItem::getIsDelete, 0).eq(CheckinItem::getCheckinId, item.getId()));
        return MiniCheckinItemVo.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .nickname(user == null ? "-" : user.getNickname())
                .checkinDate(item.getCheckinDate())
                .skinStatus(item.getSkinStatus())
                .hydrationScore(item.getHydrationScore())
                .oilinessScore(item.getOilinessScore())
                .sensitivityScore(item.getSensitivityScore())
                .note(item.getNote())
                .cosmeticCount(cosmeticCount)
                .createTime(item.getCreateTime())
                .build();
    }

    private List<MiniStatItemVo> buildSkinTypeDistribution() {
        return userProfileMapper.selectList(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getIsDelete, 0))
                .stream()
                .collect(Collectors.groupingBy(item -> {
                    if (item.getSkinType() == null || item.getSkinType().isBlank()) {
                        return "未填写";
                    }
                    return item.getSkinType();
                }, Collectors.counting()))
                .entrySet()
                .stream()
                .map(item -> MiniStatItemVo.builder().label(item.getKey()).value(item.getValue()).build())
                .collect(Collectors.toList());
    }

    private List<MiniStatItemVo> buildRecentCheckinTrend() {
        return checkinMapper.selectList(new LambdaQueryWrapper<Checkin>()
                .eq(Checkin::getIsDelete, 0)
                .orderByDesc(Checkin::getCheckinDate)
                .last("limit 30"))
                .stream()
                .collect(Collectors.groupingBy(item -> item.getCheckinDate().format(DATE_FORMATTER), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(item -> MiniStatItemVo.builder().label(item.getKey()).value(item.getValue()).build())
                .collect(Collectors.toList());
    }
}
