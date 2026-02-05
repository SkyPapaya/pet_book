package com.skypapaya.service.impl;

import com.skypapaya.entity.User;
import com.skypapaya.mapper.FollowMapper;
import com.skypapaya.mapper.InteractionMapper;
import com.skypapaya.mapper.PetMapper;
import com.skypapaya.mapper.UserMapper;
import com.skypapaya.service.UserService;
import com.skypapaya.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    FollowMapper followMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    InteractionMapper interactionMapper;
    @Autowired
    PetMapper petMapper;

    @Override
    public boolean toggleFollow(Long followerId, Long followeeId) {
        //不允许关注自己
        if (followerId.equals(followeeId)) {
            throw new RuntimeException("不能关注自己");
        }
        //之前没有关注
        if (followMapper.checkFollow(followerId, followeeId) == 0) {
            followMapper.insertFollow(followerId, followeeId);
            return false;
        } else {
            followMapper.deleteFollow(followerId, followeeId);
            return true;
        }

    }

    @Override
    public User selectById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        // 1. 查基本信息
        User user = userMapper.selectById(userId);
        if (user == null) return null;

        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setAccountId(user.getAccountId());
        vo.setSignature(user.getSignature());
        vo.setDesc(user.getSignature());
        vo.setGender(user.getGender());
        vo.setAge(user.getAge());
        vo.setLocation(user.getLocation());
        vo.setProfession(user.getProfession());
        vo.setIp(user.getLastLoginIp());

        // 2. 查统计数据
        vo.setFollowingCount(followMapper.countFollowee(userId));
        vo.setFollowersCount(followMapper.countFollower(userId));
        // 获赞+收藏总数
        vo.setLikesAndCollectCount(interactionMapper.countUserReceivedInteract(userId));

        // 3. 查宠物
        vo.setPets(petMapper.selectByUserId(userId));

        return vo;
    }

    @Override
    public void updateProfile(Long userId, String nickname, String avatar, String signature,
                              Integer gender, Integer age, String location, String profession) {
        User user = userMapper.selectById(userId);
        if (user == null) return;
        if (nickname != null) user.setNickname(nickname);
        if (avatar != null) user.setAvatar(avatar);
        if (signature != null) user.setSignature(signature);
        if (gender != null) user.setGender(gender);
        if (age != null) user.setAge(age);
        if (location != null) user.setLocation(location);
        if (profession != null) user.setProfession(profession);
        userMapper.updateUser(user);
    }
}
