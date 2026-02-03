package com.skypapaya.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private Long postId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private LocalDateTime createTime;
}
