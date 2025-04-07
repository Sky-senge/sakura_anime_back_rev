package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.model.Anime;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeCreateModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeResponseModel;
import com.computerapplicationtechnologycnus.sakura_anime.services.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.ArrayList;
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

    @Operation(description = "获取全部动漫列表讯息")
    @GetMapping("/getAllAnime")
    @AuthRequired(minPermissionLevel = 0) //仅限管理员使用
    public ResultMessage<List<AnimeResponseModel>> getAllAnime(){
        try{
            List<AnimeResponseModel> animeList = animeService.getAllAnime();

            return ResultMessage.message(animeList,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }

    /**
     * 首页，使用分页查询动漫列表信息
     * @param page 多少页，首页默认第一页
     * @param size 每页多少个数据，默认10个
     * @return 动漫列表
     */
    @Operation(description = "获取全部动漫列表讯息，但使用分页查询")
    @GetMapping("/getAnimeList")
    public ResultMessage<List<AnimeResponseModel>> getAllAnimeByPage(
            @RequestParam(defaultValue = "0") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false, defaultValue = "") String sort
    ){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100 || page<0){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            List<AnimeResponseModel> animeList = animeService.getAnimeByPage(size,page,sort);
            if(animeList.isEmpty()){
                return ResultMessage.message(false,"无法访问数据，数据为空");
            }
            return ResultMessage.message(animeList,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }


//    @Operation(description = "获取全部动漫列表讯息，但使用分页查询")
//    @GetMapping("/getAnimeListRedis")
//    public ResultMessage<List<AnimeResponseModel>> getAllAnimeByPage_Redis(
//            @RequestParam(defaultValue = "0") long page,
//            @RequestParam(defaultValue = "10") long size,
//            @RequestParam(required = false, defaultValue = "") String sort
//    ){
//        try{
//            // 处理可能存在刁民给你搬来巨大或错误参数拖累性能
//            if(size > 100 || page < 0){
//                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
//            }
//
//            // 生成唯一的缓存key
//            String cacheKey = String.format("anime:list:page:%d:size:%d:sort:%s", page, size, sort);
//
//            // 尝试从缓存获取
//            List<AnimeResponseModel> cachedList = redisTemplate.opsForValue().get(cacheKey);
//            if(cachedList != null){
//                return ResultMessage.message(cachedList, true, "数据来自缓存");
//            }
//
//            // 缓存未命中，查询数据库
//            List<AnimeResponseModel> animeList = animeService.getAnimeByPage(size, page, sort);
//            if(animeList.isEmpty()){
//                return ResultMessage.message(false,"无法访问数据，数据为空");
//            }
//
//            // 将结果存入缓存，设置过期时间(例如30分钟)
//            redisTemplate.opsForValue().set(cacheKey, animeList, 30, TimeUnit.MINUTES);
//
//            return ResultMessage.message(animeList, true, "访问成功！");
//        }catch (Exception e){
//            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
//        }
//    }

    /**
     * 首页，动漫一共可以分多少页数据
     * 根据页面大小请求可以分多少页
     *
     * @param size 每页多少个数据，默认12个
     * @return int 多少页
     */
    @Operation(description = "根据页面大小请求可以分多少页")
    @GetMapping("/countAnimePage")
    public ResultMessage<Integer> countAllAnime(@RequestParam(defaultValue = "12") long size){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            int animeList = animeService.getAnimePageTotally(size);
            return ResultMessage.message(animeList,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }

    /**
     * 分类页，根据Tag筛选并使用分页查询动漫列表信息
     *
     * @param page 多少页，首页默认第一页
     * @param size 每页多少个数据，默认10个
     * @param tag List<String> 关于动漫的标签
     * @return 动漫列表
     */
    @Operation(description = "根据Tags获取动漫资源信息")
    @GetMapping("/getAnimeListByTags")
    public ResultMessage<List<AnimeResponseModel>> getAllAnimeByPageWithTags(
            @RequestParam(defaultValue = "0") long page,
            @RequestParam(defaultValue = "30") long size,
            @RequestParam(defaultValue = "") List<String> tag,
            @RequestParam(required = false, defaultValue = "") String sort
    ){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100 || page<0){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            List<AnimeResponseModel> animeList = animeService.getAnimeByTagUseOffset(tag,size,page,sort);
            if(animeList.isEmpty()){
                return ResultMessage.message(false,"无法访问数据，数据为空");
            }
            return ResultMessage.message(animeList,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }

    /**
     * 分类页，根据Tag筛选并使用分页查询动漫列表信息（总数）
     *
     * @param size 每页多少个数据，默认10个
     * @param tag List<String> 关于动漫的标签
     * @return 动漫列表
     */
    @Operation(description = "根据Tags获取动漫资源信息总页数")
    @GetMapping("/countAnimeListByTags")
    public ResultMessage<Integer> countAllAnimeByPageWithTags(
            @RequestParam(defaultValue = "30") long size,
            @RequestParam(defaultValue = "") List<String> tag
    ){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            int animeCount = animeService.countAnimeByTagUseOffsetTotally(tag,size,1L);

            return ResultMessage.message(animeCount,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }

    /**
     * 获取搜索列表
     *
     * @param keyWord 查询关键词
     * @param page 多少页，默认第一页
     * @param size 每页多少个数据，默认30个
     * @return
     */
    @Operation(description = "按名称搜索")
    @GetMapping("/searchByName")
    public ResultMessage<List<AnimeResponseModel>> searchAnimeByName(
            @RequestParam(defaultValue = "") String keyWord,
            @RequestParam(defaultValue = "0") long page,
            @RequestParam(defaultValue = "30") long size
    ){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100 || page<0){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            List<AnimeResponseModel> animeList = animeService.searchAnime(keyWord,size,page);
            return ResultMessage.message(animeList,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }

    /**
     * 获取搜索列表总页数
     *
     * @param keyWord 查询关键词
     * @param size 每页多少个数据，默认30个
     * @return
     */
    @Operation(description = "按名称搜索（获取总页数）")
    @GetMapping("/countSearchByName")
    public ResultMessage<Integer> countSearchAnimeByName(
            @RequestParam(defaultValue = "") String keyWord,
            @RequestParam(defaultValue = "30") long size
    ){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            int animeCount = animeService.countSearchAnime(keyWord,size,1L);
            return ResultMessage.message(animeCount,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }

    @Operation(description = "根据ID获取动漫详情")
    @GetMapping("/getDetail/{id}")
    public ResultMessage<AnimeResponseModel> getDetail(@PathVariable("id") Long id){
        try{
            AnimeResponseModel animeDetail = animeService.getAnimeById(id);
            return ResultMessage.message(animeDetail,true,"请求成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"获取失败！请联络管理员",e.getMessage());
        }
    }

    @Operation(description = "新增动漫资源 (对数据库)")
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

    @Operation(description = "删除指定动漫列表")
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

    @Operation(description = "更新动漫资源 (对数据库)")
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
