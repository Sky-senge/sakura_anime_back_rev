package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.model.Anime;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeCreateModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeResponseModel;
import com.computerapplicationtechnologycnus.sakura_anime.services.AnimeService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@RestController
@RequestMapping("/api/anime")
@Schema(description = "视频资源API入口")
public class AnimeController {

    private final AnimeService animeService;
    private final HandlerMapping resourceHandlerMapping;

    public AnimeController(AnimeService animeService, @Qualifier("resourceHandlerMapping") HandlerMapping resourceHandlerMapping) {
        this.animeService = animeService;
        this.resourceHandlerMapping = resourceHandlerMapping;
    }

    @Schema(description = "获取全部动漫列表讯息")
    @GetMapping("/getAllAnime")
    public ResultMessage<List<AnimeResponseModel>> getAllAnime(){
        try{
            List<AnimeResponseModel> animeList = animeService.getAllAnime();

            return ResultMessage.message(animeList,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }

    @Schema(description = "根据ID获取动漫详情")
    @GetMapping("/getDetail/{id}")
    public ResultMessage<AnimeResponseModel> getDetail(@PathVariable("id") Long id){
        try{
            AnimeResponseModel animeDetail = animeService.getAnimeById(id);
            return ResultMessage.message(animeDetail,true,"请求成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"获取失败！请联络管理员",e.getMessage());
        }
    }

    @Schema(description = "新增动漫资源 (对数据库)")
    @PostMapping("/createAnime")
    @AuthRequired(minPermissionLevel = 0) //Only 管理员 can do.  权限详情见model/User的permission定义
    public ResultMessage createAnime(@RequestBody AnimeCreateModel request){
        try{
            animeService.insertAnime(request.getName(), request.getTags(),request.getDescription(),request.getRating(),request.getFilePath());
            return ResultMessage.message(true,"添加成功！");
        }catch (Exception e){
//            e.printStackTrace();
            return ResultMessage.message(false,"添加失败！请联络管理员",e.getMessage());
        }
    }

    @Schema(description = "删除指定动漫列表")
    @GetMapping("/deleteAnime/{id}")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage deleteAnime(@PathVariable("id") Long id){
        try{
            AnimeResponseModel animeDetail = animeService.getAnimeById(id);
            animeService.deleteAnime(id);
            return ResultMessage.message(animeDetail,true,"操作成功，已删除的动漫讯息如上\n【相关评论也随之删除！】");
        }catch (Exception e){
            return ResultMessage.message(false,"删除失败！请联络管理员",e.getMessage());
        }
    }

    @Schema(description = "更新动漫资源 (对数据库)")
    @PostMapping("/updateAnime")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage updateAnime(@RequestBody AnimeRequestModel request){
        try{
            animeService.updateAnime(request);
            return ResultMessage.message(true,"添加成功！");
        }catch (Exception e){
//            e.printStackTrace();
            return ResultMessage.message(false,"添加失败！请联络管理员",e.getMessage());
        }
    }
}
