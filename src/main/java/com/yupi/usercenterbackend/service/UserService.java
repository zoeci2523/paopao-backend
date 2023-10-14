package com.yupi.usercenterbackend.service;

import com.yupi.usercenterbackend.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author fengxiaoha
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-09-10 11:56:33
*/
public interface UserService extends IService<User> {

    long register(String account, String password, String checkPassword, String planetCode);

    User login(String userAccount, String password, HttpServletRequest request);

    User getCleanUser(User user);

    int userLogout(HttpServletRequest request);

    List<User> searchUsersByTags(List<String> tagNameList);
}
