package com.yupi.usercenterbackend.service;
import java.util.Date;

import com.yupi.usercenterbackend.model.User;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        //user.setId(0L);
        user.setUsername("cici");
        user.setUserAccount("123");
        user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.u1D8WJbUIIXMV2w-qa4r5wAAAA&w=176&h=185&c=8&rs=1&qlt=90&o=6&dpr=2&pid=3.1&rm=2");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        user.setUserRole(0);
        user.setPlanetCode("cici123");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);

    }

    @Test
    void userRegister(){
        String account = "cici";
        String password = "";
        String checkedPassword = "123456";
        String planetCode = "1";
        long result = userService.register(account, password, checkedPassword, planetCode);
        // 密码为空
        Assertions.assertEquals(-1, result);
        password = "123456";
        result = userService.register(account, password, checkedPassword, planetCode);
        // 密码长度不得小于8位
        Assertions.assertEquals(-1, result);
        account = "ci ci";
        password = "12345678";
        result = userService.register(account, password, checkedPassword, planetCode);
        // 用户名包含特殊字符
        Assertions.assertEquals(-1, result);
        checkedPassword = "12345678";
        // 账户名不得小于6位
        result = userService.register(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);
        account = "cicizoe2523";
        checkedPassword = "123456789";
        // 密码和校验密码不相同
        result = userService.register(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);
    }
}