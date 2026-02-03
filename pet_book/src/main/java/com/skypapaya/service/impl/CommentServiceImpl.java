package com.skypapaya.service.impl;

import com.skypapaya.mapper.CommentMapper;
import com.skypapaya.service.CommentService;
import com.skypapaya.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    //发表评论
    @Override
    public void addComment(Long userId, Long postId, String content, Long parentId) {

        CommentVO c = new CommentVO();
        c.setUserId(userId);
        c.setPostId(postId);
        c.setContent(content);
        c.setParentId(parentId);
        commentMapper.insertComment(c);
    }

    //获取所有评论，并转换为树状结构
    @Override
    public List<CommentVO> getComments(Long postId) {
        //查出所有评论
        List<CommentVO> all = commentMapper.selectByPostId(postId);
        //组装成树
        List<CommentVO> rootComments = new ArrayList<>();
        Map<Long, CommentVO> map = new HashMap<>();
        for (CommentVO vo : all) {
            //创建子树
            vo.setReplies(new ArrayList<>());
            map.put(vo.getId(), vo);
        }
        for (CommentVO vo : all) {
            //没有父级评论
            if (vo.getParentId() == null) {
                rootComments.add(vo);
            } else {
                // 找到父评论，把当前评论加进去
                CommentVO parent = map.get(vo.getParentId());
                if (parent != null) {
                    parent.getReplies().add(vo);
                }
            }
        }
        return rootComments;

    }
}
