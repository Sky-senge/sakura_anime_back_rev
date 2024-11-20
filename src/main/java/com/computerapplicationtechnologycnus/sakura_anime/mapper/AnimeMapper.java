package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.Anime;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnimeMapper {

    //普通插入
    @Insert("INSERT INTO anime (name, tags, description, rating, release_date, file_path) " +
            "VALUES (#{name}, #{tags}, #{description}, #{rating}, #{releaseDate}, #{filePath})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertAnime(Anime anime);

    // 查询缺失的最小 ID
    @Select("SELECT MIN(t1.id + 1) AS missing_id " +
            "FROM anime t1 " +
            "LEFT JOIN anime t2 ON t1.id + 1 = t2.id " +
            "WHERE t2.id IS NULL")
    Long findMissingId();

    // 手动指定 ID 新增动漫，不插入日期信息，默认由SQL生成
    @Insert("INSERT INTO anime (id, name, tags, description, rating, file_path) " +
            "VALUES (#{id}, #{name}, #{tags}, #{description}, #{rating}, #{filePath})")
    void insertAnimeWithId(Anime anime);

    // 手动指定 ID 插入动漫，包括日期（如有需要用于手动修正日期）
    @Insert("INSERT INTO anime (id, name, tags, description, rating, release_date, file_path) " +
            "VALUES (#{id}, #{name}, #{tags}, #{description}, #{rating}, #{releaseDate}, #{filePath})")
    void insertAnimeWithIdIncludeDate(Anime anime);

    @Select("SELECT * FROM anime WHERE id = #{id}")
    @Results({  //沙雕MyBaties，连个映射都做不好，还要我手操，杂鱼！
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "description", column = "description"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "releaseDate", column = "release_date"),
            @Result(property = "filePath", column = "file_path")
    })
    Anime findAnimeById(Long id);

    @Select("SELECT * FROM anime")
    @Results({  //沙雕MyBaties，连个映射都做不好，还要我手操，杂鱼！
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "description", column = "description"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "releaseDate", column = "release_date"),
            @Result(property = "filePath", column = "file_path")
    })
    List<Anime> findAllAnimes();

    @Update("UPDATE anime SET name = #{name}, tags = #{tags}, description = #{description}, " +
            "rating = #{rating}, release_date = #{releaseDate}, file_path = #{filePath} WHERE id = #{id}")
    void updateAnime(Anime anime); //直接提交一个完整的Anime类即可，因为类中包括ID

    @Delete("DELETE FROM anime WHERE id = #{id}")
    void deleteAnimeById(Long id);
}
