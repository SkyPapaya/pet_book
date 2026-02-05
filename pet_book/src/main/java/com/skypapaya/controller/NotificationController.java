package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.security.CurrentUserHolder;
import com.skypapaya.service.NotificationService;
import com.skypapaya.vo.NotificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 消息通知列表
     * GET /api/notification?type=comment|like|follow&page=&size=
     */
    @GetMapping
    public Result<List<NotificationVO>> getNotifications(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        List<NotificationVO> list = notificationService.getNotifications(currentUserId, type, page, size);
        return Result.success(list);
    }
}

