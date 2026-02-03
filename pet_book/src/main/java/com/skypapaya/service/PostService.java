package com.skypapaya.service;

import com.skypapaya.dto.PublishPostDTO;
import com.skypapaya.vo.PostCardVO;
import com.skypapaya.vo.PostDetailVO;

import java.util.List;

public interface PostService {
    /**
     * 获取主页瀑布流列表
     *
     * @param channel 前端频道：all | adopt | knowledge | help，空或 all 表示全部
     * @param page    页码 (1, 2, 3...)
     * @param size    每页数量
     * @return 帖子卡片列表
     */
    List<PostCardVO> getFeedList(String channel, Integer page, Integer size);

    PostDetailVO getPostDetail(Long id);

    /**
     * 切换点赞状态 (点赞/取消赞)
     *
     * @param userId 操作用户ID
     * @param postId 帖子ID
     * @return 当前是否点赞 (true=已赞, false=未赞)
     */
    boolean toggleLike(Long userId, Long postId);

    /**
     * 切换收藏状态
     *
     * @param userId 操作用户ID
     * @param postId 帖子ID
     * @return 当前是否收藏(true = 已收藏 ， false= 未收藏)
     */
    boolean toggleCollect(Long userId, Long postId);

    PostCardVO publishPost(Long userId, PublishPostDTO publishPostDTO);


}