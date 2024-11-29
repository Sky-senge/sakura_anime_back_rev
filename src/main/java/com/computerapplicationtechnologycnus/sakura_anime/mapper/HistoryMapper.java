package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.History;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HistoryMapper {

    // 根据ID查询
    @Select("SELECT * FROM history WHERE id = #{id}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "episodes",column = "episodes"),
            @Result(property = "createAt",column = "created_at"),
    })
    History findById(@Param("id") Long id);

    // 根据UID查询
    @Select("SELECT * FROM history WHERE user_id = #{userId} limit #{size} OFFSET #{offset}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "episodes",column = "episodes"),
            @Result(property = "createAt",column = "created_at"),
    })
    List<History> findByUserId(@Param("userId") Long userId, @Param("size") Long size, @Param("offset") Long offset);

    // 根据动漫ID查询
    @Select("SELECT * FROM history WHERE anime_id = #{animeId} limit #{size} OFFSET #{offset}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "animeId",column = "anime_id"),
            @Result(property = "episodes",column = "episodes"),
            @Result(property = "createAt",column = "created_at"),
    })
    List<History> findByAnimeId(@Param("animeId") Long animeId, @Param("size") Long size, @Param("offset") Long offset);

    // 新增历史记录（不插入created_at字段）
    @Insert("INSERT INTO history (user_id, anime_id, episodes) VALUES (#{userId}, #{animeId}, #{episodes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertHistory(History history);

    // 根据动漫ID和UID统计记录数
    @Select("SELECT COUNT(*) FROM history WHERE anime_id = #{animeId} AND user_id = #{userId}")
    int countByAnimeIdAndUserId(@Param("animeId") Long animeId, @Param("userId") Long userId);

    // 查询缺失的最小 ID
    @Select("SELECT MIN(t1.id + 1) AS missing_id " +
            "FROM history t1 " +
            "LEFT JOIN history t2 ON t1.id + 1 = t2.id " +
            "WHERE t2.id IS NULL")
    Long findMissingId();
}
