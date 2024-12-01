package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.model.Comment;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.CommentRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.services.CommentService;
import com.computerapplicationtechnologycnus.sakura_anime.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@Schema(description = "评论系统API入口")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService,UserService userService){
        this.commentService=commentService;
        this.userService=userService;
    }

    @Operation(description = "根据动漫ID获取评论列表，直接获取全部数据")
    @GetMapping("/getComment/{id}")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage<List<Comment>> getCommentsByAnimeID(@PathVariable("id") Long id){
        try{
            List<Comment> commentList = commentService.getAllCommentByAnimeID(id);
            logger.info(commentList.toString());
            return ResultMessage.message(commentList,true,"获取成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"获取评论失败，请联络管理员！",e.getMessage());
        }
    }

    @Operation(description = "根据动漫ID获取评论列表，分页查询，用户和未登录可用")
    @GetMapping("/getCommentList/{id}")
    public ResultMessage<List<Comment>> getCommentsByAnimeID(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0") long page,
            @RequestParam(defaultValue = "30") long size){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100 || page<0){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            List<Comment> commentList = commentService.getCommentByAnimeIDByPage(id,page,size);
            logger.info(commentList.toString());
            return ResultMessage.message(commentList,true,"获取成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"获取评论失败，请联络管理员！",e.getMessage());
        }
    }

    /**
     * 动漫详情页评论一共可以分多少页数据
     * 根据页面大小请求可以分多少页
     *
     * @param size 每页多少个数据，默认30个
     * @return int 多少页
     */
    @Operation(description = "根据页面大小请求可以分多少页")
    @GetMapping("/countCommentPage")
    public ResultMessage<Integer> countAllAnime(
            @RequestParam(defaultValue = "30") long size,
            @RequestParam(defaultValue = "") long animeId) {
        try {
            // 处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if (size > 100) {
                return ResultMessage.message(false, "您的查询参数过于巨大或不正确，请重试");
            }
            // 判断animeId是否为默认值0，因为RequestParam默认值为""，会被自动转换为0
            if (animeId==0 || animeId<1) {
                return ResultMessage.message(false, "您的查询动漫ID缺失或不正确");
            }
            // 查询执行
            int pageCount = commentService.getCommentPageTotally(size, animeId);
            return ResultMessage.message(pageCount, true, "访问成功！");
        } catch (Exception e) {
            // 捕获所有异常，并打印日志以便排查问题
//            logger.error("查询评论页数失败", e);
            return ResultMessage.message(false, "系统繁忙，请稍后重试", e.getMessage());
        }
    }

    @Operation(description = "根据用户ID获取评论列表")
    @GetMapping("/getCommentByUID/{id}")
    @AuthRequired(minPermissionLevel = 0) //只有管理员可查户口
    public ResultMessage<List<Comment>> getCommentsByUserID(@PathVariable("id") Long id,
                                                            @RequestParam(defaultValue = "0") long page,
                                                            @RequestParam(defaultValue = "30") long size){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100 || page<0){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            List<Comment> commentList = commentService.getAllCommentByUserID(id,size,page);
            return ResultMessage.message(commentList,true,"获取成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"获取评论失败，请联络管理员！",e.getMessage());
        }
    }

    @Operation(description = "新增评论，需要用户登录")
    @PostMapping("/addComment")
    @AuthRequired(minPermissionLevel = 1) //需求权限，用户
    public ResultMessage<String> addComment(@RequestBody CommentRequestModel request, HttpServletRequest requestHeader){
        try {
            String usernameFromToken = (String) requestHeader.getAttribute("username"); // 从请求中获取 username
            Long uidFromDatabase = userService.findUIDByUsername(usernameFromToken); //从数据库获取UID并对比是否本人操作
            if(!uidFromDatabase.equals(request.getUserId())){
                return ResultMessage.message(false,"添加评论失败，请本人登录！");
            }
            commentService.insertComment(request);
            return ResultMessage.message(true,"操作成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"新增评论失败，请联络管理员！",e.getMessage());
        }
    }

    @Operation(description = "更改评论内容，需要管理员操作")
    @PostMapping("/updateComment")
    @AuthRequired(minPermissionLevel = 0) //需要权限，管理员
    public ResultMessage<String> updateComment(@RequestBody Comment request){
        try{
            commentService.updateComment(request);
            return ResultMessage.message(true,"操作成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"新增评论失败，请联络管理员！",e.getMessage());
        }
    }

    @Operation(description = "删除评论，根据评论ID操作")
    @GetMapping("/deleteComment/{id}")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage<String> deleteCommentByID(@PathVariable("id") Long id){
        try{
            commentService.deleteCommentByID(id);
            return ResultMessage.message(true,"操作成功！已删除的评论ID:"+id);
        }catch (Exception e){
            return ResultMessage.message(false,"删除评论失败，请联络管理员！",e.getMessage());
        }
    }
}
