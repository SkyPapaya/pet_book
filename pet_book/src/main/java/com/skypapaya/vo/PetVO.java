package com.skypapaya.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PetVO {
    private Long id;
    private Long userId;
    private String name;
    private String avatar;
    private String species;
    private String breed;
    private Integer gender; // 0未知 1公 2母
    private LocalDate birthday;
    private String health;
    private Integer neutered; // 0否 1是
    private Integer status;
    private LocalDateTime createTime;
}
