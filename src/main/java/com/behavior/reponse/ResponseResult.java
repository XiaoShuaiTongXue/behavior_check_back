package com.behavior.reponse;

public class ResponseResult {
    private boolean success;
    private int code;
    private String message;
    private Object data;


    public ResponseResult() {
    }

    public static ResponseResult Get(ResponseState state){
        return new ResponseResult(state);
    }

    public static ResponseResult SUCCESS(){
        return new ResponseResult(ResponseState.SUCCESS);
    }

    public static ResponseResult SUCCESS(String message){
        return new ResponseResult(ResponseState.SUCCESS).setMessage(message);
    }

    public static ResponseResult FAILED(){
        return new ResponseResult(ResponseState.FAILED);
    }

    public static ResponseResult FAILED(String message){
        return new ResponseResult(ResponseState.FAILED).setMessage(message);
    }

    public ResponseResult(ResponseState state) {
        this.success = state.isSuccess();
        this.code = state.getCode();
        this.message = state.getMessage();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public ResponseResult setMessage(String message) {
        this.message = message;
        return this;
    }
    public ResponseResult addPreMessage(String preMsg){
        this.message = preMsg + message;
        return this;
    }
    public Object getData() {
        return data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }
}
