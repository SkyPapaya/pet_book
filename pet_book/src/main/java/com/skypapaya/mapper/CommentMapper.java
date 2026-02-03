package com.skypapaya.mapper;

import com.skypapaya.entity.Comment;
import com.skypapaya.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    //插入帖子
    int insertComment(CommentVO comment);

    //差某个帖子的所有评论
    List<CommentVO> selectByPostId(@Param("postId") Long postId);


}
