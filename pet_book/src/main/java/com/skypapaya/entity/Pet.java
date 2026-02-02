package com.skypapaya.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
// 删掉 @TableName, @TableId 等 MyBatis-Plus 的注解
public class Pet {
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