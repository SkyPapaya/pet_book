package com.skypapaya.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserProfileVO {
    private Long id;
    private Long accountId;
    private String nickname;
    private String avatar;
    /** 简介/签名，与前端 signature 对应 */
    private String signature;
    private String desc; // 兼容旧字段，与 signature 一致

    private Integer followingCount;
    private Integer followersCount;
    private Integer likesAndCollectCount;

    private Integer gender; // 0未知 1男 2女
    private Integer age;
    private String location;
    private String profession;
    private String ip; // 最后登录 IP 属地
    private LocalDate birthday;
    private List<PetVO> pets;
}
