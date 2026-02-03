package com.skypapaya.dto;

import lombok.Data;

import java.util.List;

//发布帖子模块接收的数据
@Data
public class PublishPostDTO {
    private String title;
    private String content;
    private String channel;//帖子属于哪个频道
    private List<String> images;//帖子的一系列图片
    private boolean isPublic;
}
