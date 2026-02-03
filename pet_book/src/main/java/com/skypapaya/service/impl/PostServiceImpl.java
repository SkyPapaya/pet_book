package com.skypapaya.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypapaya.dto.PublishPostDTO;
import com.skypapaya.entity.Interaction;
import com.skypapaya.entity.Post;
import com.skypapaya.mapper.CommentMapper;
import com.skypapaya.mapper.InteractionMapper;
import com.skypapaya.mapper.PostMapper;
import com.skypapaya.service.PostService;
import com.skypapaya.vo.PostCardVO;
import com.skypapaya.vo.PostDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private InteractionMapper interactionMapper;
    @Autowired
    private CommentMapper commentMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<PostCardVO> getFeedList(String channel, Integer page, Integer size) {
        // channel 全链路为字符串：all / adopt / knowledge / help，直接传给 Mapper
        String filterChannel = (channel != null && !channel.isEmpty() && !"all".equalsIgnoreCase(channel)) ? channel : null;
        int offset = (page - 1) * size;
        List<PostCardVO> list = postMapper.selectFeedList(filterChannel, offset, size);

        for (PostCardVO vo : list) {
            String imagesJson = vo.getCoverUrl();
            vo.setCoverUrl(extractFirstImage(imagesJson));
        }
        return list;
    }

    /**
     * 辅助方法：从 JSON 数组字符串中提取第一张图片
     */
    private String extractFirstImage(String json) {
        if (json == null || json.isEmpty()) {
            return null; // 或者返回一个默认的占位图 URL
        }
        try {
            // 将 JSON 字符串解析为 List<String>
            List<String> images = objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
            if (images != null && !images.isEmpty()) {
                return images.get(0); // 取第一张
            }
        } catch (Exception e) {
            // 解析失败（比如数据脏了），打印日志或忽略
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PostDetailVO getPostDetail(Long id) {
        PostDetailVO vo = postMapper.selectPostDetail(id);
        if (vo == null) return null;

        if (vo.getCommentCount() == null) {
            vo.setCommentCount(0);
        }
        Long currentUserId = 1L; // TODO: 从 Token 获取
        vo.setIsLiked(interactionMapper.checkStatus(currentUserId, id, 1, 1) > 0);
        vo.setIsCollected(interactionMapper.checkStatus(currentUserId, id, 2, 1) > 0);
        vo.setIsFollowed(false); // TODO: 查 follow 表
        return vo;
    }

    //切换是否喜欢
    @Override
    public boolean toggleLike(Long userId, Long postId) {
        int targetType = 1; // 帖子
        int type = 1;       // 点赞
        int count = interactionMapper.checkStatus(userId, postId, type, targetType);
        if (count > 0) {
            interactionMapper.deleteInteraction(userId, postId, type, targetType);
            return false;
        }
        Interaction interaction = new Interaction();
        interaction.setUserId(userId);
        interaction.setTargetId(postId);
        interaction.setTargetType(targetType);
        interaction.setType(type);
        interactionMapper.insertInteraction(interaction);
        return true;
    }

    //切换是否收藏
    @Override
    public boolean toggleCollect(Long userId, Long postId) {
        int targetType = 1;
        int type = 2;
        int count = interactionMapper.checkStatus(userId, postId, type, targetType);
        if (count > 0) {
            interactionMapper.deleteInteraction(userId, postId, type, targetType);
            //之前是收藏状态，现在又点了一下，变成未收藏状态
            return false;
        }
        Interaction interaction = new Interaction();
        interaction.setUserId(userId);
        interaction.setTargetId(postId);
        interaction.setTargetType(targetType);
        interaction.setType(type);
        interactionMapper.insertInteraction(interaction);
        return true;
    }

    //发布帖子
    @Override
    public PostCardVO publishPost(Long userId, PublishPostDTO publishPostDTO) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(publishPostDTO.getTitle());
        post.setContent(publishPostDTO.getContent());
        post.setChannel(publishPostDTO.getChannel());
        post.setImages(publishPostDTO.getImages());
        post.setPublic(publishPostDTO.isPublic());
        //插入前端传来的帖子，该步插入数据库
        postMapper.insertPost(post);

        //返回一个简单的视图给前端，这部将数据返回给前端
        PostCardVO vo = new PostCardVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setCoverUrl(publishPostDTO.getImages().isEmpty() ? null : publishPostDTO.getImages().get(0));
        return vo;

    }


}





