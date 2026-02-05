package com.skypapaya.mapper;

import com.skypapaya.vo.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    List<NotificationVO> selectCommentNotifications(@Param("userId") Long userId,
                                                    @Param("offset") Integer offset,
                                                    @Param("limit") Integer limit);

    List<NotificationVO> selectLikeNotifications(@Param("userId") Long userId,
                                                 @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);

    List<NotificationVO> selectFollowNotifications(@Param("userId") Long userId,
                                                   @Param("offset") Integer offset,
                                                   @Param("limit") Integer limit);
}

