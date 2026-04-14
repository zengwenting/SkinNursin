package com.tacomall.apima.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("cosmetic_item")
public class CosmeticItem {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String name;

    private String brand;

    private String category;

    private String imageUrl;

    private LocalDate productionDate;

    private LocalDate expireDate;

    private String effectTag;

    private String ingredient;

    private String usePeriod;

    private String note;

    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
