package com.skypapaya.service.impl;

import com.skypapaya.mapper.NotificationMapper;
import com.skypapaya.service.NotificationService;
import com.skypapaya.vo.NotificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public List<NotificationVO> getNotifications(Long userId, String type, Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int offset = (p - 1) * s;

        if ("like".equalsIgnoreCase(type)) {
            return notificationMapper.selectLikeNotifications(userId, offset, s);
        } else if ("follow".equalsIgnoreCase(type)) {
            return notificationMapper.selectFollowNotifications(userId, offset, s);
        } else {
            // 默认 comment
            return notificationMapper.selectCommentNotifications(userId, offset, s);
        }
    }
}

