package com.skypapaya.vo;
import lombok.Data;

@Data
public class PostCardVO {
    //帖子的基础信息
    private String id;//帖子的id
    private String title;//帖子的标题
    private String coverUrl;//帖子的封面图

    //作者的基础信息（关联查询user表）
    private Long authorId;
    private String authorName;
    private String authorAvatar;//作者头像

    //辅助信息
    private Integer likeCount;
    private Boolean isLiked;//后续实现



}
