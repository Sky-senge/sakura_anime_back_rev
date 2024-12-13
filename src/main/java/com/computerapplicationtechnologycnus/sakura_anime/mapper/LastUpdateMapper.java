package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.LastUpdate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LastUpdateMapper {
    /**
     * 获取 id = 1 的数据
     *
     * @return LastUpdate 实体
     */
    @Select("SELECT id, " +
            "video_lastUpdate AS videoLastUpdate, " +
            "user_lastUpdate AS userLastUpdate, " +
            "comment_lastUpdate AS commentLastUpdate " +
            "FROM lastupdate WHERE id = 1")
    LastUpdate getLastUpdate();

    /**
     * 更新 id = 1 的 video_lastUpdate
     *
     * @param videoLastUpdate 视频的更新时间
     * @return 更新影响的行数
     */
    @Update("UPDATE lastupdate SET video_lastUpdate = #{videoLastUpdate} WHERE id = 1")
    int updateVideoLastUpdate(@Param("videoLastUpdate") String videoLastUpdate);

    /**
     * 更新 id = 1 的 user_lastUpdate
     *
     * @param userLastUpdate 用户的更新时间
     * @return 更新影响的行数
     */
    @Update("UPDATE lastupdate SET user_lastUpdate = #{userLastUpdate} WHERE id = 1")
    int updateUserLastUpdate(@Param("userLastUpdate") String userLastUpdate);

    /**
     * 更新 id = 1 的 comment_lastUpdate
     *
     * @param commentLastUpdate 评论的更新时间
     * @return 更新影响的行数
     */
    @Update("UPDATE lastupdate SET comment_lastUpdate = #{commentLastUpdate} WHERE id = 1")
    int updateCommentLastUpdate(@Param("commentLastUpdate") String commentLastUpdate);
}
