package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.alibaba.fastjson.JSON;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.AnimeMapper;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.CommentMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.Anime;
import com.computerapplicationtechnologycnus.sakura_anime.model.AnimePathObject;
import com.computerapplicationtechnologycnus.sakura_anime.model.Comment;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeResponseModel;
import com.computerapplicationtechnologycnus.sakura_anime.utils.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnimeService {

    private static final Logger logger = LoggerFactory.getLogger(AnimeService.class);

    //构造函数
    private final AnimeMapper animeMapper;
    private final JwtUtil jwtUtil;
    private final CommentMapper commentMapper;
    public AnimeService(AnimeMapper animeMapper,JwtUtil jwtUtil,CommentMapper commentMapper){
        this.animeMapper=animeMapper;
        this.jwtUtil=jwtUtil;
        this.commentMapper=commentMapper;
    }

    /**
     * 获取所有动漫信息
     *
     * 别问我为啥写这么绕
     * 我也想知道为什么MyBaties不支持List和驼峰命名映射
     * 我真的是服了。
     */
    @Schema(description = "获取动漫信息列表")
    public List<AnimeResponseModel> getAllAnime() {
        // 从数据库中获取 Anime 对象列表
        List<Anime> animeList = animeMapper.findAllAnimes();
        // 创建一个 List 来存储转换后的 AnimeResponseModel
        List<AnimeResponseModel> animeResponseList = new ArrayList<>();
        // 遍历 animeList 并将其转换为 AnimeResponseModel
        for (Anime anime : animeList) {
            AnimeResponseModel responseModel = new AnimeResponseModel();
            // 设置 AnimeResponseModel 的属性
            responseModel.setId(anime.getId());
            responseModel.setName(anime.getName());
            responseModel.setDescription(anime.getDescription());
            responseModel.setRating(anime.getRating());
            responseModel.setReleaseDate(anime.getReleaseDate());
            //把存进去的JSON反序列化回来
            responseModel.setFilePath(JSON.parseArray(anime.getFilePath(),AnimePathObject.class));
            // 处理 tags 字段：从 JSON 字符串转为 List<String>
            if (anime.getTags() != null) {
                List<String> tagsList = JSON.parseArray(anime.getTags(), String.class);
                responseModel.setTags(tagsList);
            }
            // 将转换后的对象添加到响应列表中
            animeResponseList.add(responseModel);
        }
        return animeResponseList;
    }

    @Schema(description = "通过ID获取动漫信息")
    public AnimeResponseModel getAnimeById(Long id){
        Anime returnedAnime=animeMapper.findAnimeById(id);
        AnimeResponseModel animeResponse=new AnimeResponseModel();
        //转换对象
        animeResponse.setId(returnedAnime.getId());
        animeResponse.setDescription(returnedAnime.getDescription());
        animeResponse.setName(returnedAnime.getName());
        animeResponse.setTags(returnedAnime.getTagsList());
        animeResponse.setRating(returnedAnime.getRating());
        animeResponse.setReleaseDate(returnedAnime.getReleaseDate());
        animeResponse.setFilePath(JSON.parseArray(returnedAnime.getFilePath(),AnimePathObject.class));
        return animeResponse;
    }

    /**
     * 通过动漫ID更新文件存在路径
     *
     * @param id        动漫ID
     * @param episodes  集数
     * @param filePath  文件路径
     */
    @Schema(description = "通过动漫ID更新文件存在路径")
    @Transactional
    public void updatePathById(Long id, Long episodes, String filePath) {
        try {
            // 获取当前路径的 JSON 字符串
            String preProduceList = animeMapper.findFilePathListById(id);
            // 将 JSON 转换为 AnimePathObject 的列表
            List<AnimePathObject> animePathList = JSON.parseArray(preProduceList, AnimePathObject.class);
            // 标记是否找到匹配的 episodes
            boolean isUpdated = false;
            // 遍历列表，查找是否存在相同的 episodes
            for (AnimePathObject obj : animePathList) {
                if (obj.getEpisodes().equals(episodes)) {
                    // 更新 fileName
                    obj.setFileName(filePath);
                    isUpdated = true;
                    break;
                }
            }
            // 如果没有找到对应的 episodes，则新增一个对象
            if (!isUpdated) {
                AnimePathObject newObject = new AnimePathObject();
                newObject.setEpisodes(episodes);
                newObject.setFileName(filePath);
                animePathList.add(newObject);
            }
            // 将更新后的列表转换为 JSON 字符串
            String updatedListJson = JSON.toJSONString(animePathList);
            // 更新数据库
            animeMapper.updateAnimeFilePathById(updatedListJson, id);
            // 日志记录
            logger.info("ID为 \"" + id + "\" 的动漫路径已更新为：" + updatedListJson);
        } catch (Exception e) {
            // 错误处理
            logger.error("更新动漫路径时发生错误：", e);
            throw new RuntimeException("更新动漫路径失败", e);
        }
    }

    /**
     * 更新动漫信息
     *
     * @return 是否成功
     */
    @Schema(description = "更新动漫信息")
    @Transactional
    public void updateAnime(AnimeRequestModel anime) throws Exception{
        try{
            // 校验评分范围并限制为一位小数
            if (anime.getRating() == null || anime.getRating() < 1 || anime.getRating() > 10 || !isOneDecimalPlace(anime.getRating())) {
                throw new IllegalArgumentException("评分必须在 1 到 10 之间，并保留一位小数，例如 3.8、5.6、10.0");
            }
            //为什么要绕弯弯，为什么MyBaties不能原生处理List？啊？
            Anime animeFinal =new Anime();
            animeFinal.setId(anime.getId());
            animeFinal.setName(anime.getName());
            animeFinal.setTagsList(anime.getTags());
            animeFinal.setDescription(anime.getDescription());
            animeFinal.setFilePath(JSON.toJSONString(anime.getFilePath()));
            animeFinal.setRating(anime.getRating());
            animeMapper.updateAnime(animeFinal);
        }catch (Exception e){
            throw new Exception("视频数据库更新失败："+e.getMessage());
        }
    }

    /**
     * 添加新的动漫
     *
     * @return 是否成功
     */
    @Schema(description = "添加新的动漫")
    @Transactional
    public void insertAnime(String name,List<String> tags,String description,Float rating,String filePath) throws Exception{
        try{
            // 校验评分范围并限制为一位小数
            if (rating == null || rating < 1 || rating > 10 || !isOneDecimalPlace(rating)) {
                throw new IllegalArgumentException("评分必须在 1 到 10 之间，并保留一位小数，例如 3.8、5.6、10.0");
            }

            Long missingId=animeMapper.findMissingId();
            Anime anime=new Anime();
            anime.setName(name);
            anime.setTagsList(tags);
            anime.setDescription(description);
            anime.setFilePath("[]"); //为避免后续冲突问题，文件路径由文件上传提供。
            anime.setRating(rating);

            //检查Rating应当在 1-10内的一位小数，例如3.8 ，5.6 ，10.0

            if(missingId !=null){
                anime.setId(missingId);
                animeMapper.insertAnimeWithId(anime);
            }else {
                animeMapper.insertAnime(anime);
            }
        }catch (Exception e){
            throw new Exception("视频数据库添加失败："+e.getMessage());
        }
    }

    /**
     * 检查一个浮点数是否为一位小数
     */
    private boolean isOneDecimalPlace(Float number) {
        // 转换为字符串检查是否为一位小数
        String str = String.format("%.1f", number);
        return Float.parseFloat(str) == number;
    }

    /**
     * 删除1某个动漫列表
     *
     *
     */
    @Schema(description = "删除某条动漫列表")
    @Transactional
    public void deleteAnime(Long id) throws Exception {
        try{
            animeMapper.deleteAnimeById(id);
            // 注意！删除动漫会导致该动漫下的评论一并全部删除！
            commentMapper.deleteCommentsByAnimeId(id);
        }catch (Exception e){
            throw new Exception("该条目["+id+"]删除失败！原因："+e.getMessage());
        }
    }
}
