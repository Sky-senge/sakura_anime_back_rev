package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.alibaba.fastjson.JSON;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.AnimeMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.Anime;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeResponseModel;
import com.computerapplicationtechnologycnus.sakura_anime.utils.JwtUtil;
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
    public AnimeService(AnimeMapper animeMapper,JwtUtil jwtUtil){
        this.animeMapper=animeMapper;
        this.jwtUtil=jwtUtil;
    }

    /**
     * 获取所有动漫信息
     *
     * 别问我为啥写这么绕
     * 我也想知道为什么MyBaties不支持List和驼峰命名映射
     * 我真的是服了。
     */
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
            responseModel.setFilePath(anime.getFilePath());
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

    /**
     * 更新动漫信息
     *
     * @return 是否成功
     */

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
            animeFinal.setFilePath(anime.getFilePath());
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
            anime.setFilePath(filePath);
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
}
