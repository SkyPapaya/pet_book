package com.skypapaya.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailVO {
    private Long id;//帖子id
    private String title;//帖子标题
    private String desc; //正文内容
    private List<String> imageUrls;//所有图片，存放url

    //作者信息
    private Long authorId;
    private String authorName;
    private String authorAvatar;

    //统计数据
    private Integer likeCount;//点赞数
    private Integer collectCount;//收藏数
    private Integer commentCount;//评论数

    //当前用户状态 用于前端渲染
    private Boolean isLiked;
    private Boolean isCollected;
    private Boolean isFollowed;

    private LocalDateTime createdAt;
    private String publishIp;



}
