package com.skypapaya.entity;

import lombok.Data;

import java.time.LocalDateTime;
/*
记录了互动的类型，以供以后查询
 */
@Data
public class Interaction {
    private Long id;
    private Long userId;
    private Integer targetType;//1 帖子 2 评论
    private Long targetId;  // 帖子ID或评论ID，与 post.id 类型一致
    private Integer type;//1 喜欢 2 收藏
    private LocalDateTime createTime;
}
