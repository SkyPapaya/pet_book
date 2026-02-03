package com.skypapaya.service;

import com.skypapaya.vo.CommentVO;

import java.util.List;

public interface CommentService {
    public List<CommentVO> getComments(Long postId);
    public void addComment(Long userId, Long postId, String content, Long parentId);
}
