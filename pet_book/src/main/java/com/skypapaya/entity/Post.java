package com.skypapaya.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Post {
    private Long id;           // 帖子ID
    private Long userId;       // 发布者ID
    private Integer category;  // 频道：1-领养, 2-知识, 3-求助 [cite: 9]
    private String title;      // 标题
    private String content;    // 简介/正文

    // 这里注意：数据库存的是 JSON 字符串，Java 里我们直接用 List
    private List<String> images; // 图片列表

    private Integer status;    // 状态：0-审核中, 1-已发布, 2-未通过 [cite: 18]
    private LocalDateTime createTime;
}