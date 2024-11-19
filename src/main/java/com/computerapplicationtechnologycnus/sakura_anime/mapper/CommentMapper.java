package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO comment (anime_id, user_id, content, create_at) " +
            "VALUES (#{animeId}, #{userId}, #{content}, #{createAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertComment(Comment comment);

    @Select("SELECT * FROM comment WHERE id = #{id}")
    Comment selectCommentById(Long id);

    @Select("SELECT * FROM comment WHERE anime_id = #{animeId}")
    List<Comment> selectCommentsByAnimeId(Long animeId);

    @Select("SELECT * FROM comment WHERE user_id = #{userId}")
    List<Comment> selectCommentsByUserId(Long userId);

    @Update("UPDATE comment SET anime_id = #{animeId}, user_id = #{userId}, content = #{content}, " +
            "create_at = #{createAt} WHERE id = #{id}")
    void updateComment(Comment comment);

    @Delete("DELETE FROM comment WHERE id = #{id}")
    void deleteComment(Long id);

    // 删除指定用户的所有评论
    @Delete("DELETE FROM comments WHERE user_id = #{userId}")
    int deleteCommentsByUserId(@Param("userId") Long userId);
}
