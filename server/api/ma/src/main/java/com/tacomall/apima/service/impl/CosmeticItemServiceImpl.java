package com.tacomall.apima.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tacomall.apima.dto.ApiCosmeticCreateRequest;
import com.tacomall.apima.entity.CosmeticItem;
import com.tacomall.apima.mapper.CosmeticItemMapper;
import com.tacomall.apima.service.CosmeticItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CosmeticItemServiceImpl implements CosmeticItemService {

    private final CosmeticItemMapper cosmeticItemMapper;

    @Override
    public List<CosmeticItem> list(Integer userId, String category) {
        QueryWrapper<CosmeticItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(CosmeticItem::getIsDelete, 0)
                .eq(CosmeticItem::getUserId, userId == null ? 1 : userId)
                .orderByDesc(CosmeticItem::getCreateTime);
        if (category != null && !category.isBlank()) {
            queryWrapper.lambda().eq(CosmeticItem::getCategory, category);
        }
        return cosmeticItemMapper.selectList(queryWrapper);
    }

    @Override
    public CosmeticItem create(ApiCosmeticCreateRequest request) {
        CosmeticItem item = CosmeticItem.builder()
                .userId(request.getUserId() == null ? 1 : request.getUserId())
                .name(request.getName())
                .brand(request.getBrand())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .productionDate(request.getProductionDate())
                .expireDate(request.getExpireDate())
                .effectTag(request.getEffectTag())
                .ingredient(request.getIngredient())
                .usePeriod(request.getUsePeriod())
                .note(request.getNote())
                .isDelete(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        cosmeticItemMapper.insert(item);
        return cosmeticItemMapper.selectById(item.getId());
    }

    @Override
    public Boolean delete(Integer id) {
        return cosmeticItemMapper.update(null, new UpdateWrapper<CosmeticItem>().lambda()
                .eq(CosmeticItem::getId, id)
                .set(CosmeticItem::getIsDelete, 1)) > 0;
    }
}
