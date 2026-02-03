package com.skypapaya.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Post {
    private Long id;
    private Long userId;
    /** 频道：adopt | knowledge | help，与前端、数据库统一用字符串 */
    private String channel;
    private String title;
    private String content;
    private List<String> images; // 需要配合 TypeHandler
    private Integer status;
    private boolean isPublic;    // 是否公开
    private String locationTag;  // 新增
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}