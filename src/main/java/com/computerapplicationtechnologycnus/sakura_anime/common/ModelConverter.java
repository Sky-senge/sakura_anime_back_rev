package com.computerapplicationtechnologycnus.sakura_anime.common;

import com.alibaba.fastjson.JSON;
import com.computerapplicationtechnologycnus.sakura_anime.model.Anime;
import com.computerapplicationtechnologycnus.sakura_anime.model.AnimePathObject;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeResponseModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModelConverter {

    /**
     * 将animeList转换为animeResponseList
     * @param animeList List<Anime>
     * @return List<AnimeResponseModel>
     */
    public List<AnimeResponseModel> convertToAnimeResponseList(List<Anime> animeList){
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
            responseModel.setFilePath(JSON.parseArray(anime.getFilePath(), AnimePathObject.class));
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
}
