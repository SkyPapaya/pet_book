package com.skypapaya.vo;
import lombok.Data;

@Data
public class PostCardVO {
    //帖子的基础信息
    private Long id;  // 帖子ID，与前端 PostCard.id (number) 一致
    private String title;//帖子的标题
    private String coverUrl;//帖子的封面图
    private Double coverAspectRatio; // 图片宽高比 (可选，暂留空)
    private String desc;             // 简短描述 (取 content 前几十个字)

    //作者的基础信息（关联查询user表）
    private Long authorId;
    private String authorName;
    private String authorAvatar;//作者头像

    //辅助信息
    private Integer likeCount;
    private Boolean isLiked;

    /** 频道：adopt | knowledge | help，与数据库、前端统一用字符串 */
    private String channel;
}
