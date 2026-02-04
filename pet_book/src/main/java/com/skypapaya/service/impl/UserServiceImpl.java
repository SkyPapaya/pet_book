package com.skypapaya.service.impl;

import com.skypapaya.mapper.FollowMapper;
import com.skypapaya.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {
    @Autowired
    FollowMapper followMapper;
    @Override
    public boolean toggleFollow(Long followerId, Long followeeId) {
        //不允许关注自己
        if (followerId.equals(followeeId)) {
            throw new RuntimeException("不能关注自己");
        }
       //之前没有关注
        if(followMapper.checkFollow(followerId,followeeId) == 0){
            followMapper.insertFollow(followerId,followeeId);
            return false;
        }else{
            followMapper.deleteFollow(followerId,followeeId);
            return true;
        }

    }
}
