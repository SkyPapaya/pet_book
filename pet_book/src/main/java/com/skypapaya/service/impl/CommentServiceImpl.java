package com.skypapaya.service.impl;

import com.skypapaya.entity.Interaction;
import com.skypapaya.mapper.CommentMapper;
import com.skypapaya.mapper.InteractionMapper;
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
    @Autowired
    InteractionMapper interactionMapper;

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

    //获取所有评论，并转换为树状结构（对根评论做简单分页）
    @Override
    public List<CommentVO> getComments(Long postId, Integer page, Integer size) {
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
        // 对根评论做简单分页
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int fromIndex = (p - 1) * s;
        if (fromIndex >= rootComments.size()) {
            return new ArrayList<>();
        }
        int toIndex = Math.min(fromIndex + s, rootComments.size());
        return rootComments.subList(fromIndex, toIndex);
    }

    @Override
    public boolean toggleLikeForComment(Long userId, Long commentId) {
        int targetType = 2; // 2 表示评论
        int type = 1;       // 1 表示点赞
        int count = interactionMapper.checkStatus(userId, commentId, type, targetType);
        if (count > 0) {
            interactionMapper.deleteInteraction(userId, commentId, type, targetType);
            return false;
        }
        Interaction interaction = new Interaction();
        interaction.setUserId(userId);
        interaction.setTargetId(commentId);
        interaction.setTargetType(targetType);
        interaction.setType(type);
        interactionMapper.insertInteraction(interaction);
        return true;
    }
}
