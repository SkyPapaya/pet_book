package com.skypapaya.mapper;

import com.skypapaya.entity.Post;
import com.skypapaya.vo.PostCardVO;
import com.skypapaya.vo.PostDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostMapper {
    // 注意：这里返回的是 VO，不是 Entity
    List<PostCardVO> selectFeedList(@Param("channel") String channel,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    PostDetailVO selectPostDetail(@Param("id") Long id);

    int insertPost(Post post);

}

