package com.tacomall.apiadmin.entity;

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
@TableName("skin_test")
public class SkinTest {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private LocalDate testDate;

    private String skinType;

    private Integer hydrationScore;

    private Integer oilinessScore;

    private Integer sensitivityScore;

    private Integer poreScore;

    private Integer blackheadScore;

    private String summary;

    private String advice;

    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
