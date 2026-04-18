package com.tacomall.apiadmin.service.impl;

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
import com.tacomall.apiadmin.entity.CosmeticItem;
import com.tacomall.apiadmin.entity.SkinTest;
import com.tacomall.apiadmin.entity.UserProfile;
import com.tacomall.apiadmin.mapper.CheckinItemMapper;
import com.tacomall.apiadmin.mapper.CheckinMapper;
import com.tacomall.apiadmin.mapper.CosmeticItemMapper;
import com.tacomall.apiadmin.mapper.SkinTestMapper;
import com.tacomall.apiadmin.mapper.UserProfileMapper;
import com.tacomall.apiadmin.service.MiniappAdminService;
import com.tacomall.apiadmin.vo.mini.MiniCheckinItemVo;
import com.tacomall.apiadmin.vo.mini.MiniCosmeticItemVo;
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

    @Autowired
    private CosmeticItemMapper cosmeticItemMapper;

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
        List<CosmeticItem> recentCosmetics = cosmeticItemMapper.selectList(
                new LambdaQueryWrapper<CosmeticItem>()
                        .eq(CosmeticItem::getIsDelete, 0)
                        .orderByDesc(CosmeticItem::getCreateTime)
                        .last("limit 5"));

        Map<Integer, UserProfile> userMap = getUserMap(
                java.util.stream.Stream.concat(
                        java.util.stream.Stream.concat(
                                recentSkinTests.stream().map(SkinTest::getUserId),
                                recentCheckins.stream().map(Checkin::getUserId)),
                        recentCosmetics.stream().map(CosmeticItem::getUserId))
                        .collect(Collectors.toSet()));

        responseJson.setData(MiniDashboardVo.builder()
                .userCount(userProfileMapper.selectCount(new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getIsDelete, 0)))
                .skinTestCount(skinTestMapper.selectCount(new LambdaQueryWrapper<SkinTest>().eq(SkinTest::getIsDelete, 0)))
                .checkinCount(checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>().eq(Checkin::getIsDelete, 0)))
                .cosmeticCount(cosmeticItemMapper.selectCount(new LambdaQueryWrapper<CosmeticItem>().eq(CosmeticItem::getIsDelete, 0)))
                .skinTypeDistribution(buildSkinTypeDistribution())
                .cosmeticCategoryDistribution(buildCosmeticCategoryDistribution())
                .recentCheckinTrend(buildRecentCheckinTrend())
                .recentUsers(recentUsers.stream().map(this::toUserItem).collect(Collectors.toList()))
                .recentSkinTests(recentSkinTests.stream().map(item -> toSkinTestItem(item, userMap)).collect(Collectors.toList()))
                .recentCheckins(recentCheckins.stream().map(item -> toCheckinItem(item, userMap)).collect(Collectors.toList()))
                .recentCosmetics(recentCosmetics.stream().map(item -> toCosmeticItem(item, userMap)).collect(Collectors.toList()))
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
        userProfileMapper.update(null, new LambdaUpdateWrapper<UserProfile>()
                .eq(UserProfile::getId, id)
                .set(UserProfile::getNickname, json.getString("nickname"))
                .set(UserProfile::getAccount, json.getString("account"))
                .set(UserProfile::getAge, json.getInteger("age"))
                .set(UserProfile::getGender, json.getString("gender"))
                .set(UserProfile::getSkinType, json.getString("skinType"))
                .set(UserProfile::getSkinGoal, json.getString("skinGoal"))
                .set(UserProfile::getBio, json.getString("bio")));
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

    @Override
    public ResponsePageJson<List<MiniCosmeticItemVo>> cosmeticPage(Integer pageIndex, Integer pageSize, JSONObject json) {
        ResponsePageJson<List<MiniCosmeticItemVo>> response = new ResponsePageJson<>();
        JSONObject query = json == null ? null : json.getJSONObject("query");
        Page<CosmeticItem> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<CosmeticItem> wrapper = new LambdaQueryWrapper<CosmeticItem>()
                .eq(CosmeticItem::getIsDelete, 0)
                .orderByDesc(CosmeticItem::getCreateTime);
        if (ObjectUtil.isNotEmpty(query)) {
            if (ObjectUtil.isNotEmpty(query.getString("keyword"))) {
                String keyword = query.getString("keyword");
                Set<Integer> userIds = findUserIdsByKeyword(keyword);
                wrapper.and(q -> q.like(CosmeticItem::getName, keyword)
                        .or().like(CosmeticItem::getBrand, keyword)
                        .or().like(CosmeticItem::getEffectTag, keyword)
                        .or().in(!userIds.isEmpty(), CosmeticItem::getUserId, userIds));
            }
            if (ObjectUtil.isNotEmpty(query.getString("category"))) {
                wrapper.eq(CosmeticItem::getCategory, query.getString("category"));
            }
        }
        IPage<CosmeticItem> result = cosmeticItemMapper.selectPage(page, wrapper);
        Map<Integer, UserProfile> userMap = getUserMap(result.getRecords().stream().map(CosmeticItem::getUserId).collect(Collectors.toSet()));
        response.setData(result.getRecords().stream().map(item -> toCosmeticItem(item, userMap)).collect(Collectors.toList()));
        response.buildPage(result.getCurrent(), result.getSize(), result.getTotal());
        response.ok();
        return response;
    }

    @Override
    public ResponseJson<MiniCosmeticItemVo> cosmeticInfo(Integer id) {
        ResponseJson<MiniCosmeticItemVo> responseJson = new ResponseJson<>();
        CosmeticItem item = cosmeticItemMapper.selectById(id);
        Map<Integer, UserProfile> userMap = item == null ? Collections.emptyMap() : getUserMap(Set.of(item.getUserId()));
        responseJson.setData(item == null ? null : toCosmeticItem(item, userMap));
        responseJson.ok();
        return responseJson;
    }

    @Override
    public ResponseJson<String> cosmeticUpdate(Integer id, JSONObject json) {
        ResponseJson<String> responseJson = new ResponseJson<>();
        cosmeticItemMapper.update(null, new LambdaUpdateWrapper<CosmeticItem>()
                .eq(CosmeticItem::getId, id)
                .set(CosmeticItem::getName, json.getString("name"))
                .set(CosmeticItem::getBrand, json.getString("brand"))
                .set(CosmeticItem::getCategory, json.getString("category"))
                .set(CosmeticItem::getImageUrl, json.getString("imageUrl"))
                .set(CosmeticItem::getEffectTag, json.getString("effectTag"))
                .set(CosmeticItem::getIngredient, json.getString("ingredient"))
                .set(CosmeticItem::getUsePeriod, json.getString("usePeriod"))
                .set(CosmeticItem::getNote, json.getString("note")));
        responseJson.setData("化妆台产品更新成功");
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
        Long cosmeticCount = cosmeticItemMapper.selectCount(new LambdaQueryWrapper<CosmeticItem>().eq(CosmeticItem::getIsDelete, 0).eq(CosmeticItem::getUserId, item.getId()));
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
                .bio(item.getBio())
                .skinTestCount(skinTestCount)
                .checkinCount(checkinCount)
                .cosmeticCount(cosmeticCount)
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

    private MiniCosmeticItemVo toCosmeticItem(CosmeticItem item, Map<Integer, UserProfile> userMap) {
        UserProfile user = userMap.get(item.getUserId());
        Long usageCount = checkinItemMapper.selectCount(new LambdaQueryWrapper<CheckinItem>().eq(CheckinItem::getIsDelete, 0).eq(CheckinItem::getCosmeticId, item.getId()));
        return MiniCosmeticItemVo.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .nickname(user == null ? "-" : user.getNickname())
                .name(item.getName())
                .brand(item.getBrand())
                .category(item.getCategory())
                .imageUrl(item.getImageUrl())
                .productionDate(item.getProductionDate())
                .expireDate(item.getExpireDate())
                .effectTag(item.getEffectTag())
                .ingredient(item.getIngredient())
                .usePeriod(item.getUsePeriod())
                .note(item.getNote())
                .usageCount(usageCount)
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

    private List<MiniStatItemVo> buildCosmeticCategoryDistribution() {
        return cosmeticItemMapper.selectList(new LambdaQueryWrapper<CosmeticItem>()
                .eq(CosmeticItem::getIsDelete, 0))
                .stream()
                .collect(Collectors.groupingBy(item -> {
                    if (item.getCategory() == null || item.getCategory().isBlank()) {
                        return "未分类";
                    }
                    return item.getCategory();
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
