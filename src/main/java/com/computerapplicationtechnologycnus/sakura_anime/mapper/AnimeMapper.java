package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.Anime;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnimeMapper {

    @Insert("INSERT INTO anime (name, tags, description, rating, release_date, file_path) " +
            "VALUES (#{name}, #{tags}, #{description}, #{rating}, #{releaseDate}, #{filePath})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertAnime(Anime anime);

    @Select("SELECT * FROM anime WHERE id = #{id}")
    Anime selectAnimeById(Long id);

    @Select("SELECT * FROM anime")
    List<Anime> selectAllAnimes();

    @Update("UPDATE anime SET name = #{name}, tags = #{tags}, description = #{description}, " +
            "rating = #{rating}, release_date = #{releaseDate}, file_path = #{filePath} WHERE id = #{id}")
    void updateAnime(Anime anime);

    @Delete("DELETE FROM anime WHERE id = #{id}")
    void deleteAnime(Long id);
}
