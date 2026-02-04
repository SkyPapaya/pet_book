package com.skypapaya.mapper;

import com.skypapaya.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public User selectById(@Param("userId") Long userId);

}
