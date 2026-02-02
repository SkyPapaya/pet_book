package com.skypapaya.service;

import com.skypapaya.vo.PostCardVO;
import java.util.List;

public interface PostService {
    /**
     * 获取主页瀑布流列表
     * @param category 频道ID (可为空)
     * @param page 页码 (1, 2, 3...)
     * @param pageSize 每页数量
     * @return 帖子卡片列表
     */
    List<PostCardVO> getFeedList(Integer category, Integer page, Integer pageSize);
}