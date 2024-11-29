package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.alibaba.fastjson.JSON;
import com.computerapplicationtechnologycnus.sakura_anime.common.ModelConverter;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.AnimeMapper;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.CommentMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.Anime;
import com.computerapplicationtechnologycnus.sakura_anime.model.AnimePathObject;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeResponseModel;
import com.computerapplicationtechnologycnus.sakura_anime.utils.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnimeService {

    private static final Logger logger = LoggerFactory.getLogger(AnimeService.class);

    //构造函数
    private final AnimeMapper animeMapper;
    private final JwtUtil jwtUtil;
    private final CommentMapper commentMapper;
    private final ModelConverter modelConverter;
    public AnimeService(AnimeMapper animeMapper, JwtUtil jwtUtil, CommentMapper commentMapper, ModelConverter modelConverter){
        this.animeMapper=animeMapper;
        this.jwtUtil=jwtUtil;
        this.commentMapper=commentMapper;
        this.modelConverter=modelConverter;
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
        return modelConverter.convertToAnimeResponseList(animeList);
    }

    /**
     * 获取所有动漫信息
     * 使用分页查询功能，减少数据库压力
     *
     * @param size 分页长度
     * @param page 页面数
     * @return List<AnimeResponseModel>
     */
    @Schema(description = "获取动漫信息列表")
    public List<AnimeResponseModel> getAnimeByPage(Long size,Long page) {
        if(page<=1 || size<1){ //假如出现异常参数，恢复默认
            page = 0L;
            size = 10L;
        }else {
            page = (page-1)*size;
        }
        // 从数据库中获取 Anime 对象列表
        List<Anime> animeList = animeMapper.findAllAnimeWithIndexPageUseOffset(size,page);
        // 创建一个 List 来存储转换后的 AnimeResponseModel
        return modelConverter.convertToAnimeResponseList(animeList);
    }

    /**
     * 根据tags筛选动漫信息列表
     *
     * @param tags 动漫标签
     * @param size 分页长度
     * @param page 页面数
     * @return List<AnimeResponseModel>
     */
    @Schema(description = "根据tags筛选动漫信息列表")
    public List<AnimeResponseModel> getAnimeByTagUseOffset(List<String> tags,Long size,Long page) {
        if(page<=1 || size<1){ //假如出现异常参数，恢复默认
            page = 0L;
            size = 10L;
        }else {
            page = (page-1)*size;
        }
        // 从数据库中获取 Anime 对象列表
        List<Anime> animeList = animeMapper.findAnimeWithIndexPageByTags(tags, size, page);
        // 创建一个 List 来存储转换后的 AnimeResponseModel
        return modelConverter.convertToAnimeResponseList(animeList);
    }

    /**
     * 搜索动漫信息
     * 支持分页和模糊搜索功能
     *
     * @param name 搜索关键字
     * @param size 分页长度
     * @param page 页面数
     * @return List<AnimeResponseModel>
     */
    @Schema(description = "搜索动漫信息")
    public List<AnimeResponseModel> searchAnime(String name, Long size, Long page) {
        // 参数校验和分页逻辑处理
        if (page <= 1 || size < 1) { // 出现异常参数时，恢复默认
            page = 0L;
            size = 10L;
        } else {
            page = (page - 1) * size;
        }
        // 清理用户输入并构造模糊匹配的搜索关键字
        String cleanedName = name == null ? "" : name.trim();
        String[] words = cleanedName.split("\\s+");
        StringBuilder fuzzyName = new StringBuilder();
        for (String word : words) {
            fuzzyName.append("%").append(word).append("%");
        }
        // 从数据库中获取 Anime 对象列表
        List<Anime> animeList = animeMapper.searchAnimeByNameUseOffset(fuzzyName.toString(), size, page);
        // 转换 Anime 对象为 AnimeResponseModel
        return modelConverter.convertToAnimeResponseList(animeList);
    }

    @Schema(description = "通过ID获取动漫信息")
    public AnimeResponseModel getAnimeById(Long id){
        Anime returnedAnime=animeMapper.findAnimeById(id);
        if(returnedAnime==null){
            return new AnimeResponseModel();
        }
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
     *
     * @param id 动漫ID
     * @param epodies 动漫集数
     * @return 对应FileName
     */
    @Schema(description = "通过ID和集数来获取文件唯一路径")
    public String getAnimePathByIdAndEpodies(Long id,Long epodies){
        List<AnimePathObject> animeList = JSON.parseArray(animeMapper.findFilePathListById(id),AnimePathObject.class);
        return getFileNameByEpisode(animeList,epodies);
    }

    /**
     *
     * @param animeList List<AnimePathObject>
     * @param n 第几集
     * @return FileName属性
     */
    public static String getFileNameByEpisode(List<AnimePathObject> animeList, long n) {
        return animeList.stream()
                .filter(anime -> anime.getEpisodes() == n) // 筛选 episodes 为 n 的对象
                .map(AnimePathObject::getFileName)         // 提取 fileName 属性
                .findFirst()                               // 找到第一个匹配的结果
                .orElse(null);                       // 如果没有匹配的，返回 null
    }

    /**
     *
     * @param searchKeyWord 搜索关键字
     * @param size 查询大小
     * @param page 查询页
     * @return List<Anime>
     */
    @Deprecated //已经弃用的方法
    public List<AnimeResponseModel> animeSearch(String searchKeyWord,Long size,Long page){
        if(page<=1 || size<1){ //假如出现异常参数，恢复默认
            page = 0L;
            size = 10L;
        }else {
            page = (page-1)*size;
        }
        List<Anime> animeList = animeMapper.searchAnimeByNameUseOffset(searchKeyWord,size,page);
        // 创建一个 List 来存储转换后的 AnimeResponseModel
        return modelConverter.convertToAnimeResponseList(animeList);
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
     * @param anime Anime类
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
     * @param name 动漫名称
     * @param tags 动漫tag列表
     * @param description 动漫描述
     * @param rating 动漫评分，应当在1.0~10.0之间的小数（Float）
     * @param filePath 已经弃用的参数。
     * @throws Exception
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
