package com.behavior.reponse;

public enum ResponseState {

    SUCCESS(2000,true,"获取成功"),
    FAILED(4000,false,"获取失败"),
    JOIN_IN_SUCCESS(2001,true,"注册成功"),
    LOGIN_IN_SUCCESS(1000,true,"登录成功");

    ResponseState(int code, boolean success, String message) {
        this.code = code;
        this.success = success;
        this.message = message;
    }
    private int code;
    private boolean success;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
