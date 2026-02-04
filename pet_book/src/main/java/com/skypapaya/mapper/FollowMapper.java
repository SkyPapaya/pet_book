package com.skypapaya.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FollowMapper {
    //检查是否已经关注
    int checkFollow(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    //关注
    int insertFollow(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    //取消关注
    int deleteFollow(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    //统计关注人数
    int countFollowee(@Param("userId") Long userId);

    //统计粉丝数量
    int countFollower(@Param("followerId") Long userId);


}
