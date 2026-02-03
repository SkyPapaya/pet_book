package com.skypapaya.mapper;

import com.skypapaya.entity.Interaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InteractionMapper {
    // 查询是否有记录 (用于判断 isLiked)
    int checkStatus(@Param("userId") Long userId,
                    @Param("targetId") Long targetId,
                    @Param("type") Integer type,
                    @Param("targetType") Integer targetType);

    // 插入 (点赞)/收藏
    int insertInteraction(Interaction interaction);

    // 删除 (取消点赞)/收藏
    int deleteInteraction(@Param("userId") Long userId,
                          @Param("targetId") Long targetId,
                          @Param("type") Integer type,
                          @Param("targetType") Integer targetType);


}