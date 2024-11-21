package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.model.Comment;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.CommentRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.services.CommentService;
import io.swagger.v3.oas.annotations.media.Schema;
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

    public CommentController(CommentService commentService){
        this.commentService=commentService;
    }

    @Schema(description = "根据动漫ID获取评论列表")
    @GetMapping("/getComment/{id}")
    public ResultMessage<List<Comment>> getCommentsByAnimeID(@PathVariable("id") Long id){
        try{
            List<Comment> commentList = commentService.getAllCommentByAnimeID(id);
            logger.info(commentList.toString());
            return ResultMessage.message(commentList,true,"获取成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"获取评论失败，请联络管理员！",e.getMessage());
        }
    }

    @Schema(description = "根据用户ID获取评论列表")
    @GetMapping("/getCommentByUID/{id}")
    public ResultMessage<List<Comment>> getCommentsByUserID(@PathVariable("id") Long id){
        try{
            List<Comment> commentList = commentService.getAllCommentByUserID(id);
            return ResultMessage.message(commentList,true,"获取成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"获取评论失败，请联络管理员！",e.getMessage());
        }
    }

    @Schema(description = "新增评论，需要用户登录")
    @PostMapping("/addComment")
    @AuthRequired(minPermissionLevel = 1) //需求权限，用户
    public ResultMessage<String> addComment(@RequestBody CommentRequestModel request){
        try {
            request.setCreateAt(null);
            commentService.insertComment(request);
            return ResultMessage.message(true,"操作成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"新增评论失败，请联络管理员！",e.getMessage());
        }
    }

    @Schema(description = "更改评论内容，需要管理员操作")
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

    @Schema(description = "删除评论，根据评论ID操作")
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
