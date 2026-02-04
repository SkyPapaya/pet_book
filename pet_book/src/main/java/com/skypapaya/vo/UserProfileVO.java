package com.skypapaya.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserProfileVO {
    private Long id;
    private Long accountId;//小红书id
    private String nickname;
    private String avatar; //头像
    private String desc;//简介

    private Integer followingCount;//关注人数
    private Integer followersCount;//粉丝人数
    private Integer likesAndCollectCount; // 获赞与收藏

    private Integer gender;//性别
    private LocalDate birthday;//生日
    private List<PetVO> pets;//宠物列表

}
