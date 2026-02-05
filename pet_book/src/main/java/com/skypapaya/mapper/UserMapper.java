package com.skypapaya.mapper;

import com.skypapaya.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public User selectById(@Param("userId") Long userId);

    public User selectByAccountId(@Param("accountId") Long accountId);

    public int insertUser(User user);

}
