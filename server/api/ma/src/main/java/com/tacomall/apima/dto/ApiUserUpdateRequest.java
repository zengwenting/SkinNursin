package com.tacomall.apima.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ApiUserUpdateRequest {

    private Integer id;

    private String nickname;

    private String avatar;

    private String account;



    private Integer age;

    private String gender;

   private String skinType;

    private String skinGoal;

    private Boolean isSensitive;

    private String sensitiveSource;

    private Integer status;

    private LocalDateTime lastLoginTime;

    private String openid;

    private String remindtime;

    private Boolean onclock;}
