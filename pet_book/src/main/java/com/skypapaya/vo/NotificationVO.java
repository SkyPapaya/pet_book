package com.skypapaya.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知列表项 VO：用于消息中心（评论、赞/收藏、新增关注）
 */
@Data
public class NotificationVO {
    private Long id;
    /**
     * 类型：comment | like | follow
     */
    private String type;

    private Long actorId;
    private String actorName;
    private String actorAvatar;

    private Long postId;
    private String postTitle;

    /**
     * 文本内容（如评论内容），部分类型可为空
     */
    private String content;

    private LocalDateTime createdAt;
}

