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

    /**
     * 对于首页的动漫列表查询
     *
     * @param limit 查询大小限制
     * @param offset 查询偏移
     * @return List<Anime>
     */
    @Select("""
    SELECT
        a.id,
        a.name,
        a.tags,
        a.description,
        a.rating,
        a.release_date,
        COALESCE(
            NULLIF(
                JSON_ARRAYAGG(
                    CASE 
                        WHEN jt.fileName IS NOT NULL 
                        THEN JSON_OBJECT('episodes', jt.episodes, 'fileName', jt.fileName)
                    END
                ),
                JSON_ARRAY(NULL)
            ), 
            JSON_ARRAY()
        ) AS file_path
    FROM 
        anime a
    LEFT JOIN 
        JSON_TABLE(
            a.file_path, 
            '$[*]'
            COLUMNS (
                episodes BIGINT PATH '$.episodes',
                fileName VARCHAR(255) PATH '$.fileName'
            )
        ) AS jt ON jt.episodes = 1
    GROUP BY 
        a.id, 
        a.name, 
        a.tags, 
        a.description, 
        a.rating, 
        a.release_date
    ORDER BY 
        a.id 
    LIMIT #{limit} OFFSET #{offset};
    """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "description", column = "description"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "releaseDate", column = "release_date"),
            @Result(property = "filePath", column = "file_path")
    })
    List<Anime> findAllAnimeWithIndexPageUseOffset(@Param("limit") Long limit, @Param("offset") Long offset);

    @Select("<script>"
            + "SELECT"
            + "    a.id,"
            + "    a.name,"
            + "    a.tags,"
            + "    a.description,"
            + "    a.rating,"
            + "    a.release_date,"
            + "    COALESCE("
            + "        NULLIF("
            + "            JSON_ARRAYAGG("
            + "                CASE "
            + "                    WHEN jt.fileName IS NOT NULL "
            + "                    THEN JSON_OBJECT('episodes', jt.episodes, 'fileName', jt.fileName)"
            + "                END"
            + "            ),"
            + "            JSON_ARRAY(NULL)"
            + "        ),"
            + "        JSON_ARRAY()"
            + "    ) AS file_path "
            + "FROM "
            + "anime a "
            + "LEFT JOIN"
            + "    JSON_TABLE("
            + "        a.file_path, "
            + "        '$[*]'"
            + "        COLUMNS ("
            + "            episodes BIGINT PATH '$.episodes',"
            + "            fileName VARCHAR(255) PATH '$.fileName'"
            + "        )"
            + "    ) AS jt ON jt.episodes = 1 "
            + "WHERE" //MyBaties动态查询
            + "    <foreach collection='tags' item='tag' open='(' separator=' AND ' close=')'>"
            + "        JSON_CONTAINS(a.tags, JSON_ARRAY(#{tag})) "
            + "    </foreach>"
            + "GROUP BY"
            + "    a.id,"
            + "    a.name,"
            + "    a.tags,"
            + "    a.description,"
            + "    a.rating,"
            + "    a.release_date "
            + "ORDER BY a.id "
            + "LIMIT #{limit} OFFSET #{offset}"
            + "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "description", column = "description"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "releaseDate", column = "release_date"),
            @Result(property = "filePath", column = "file_path")
    })
    List<Anime> findAnimeWithIndexPageByTags(@Param("tags") List<String> tags,@Param("limit") Long limit, @Param("offset") Long offset);


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

//    @Select("SELECT * FROM anime WHERE name LIKE CONCAT('%', #{name}, '%') LIMIT #{limit} OFFSET #{offset}")
@Select("""
    SELECT
        a.id,
        a.name,
        a.tags,
        a.description,
        a.rating,
        a.release_date,
        COALESCE(
            NULLIF(
                JSON_ARRAYAGG(
                    CASE 
                        WHEN jt.fileName IS NOT NULL 
                        THEN JSON_OBJECT('episodes', jt.episodes, 'fileName', jt.fileName)
                    END
                ),
                JSON_ARRAY(NULL)
            ), 
            JSON_ARRAY()
        ) AS file_path
    FROM 
        anime a
    LEFT JOIN 
        JSON_TABLE(
            a.file_path, 
            '$[*]'
            COLUMNS (
                episodes BIGINT PATH '$.episodes',
                fileName VARCHAR(255) PATH '$.fileName'
            )
        ) AS jt ON jt.episodes = 1 
    WHERE name LIKE CONCAT('%',#{name},'%') 
    GROUP BY 
        a.id, 
        a.name, 
        a.tags, 
        a.description, 
        a.rating, 
        a.release_date
    ORDER BY 
        a.id 
    LIMIT #{limit} OFFSET #{offset};
    """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "description", column = "description"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "releaseDate", column = "release_date"),
            @Result(property = "filePath", column = "file_path")
    })
    List<Anime> searchAnimeByNameUseOffset(@Param("name") String name, @Param("limit") Long limit, @Param("offset") Long offset);

    @Select("select * from anime order by id asc limit #{size} OFFSET #{offset}")
    @Results({  //沙雕MyBaties，连个映射都做不好，还要我手操，杂鱼！
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "description", column = "description"),
            @Result(property = "rating", column = "rating"),
            @Result(property = "releaseDate", column = "release_date"),
            @Result(property = "filePath", column = "file_path")
    })
    List<Anime> findAnimeUseOffset(@Param("size") Long size, @Param("offset") Long offset);

    @Select("SELECT file_path FROM anime WHERE id=#{id}")
    @Results({@Result(property = "filePath", column = "file_path")})
    String findFilePathListById(Long id);

    @Update("UPDATE anime SET name = #{name}, tags = #{tags}, description = #{description}, " +
            "rating = #{rating}, file_path = #{filePath} WHERE id = #{id}") //不再更新releaseDate，因为java的Date和SQL的Date类型不同，待商议
    void updateAnime(Anime anime); //直接提交一个完整的Anime类即可，因为类中包括ID

    @Update("UPDATE anime SET file_path = #{filePath} WHERE id = #{id}")
    void updateAnimeFilePathById(String filePath,Long id);

    @Delete("DELETE FROM anime WHERE id = #{id}")
    void deleteAnimeById(Long id);
}
