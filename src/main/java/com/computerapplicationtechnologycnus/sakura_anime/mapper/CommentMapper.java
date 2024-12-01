package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    //查询评论全表
    @Select("SELECT * from comments")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "createAt",column = "created_at"),
    })
    List<Comment> findAllComment();

    @Select("SELECT COUNT(*) FROM comments WHERE anime_id = #{animeId}")
    int countComment(@Param("animeId") Long animeId);

    //查询评论全表
    @Select("SELECT * from comments order by id asc limit #{size} OFFSET #{offset}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "createAt",column = "created_at"),
    })
    List<Comment> findCommentUseOffset(@Param("size") Long size, @Param("offset") Long offset);

    @Insert("INSERT INTO comments (anime_id, user_id, content) " +
            "VALUES (#{animeId}, #{userId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "createAt",column = "created_at"),
    })
    void insertComment(Comment comment);

    // 查询缺失的最小 ID
    @Select("SELECT MIN(t1.id + 1) AS missing_id " +
            "FROM comments t1 " +
            "LEFT JOIN comments t2 ON t1.id + 1 = t2.id " +
            "WHERE t2.id IS NULL")
    Long findMissingId();

    // 手动指定 ID 新增评论，不插入日期信息，默认由SQL生成
    @Insert("INSERT INTO comments (id, anime_id, user_id, content) " +
            "VALUES (#{id},#{animeId}, #{userId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "content",column = "content"),
    })
    void insertCommentWithId(Comment comment);

    @Select("SELECT * FROM comments WHERE id = #{id}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "createAt",column = "created_at"),
    })
    Comment findCommentById(Long id);

    @Select("SELECT * FROM comments WHERE anime_id = #{animeId} order by id asc limit #{size} OFFSET #{offset}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "createAt",column = "created_at"),
    })
    List<Comment> findCommentsByAnimeIdUseOffset(@Param("animeId") Long animeId,@Param("size") Long size, @Param("offset") Long offset);

    @Select("SELECT * FROM comments WHERE anime_id = #{animeId}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "createAt",column = "created_at"),
    })
    List<Comment> findCommentsByAnimeId(Long animeId);

    @Select("SELECT * FROM comments WHERE user_id = #{userId} limit #{size} OFFSET #{offset}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "createAt",column = "created_at"),
    })
    List<Comment> findCommentsByUserId(@Param("userId") Long userId,@Param("size") Long size, @Param("offset") Long offset);

    @Update("UPDATE comments SET anime_id = #{animeId}, user_id = #{userId}, content = #{content} " +
            "WHERE id = #{id}")
    void updateComment(Comment comment);

    @Delete("DELETE FROM comments WHERE id = #{id}")
    void deleteComment(Long id);

    // 删除指定用户的所有评论
    @Delete("DELETE FROM comments WHERE user_id = #{userId}")
    int deleteCommentsByUserId(@Param("userId") Long userId);

    // 删除指定动漫的所有评论
    @Delete("DELETE FROM comments WHERE anime_id = #{animeId}")
    int deleteCommentsByAnimeId(@Param("animeId") Long animeId);
}
