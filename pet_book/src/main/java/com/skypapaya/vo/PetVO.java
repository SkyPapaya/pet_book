package com.skypapaya.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PetVO {
    private Long id; // 对应数据库 id
    private Long userId;
    private String nickname;
    private Integer type;
    private Integer gender;
    private LocalDate birthday;
    private Integer status;
    private String avatar;
    private String bio;
    private LocalDateTime createTime;
}
