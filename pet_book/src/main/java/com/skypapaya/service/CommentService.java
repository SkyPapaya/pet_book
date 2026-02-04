package com.skypapaya.service;

import com.skypapaya.vo.CommentVO;

import java.util.List;

public interface CommentService {
    /**
     * 获取某个帖子的评论列表（树状），支持简单分页：对根评论分页，子评论全部带出
     */
    List<CommentVO> getComments(Long postId, Integer page, Integer size);

    /**
     * 发表评论或回复
     */
    void addComment(Long userId, Long postId, String content, Long parentId);

    /**
     * 点赞/取消点赞某条评论
     * @return 当前是否已点赞（true 已点赞，false 未点赞）
     */
    boolean toggleLikeForComment(Long userId, Long commentId);
}
