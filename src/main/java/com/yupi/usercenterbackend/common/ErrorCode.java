package com.yupi.usercenterbackend.common;

/**
 * 全局错误码
 */
public enum ErrorCode {
    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(400001, "请求参数为空", ""),
    NO_LOGIN(40100, "未登录",""),
    NO_AUTH(40101, "无权限",""),
    ACCOUNT_REPEATED(40102, "用户重复注册", ""),
    NOT_EXIST(40103, "用户不存在", ""),
    SYSTEM_ERROR(50000, "系统内部异常","");

    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
