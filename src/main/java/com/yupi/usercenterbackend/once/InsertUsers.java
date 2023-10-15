package com.yupi.usercenterbackend.once;

import com.yupi.usercenterbackend.mapper.UserMapper;
import com.yupi.usercenterbackend.model.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

//@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000; // 大概需要90秒
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeUser");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("189");
            user.setEmail("189@163.com");
            user.setProfile("假人一个");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("654321");
            user.setTags("[]");
            userMapper.insert(user);
        }
        stopWatch.stop();
    }
}
