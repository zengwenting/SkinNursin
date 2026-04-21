package com.tacomall.apima.dto;

import lombok.Data;

@Data
public class ApiUserUpdateRequest {

    private Integer id;

    private String nickname;

    private String avatar;

    private String account;

    private String password;

    private Integer age;

    private String gender;

    private String skinType;

    private String skinGoal;

    private Boolean isSensitive;

    private String sensitiveSource;

    private String bio;

    private Integer status;

    private java.time.LocalDateTime lastLoginTime;
}
