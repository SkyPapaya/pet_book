package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.service.UserService;
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
}
