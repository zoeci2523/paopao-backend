package com.yupi.usercenterbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {

    private String userAccount;
    private String password;
    private String checkPassword;
    private String planetCode;
}
