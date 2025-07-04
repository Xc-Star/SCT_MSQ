package com.sct;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@EnableCaching // 开启注解缓存功能
@EnableScheduling // 开启定时任务功能
@Slf4j
@MapperScan("com.sct.mapper")
public class SctApplication {
    public static void main(String[] args) {
        SpringApplication.run(SctApplication.class, args);
        log.info("server started");
    }
}
