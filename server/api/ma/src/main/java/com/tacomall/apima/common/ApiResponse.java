package com.tacomall.apima.common;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private Integer code;

    private T data;

    private String msg;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(0);
        response.setData(data);
        response.setMsg("success");
        return response;
    }

    public static <T> ApiResponse<T> failure(String msg) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(1);
        response.setData(null);
        response.setMsg(msg);
        return response;
    }
}
