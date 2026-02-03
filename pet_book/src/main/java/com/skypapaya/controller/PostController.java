package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.dto.PublishPostDTO;
import com.skypapaya.service.PostService;
import com.skypapaya.vo.PostCardVO;
import com.skypapaya.vo.PostDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取主页瀑布流列表（与前端 HomeView 对齐）
     * GET /api/post/feed?channel=all|adopt|knowledge|help&page=1&size=16
     */
    ///需要改一下？
    @GetMapping("/feed")
    public Result<List<PostCardVO>> getFeedList(
            @RequestParam(required = false) String channel,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "16") Integer size) {

        List<PostCardVO> list = postService.getFeedList(channel, page, size);
        return Result.success(list);
    }

    /**
     * 帖子详情（PostDetailModal 按 id 拉取）
     * GET /api/post/{id}
     */
    @GetMapping("/{id}")
    public Result<PostDetailVO> getPostDetail(@PathVariable Long id) {
        PostDetailVO vo = postService.getPostDetail(id);
        return Result.success(vo);
    }

    /**
     * 点赞/取消点赞
     * POST /api/post/{id}/like
     */
    @PostMapping("/{id}/like")
    public Result<Boolean> likePost(@PathVariable Long id) {
        // TODO: 后续接入登录后，从 Token 获取 userId
        Long currentUserId = 1L;
        boolean isLiked = postService.toggleLike(currentUserId, id);
        // 返回最新的状态给前端，前端可以根据这个变红心/变灰心
        return Result.success(isLiked);
    }

    @PostMapping("/{id}/collect")
    public Result<Boolean> collectPost(@PathVariable Long id) {
        Long currentUserId = 1L;
        boolean isCollect = postService.toggleCollect(currentUserId, id);
        return Result.success(isCollect);
    }

    @PostMapping
    public Result<PostCardVO> publish(@RequestBody PublishPostDTO dto) {
        Long currentUserId = 1L;
        PostCardVO vo = postService.publishPost(currentUserId, dto);
        return Result.success(vo);
    }
}