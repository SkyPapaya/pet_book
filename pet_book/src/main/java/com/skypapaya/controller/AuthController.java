package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.entity.User;
import com.skypapaya.mapper.UserMapper;
import com.skypapaya.security.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest req) {
        if (req.getAccountId() == null || req.getPassword() == null) {
            return Result.error(400, "账号和密码不能为空");
        }
        User user = userMapper.selectByAccountId(req.getAccountId());
        if (user == null) {
            return Result.error(401, "账号不存在");
        }
        // 简单起见先用明文比对，实际建议改为 BCrypt 等加密
        if (!req.getPassword().equals(user.getPassword())) {
            return Result.error(401, "密码错误");
        }
        String token = JwtUtil.generateToken(user.getId());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setNickname(user.getNickname());
        resp.setAvatar(user.getAvatar());
        return Result.success(resp);
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest req) {
        if (req.getAccountId() == null || req.getPassword() == null) {
            return Result.error(400, "账号和密码不能为空");
        }
        User exists = userMapper.selectByAccountId(req.getAccountId());
        if (exists != null) {
            return Result.error(400, "账号已存在");
        }
        User user = new User();
        user.setAccountId(req.getAccountId());
        user.setPassword(req.getPassword()); // 简单示例，建议后续改为加密存储
        user.setNickname(req.getNickname() != null ? req.getNickname() : "新用户");
        userMapper.insertUser(user);

        String token = JwtUtil.generateToken(user.getId());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setNickname(user.getNickname());
        resp.setAvatar(user.getAvatar());
        return Result.success(resp);
    }

    @Data
    public static class LoginRequest {
        private Long accountId;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private Long userId;
        private String nickname;
        private String avatar;
    }

    @Data
    public static class RegisterRequest {
        private Long accountId;
        private String password;
        private String nickname;
    }
}

