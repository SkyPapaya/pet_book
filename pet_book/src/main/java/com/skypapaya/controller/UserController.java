package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.entity.User;
import com.skypapaya.service.UserService;
import com.skypapaya.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 关注/取关
     * POST /user/{id}/follow
     */
    @PostMapping("/{id}/follow")
    public Result<Boolean> follow(@PathVariable Long id) {
        Long currentUserId = 1L; // 暂写死
        boolean isFollowed = userService.toggleFollow(currentUserId, id);
        return Result.success(isFollowed);
    }

    @GetMapping("/{id}")
    private Result<User> selectById(@PathVariable Long id) {
        Long userId = 1L;
        User user = userService.selectById(userId);
        return Result.success(user);
    }

    /**
     * 获取当前用户个人资料
     * GET /user/profile
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getMyProfile() {
        Long currentUserId = 1L; // 暂写死
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
}
