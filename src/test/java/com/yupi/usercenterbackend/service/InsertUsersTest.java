package com.yupi.usercenterbackend.service;

import com.yupi.usercenterbackend.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(20, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 单线程批量插入用户
     */
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeUser");
            user.setAvatarUrl("https://th.bing.com/th/id/OIP.OxPKyYDA8QWuucHZolWWYgHaHa?pid=ImgDet&rs=1");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("189");
            user.setEmail("189@163.com");
            user.setProfile("假人一个");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("654321");
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList, 5000);
        stopWatch.stop();
        System.out.println("Total time used: "+stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int batchSize = 3000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<User> userList = new ArrayList<>();
            while(true){
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("fakeUser");
                user.setAvatarUrl("https://th.bing.com/th/id/OIP.OxPKyYDA8QWuucHZolWWYgHaHa?pid=ImgDet&rs=1");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("189");
                user.setEmail("189@163.com");
                user.setProfile("假人一个");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("654321");
                user.setTags("[]");
                userList.add(user);
                if (j % batchSize == 0) break;
            }
            // 异步执行
            // 自定义一个线程池
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: "+ Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        }
        // 120s 30w 条
        stopWatch.stop();
        System.out.println("Total time used: "+stopWatch.getTotalTimeMillis());
    }

}

