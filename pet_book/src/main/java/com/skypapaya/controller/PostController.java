package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.dto.PublishCommentDTO;
import com.skypapaya.dto.PublishPostDTO;
import com.skypapaya.security.CurrentUserHolder;
import com.skypapaya.service.CommentService;
import com.skypapaya.service.PostService;
import com.skypapaya.vo.CommentVO;
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
    @Autowired
    private CommentService commentService;

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
     * 获取某个帖子的评论列表（对根评论分页）
     * GET /api/post/{id}/comments?page=&size=
     */
    @GetMapping("/{id}/comments")
    public Result<List<CommentVO>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commentService.getComments(id, page, size));
    }

    /**
     * 发表评论 / 回复
     * POST /api/post/{id}/comments
     */
    @PostMapping("/{id}/comments")
    public Result<String> addComment(@PathVariable Long id, @RequestBody PublishCommentDTO dto) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        commentService.addComment(currentUserId, id, dto.getContent(), dto.getParentId());
        return Result.success("评论成功");
    }

    /**
     * 点赞/取消点赞
     * POST /api/post/{id}/like
     */
    @PostMapping("/{id}/like")
    public Result<Boolean> likePost(@PathVariable Long id) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        boolean isLiked = postService.toggleLike(currentUserId, id);
        // 返回最新的状态给前端，前端可以根据这个变红心/变灰心
        return Result.success(isLiked);
    }

    @PostMapping("/{id}/collect")
    public Result<Boolean> collectPost(@PathVariable Long id) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        boolean isCollect = postService.toggleCollect(currentUserId, id);
        return Result.success(isCollect);
    }

    @PostMapping
    public Result<PostCardVO> publish(@RequestBody PublishPostDTO dto) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        PostCardVO vo = postService.publishPost(currentUserId, dto);
        return Result.success(vo);
    }
}