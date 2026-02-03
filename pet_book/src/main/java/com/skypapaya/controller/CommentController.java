package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.dto.PublishCommentDTO;
import com.skypapaya.dto.PublishPostDTO;
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

    // 获取评论列表
    @GetMapping("/{id}")
    public Result<List<CommentVO>> getComments(@PathVariable Long id) {
        return Result.success(commentService.getComments(id));
    }

    @PostMapping("/{id}/publish")
    public Result<String> addComment(@PathVariable Long id, @RequestBody PublishCommentDTO dto) {
        commentService.addComment(1L, id, dto.getContent(), dto.getParentId());
        return Result.success("评论成功");
    }
}
