package com.skypapaya.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    // 关注/取关
    boolean toggleFollow(Long followerId, Long followeeId);

}
