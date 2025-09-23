package com.bangvan;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@EnableAsync
public class VsvShopApplication {

    @Value("${environment.info}")
    private String environmentInfo;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(VsvShopApplication.class, args);
    }
    @PostConstruct
    public void printEnvironmentInfo() {
        log.info("environmentInfo: " + environmentInfo);
    }

}
//4:25:07