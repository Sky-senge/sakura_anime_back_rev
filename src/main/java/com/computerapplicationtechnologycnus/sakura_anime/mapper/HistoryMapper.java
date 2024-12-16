package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.History;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.HistoryResponseModel;
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

    @Select("""
    SELECT 
        h.id,
        h.user_id,
        h.anime_id,
        h.episodes,
        h.created_at,
        u.display_name AS displayName,
        a.name AS animeName
    FROM 
        history h
    LEFT JOIN 
        users u ON h.user_id = u.id
    LEFT JOIN 
        anime a ON h.anime_id = a.id
    WHERE 
        h.anime_id = #{userId}
    LIMIT #{size} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "animeId", column = "anime_id"),
            @Result(property = "episodes", column = "episodes"),
            @Result(property = "createAt", column = "created_at"),
            @Result(property = "displayName", column = "displayName"),
            @Result(property = "animeName", column = "animeName"),
    })
    List<HistoryResponseModel> findByUserIdWithDetails(@Param("userId") Long userId, @Param("size") Long size, @Param("offset") Long offset);

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

    @Select("""
    SELECT 
        h.id,
        h.user_id,
        h.anime_id,
        h.episodes,
        h.created_at,
        u.display_name AS displayName,
        a.name AS animeName
    FROM 
        history h
    LEFT JOIN 
        users u ON h.user_id = u.id
    LEFT JOIN 
        anime a ON h.anime_id = a.id
    WHERE 
        h.anime_id = #{animeId}
    LIMIT #{size} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "animeId", column = "anime_id"),
            @Result(property = "episodes", column = "episodes"),
            @Result(property = "createAt", column = "created_at"),
            @Result(property = "displayName", column = "displayName"),
            @Result(property = "animeName", column = "animeName"),
    })
    List<HistoryResponseModel> findByAnimeIdWithDetails(@Param("animeId") Long animeId, @Param("size") Long size, @Param("offset") Long offset);


    // 新增历史记录（不插入created_at字段，让SQL自动生成）
    @Insert("INSERT INTO history (user_id, anime_id, episodes) VALUES (#{userId}, #{animeId}, #{episodes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertHistory(History history);

    // 新增历史记录（不插入created_at字段，让SQL自动生成）
    @Insert("INSERT INTO history (id, user_id, anime_id, episodes) VALUES (#{id}, #{userId}, #{animeId}, #{episodes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertHistoryWithId(History history);


    // 根据UID统计记录数
    @Select("SELECT COUNT(*) FROM history WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    //查询用户最老的历史记录
    @Select("SELECT id FROM history WHERE user_id = #{userId} ORDER BY created_at ASC LIMIT 1")
    Long findOldestHistoryIdByUserId(@Param("userId") Long userId);

    //根据ID删除记录
    @Delete("DELETE FROM history WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    // 查询缺失的最小 ID
    @Select("SELECT MIN(t1.id + 1) AS missing_id " +
            "FROM history t1 " +
            "LEFT JOIN history t2 ON t1.id + 1 = t2.id " +
            "WHERE t2.id IS NULL")
    Long findMissingId();
}
