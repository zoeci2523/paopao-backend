package com.yupi.usercenterbackend;

import com.yupi.usercenterbackend.mapper.UserMapper;
import com.yupi.usercenterbackend.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class) // 要注入才能启动spring，才可以识别mapper
public class SampleTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelect(){
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(1, userList.size());
        userList.forEach(System.out::println);
    }
}
