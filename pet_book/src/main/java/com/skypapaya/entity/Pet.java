package com.skypapaya.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Pet {
    private Long id; // 对应数据库 id
    private Long userId;
    private String name;
    private String avatar;
    private String species;
    private String breed;
    private Integer gender;//0未知，1.公2母
    private LocalDate birthday;
    private String health;
    private Integer neutered;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}