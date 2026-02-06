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

    List<PostCardVO> selectSearch(@Param("keywordEscaped") String keywordEscaped,
                                  @Param("keywordRaw") String keywordRaw,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);

    PostDetailVO selectPostDetail(@Param("id") Long id);

    int insertPost(Post post);

    // 某用户发布的帖子
    List<PostCardVO> selectUserPosts(@Param("userId") Long userId,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    // 某用户收藏的帖子
    List<PostCardVO> selectUserCollects(@Param("userId") Long userId,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    // 某用户点赞的帖子
    List<PostCardVO> selectUserLikes(@Param("userId") Long userId,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

}

