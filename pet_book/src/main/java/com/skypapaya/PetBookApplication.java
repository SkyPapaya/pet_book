package com.skypapaya;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.skypapaya.mapper") // <--- 必须加这一行，告诉MyBatis去哪里找接口
public class PetBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetBookApplication.class, args);
    }

}