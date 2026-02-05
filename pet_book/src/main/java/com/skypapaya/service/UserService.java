package com.skypapaya.service;

import com.skypapaya.entity.User;
import com.skypapaya.vo.UserProfileVO;

public interface UserService {
    // 关注/取关
    boolean toggleFollow(Long followerId, Long followeeId);
    User selectById(Long id);
    UserProfileVO getProfile(Long userId);

    void updateProfile(Long userId, String nickname, String avatar, String signature,
                      Integer gender, Integer age, String location, String profession);
}
