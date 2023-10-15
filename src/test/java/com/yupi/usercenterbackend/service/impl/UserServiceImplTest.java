package com.yupi.usercenterbackend.service.impl;

import com.yupi.usercenterbackend.model.User;
import com.yupi.usercenterbackend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceImplTest {

    @Resource
    private UserService userService;

    @Test
    public void testSearchUsersByTags(){
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assertions.assertNotNull(userList);
    }

}