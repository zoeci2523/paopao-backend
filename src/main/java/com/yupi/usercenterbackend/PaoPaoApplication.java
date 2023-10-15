package com.yupi.usercenterbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication(exclude = RedisAutoConfiguration.class)
@MapperScan("com.yupi.usercenterbackend.mapper")
public class PaoPaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaoPaoApplication.class, args);
    }

}
