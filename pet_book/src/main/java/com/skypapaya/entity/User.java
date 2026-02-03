package com.skypapaya.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id; //用户id
    private String accountId; //账号id
    private String password; //密码
    private String nickname;//昵称
    private String avatar;//头像url
    private Integer gender;//0 未知 1 男 2女
    private Integer age;//年龄
    private String location;//地区
    private String profession;//职业
    private String signature;//简介
    private String lastLoginIp;//最后一次登录的ip
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间

}
