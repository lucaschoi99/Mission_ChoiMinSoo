package com.bell.ringMyBell.base.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseData<T> {
    private String resultCode;
    private String msg;
    private T data;

    public static <T> ResponseData<T> of(String resultCode, String msg, T data) {
        return new ResponseData<>(resultCode, msg, data);
    }

    public static <T> ResponseData<T> of(String resultCode, String msg) {
        return of(resultCode, msg, null);
    }

    public static <T> ResponseData<T> successOf(T data) {
        return of("S-1", "성공", data);
    }

    public static <T> ResponseData<T> failOf(T data) {
        return of("F-1", "실패", data);
    }

    public boolean isSuccess() {
        return resultCode.startsWith("S-");
    }

    public boolean isFail() {
        return isSuccess() == false;
    }
}
