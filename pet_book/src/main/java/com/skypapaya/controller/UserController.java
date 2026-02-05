package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.entity.User;
import com.skypapaya.security.CurrentUserHolder;
import com.skypapaya.service.PostService;
import com.skypapaya.service.UserService;
import com.skypapaya.vo.PostCardVO;
import com.skypapaya.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    /**
     * 关注/取关
     * POST /user/{id}/follow
     */
    @PostMapping("/{id}/follow")
    public Result<Boolean> follow(@PathVariable Long id) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        boolean isFollowed = userService.toggleFollow(currentUserId, id);
        return Result.success(isFollowed);
    }

    // 获取单个用户实体（调试用）
    @GetMapping("/{id}")
    public Result<User> selectById(@PathVariable Long id) {
        User user = userService.selectById(id);
        return Result.success(user);
    }

    /**
     * 获取当前用户个人资料
     * GET /user/profile
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getMyProfile() {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(userService.getProfile(currentUserId));
    }

    /**
     * 获取他人资料
     * GET /user/{id}/profile
     */
    @GetMapping("/{id}/profile")
    public Result<UserProfileVO> getUserProfile(@PathVariable Long id) {
        return Result.success(userService.getProfile(id));
    }

    /**
     * 个人页：用户发布的帖子列表
     * GET /api/user/{id}/posts?page=&size=
     */
    @GetMapping("/{id}/posts")
    public Result<List<PostCardVO>> getUserPosts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "16") Integer size) {
        return Result.success(postService.getUserPosts(id, page, size));
    }

    /**
     * 个人页：用户收藏的帖子列表
     * GET /api/user/{id}/collects?page=&size=
     */
    @GetMapping("/{id}/collects")
    public Result<List<PostCardVO>> getUserCollects(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "16") Integer size) {
        return Result.success(postService.getUserCollects(id, page, size));
    }

    /**
     * 个人页：用户点赞的帖子列表
     * GET /api/user/{id}/likes?page=&size=
     */
    @GetMapping("/{id}/likes")
    public Result<List<PostCardVO>> getUserLikes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "16") Integer size) {
        return Result.success(postService.getUserLikes(id, page, size));
    }
}
