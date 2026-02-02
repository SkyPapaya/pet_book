package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.service.PostService;
import com.skypapaya.vo.PostCardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取主页瀑布流列表
     * URL: /post/feed?category=1&page=1&pageSize=10
     */
    @GetMapping("/feed")
    public Result<List<PostCardVO>> getFeedList(
            @RequestParam(required = false) Integer category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        List<PostCardVO> list = postService.getFeedList(category, page, pageSize);
        return Result.success(list);
    }
}