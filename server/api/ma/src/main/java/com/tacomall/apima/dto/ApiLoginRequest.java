package com.tacomall.apima.dto;

import lombok.Data;

@Data
public class ApiLoginRequest {

    private String code;

    private String nickname;

    private String avatar;
}
