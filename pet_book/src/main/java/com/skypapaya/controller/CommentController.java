package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.dto.PublishCommentDTO;
import com.skypapaya.dto.PublishPostDTO;
import com.skypapaya.security.CurrentUserHolder;
import com.skypapaya.service.CommentService;
import com.skypapaya.service.PostService;
import com.skypapaya.vo.CommentVO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {
    @Autowired
    private CommentService commentService;

    // 获取评论列表（保留给内部或调试使用，推荐前端走 /api/post/{id}/comments）
    @GetMapping("/{id}")
    public Result<List<CommentVO>> getComments(@PathVariable Long id) {
        return Result.success(commentService.getComments(id, 1, 100));
    }

    @PostMapping("/{id}/publish")
    public Result<String> addComment(@PathVariable Long id, @RequestBody PublishCommentDTO dto) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        commentService.addComment(currentUserId, id, dto.getContent(), dto.getParentId());
        return Result.success("评论成功");
    }

    /**
     * 评论点赞/取消点赞（内部或调试使用，推荐前端未来接 /api/comment/{id}/like）
     */
    @PostMapping("/{id}/like")
    public Result<Boolean> likeComment(@PathVariable Long id) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        boolean liked = commentService.toggleLikeForComment(currentUserId, id);
        return Result.success(liked);
    }
}
