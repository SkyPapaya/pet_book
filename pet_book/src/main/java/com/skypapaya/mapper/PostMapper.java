package com.skypapaya.mapper;

import com.skypapaya.vo.PostCardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostMapper {
    // 注意：这里返回的是 VO，不是 Entity
    List<PostCardVO> selectFeedList(@Param("category") Integer category,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);
}