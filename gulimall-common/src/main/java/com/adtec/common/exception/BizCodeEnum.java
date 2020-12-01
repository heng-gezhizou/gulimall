package com.adtec.common.exception;

public enum  BizCodeEnum {
    UNKNOWN_EXCEPTION(10000,"未知的系统异常"),
    VALID_EXCEPTION(10001,"参数校验异常"),
    ;
    private int code;
    private String msg;
    BizCodeEnum(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }
}
