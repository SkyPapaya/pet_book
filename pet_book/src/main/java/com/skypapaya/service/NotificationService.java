package com.skypapaya.service;

import com.skypapaya.vo.NotificationVO;

import java.util.List;

public interface NotificationService {

    /**
     * 获取当前用户的通知列表
     *
     * @param userId 当前用户
     * @param type   comment | like | follow | null(表示全部，先简单按 comment 处理)
     * @param page   页码
     * @param size   每页数量
     */
    List<NotificationVO> getNotifications(Long userId, String type, Integer page, Integer size);
}

