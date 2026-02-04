package com.skypapaya.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Follow {
    Long id;
    //粉丝id
    private Long followerId;
    //被关注者id
    private Long followeeId;
    private LocalDateTime createTime;
}
