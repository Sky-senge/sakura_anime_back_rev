package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.computerapplicationtechnologycnus.sakura_anime.mapper.CommentMapper;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.LastUpdateMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.Comment;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.CommentRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.CommentResponseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.computerapplicationtechnologycnus.sakura_anime.utils.TimeUtil.getCurrentTimestampInSeconds;

@Service
public class CommentService {
    //构造函数，实例化Mapper
    private final CommentMapper commentMapper;
    private final LastUpdateMapper lastUpdateMapper;
    public CommentService(CommentMapper commentMapper,LastUpdateMapper lastUpdateMapper){
        this.commentMapper=commentMapper;
        this.lastUpdateMapper=lastUpdateMapper;
    }

    @Schema(description = "根据动漫ID，查询评论列表")
    public List<Comment> getAllCommentByAnimeID(Long id){
        return commentMapper.findCommentsByAnimeId(id);
    }

    @Schema(description = "根据动漫ID，分页查询评论列表")
    public List<CommentResponseModel> getCommentByAnimeIDByPage(Long id, Long page, Long size){
        if(page<1 || size<1){ //假如出现异常参数，恢复默认
            page = 0L;
            size = 30L;
        }else {
            page = (page-1)*size;
        }
//        return commentMapper.findCommentsByAnimeIdUseOffset(id,size,page);
          return commentMapper.findCommentsByAnimeIdUseOffsetDisplayUsername(id,size,page);
    }

    /**
     * 查询数据库总数来确认有多少页数据
     *
     * @param size 查询每页大小
     * @param animeId 对应的动漫ID
     * @return int 总页数
     * @throws Exception
     */
    @Schema(description = "根据页面长度来统计有多少页可以翻页")
    public int getCommentPageTotally(Long size,Long animeId) throws Exception {
        try {
            if (size < 1) {
                throw new Exception("每页请求内容不能小于1个！");
            }
            int totalCount = commentMapper.countComment(animeId);
            // 使用向上取整，确保即使有余数也能分出一页
            return (int) Math.ceil((double) totalCount / size);
        } catch (Exception e) {
            throw new Exception("请求页面失败：" + e.getMessage(), e);
        }
    }

    @Schema(description = "获取全部评论，不管是什么动漫的，仅限管理员使用")
    public List<Comment> getAllComment(){
        return commentMapper.findAllComment();
    }

    @Schema(description = "根据用户ID，查询其全部评论")
    public List<Comment> getAllCommentByUserID(Long id,Long size,Long page){
        if(page<1 || size<1){ //假如出现异常参数，恢复默认
            page = 0L;
            size = 30L;
        }else {
            page = (page-1)*size;
        }
        return commentMapper.findCommentsByUserId(id,size,page);
    }

    @Schema(description = "插入新的评论，仅限登录用户可用")
    @Transactional
    public void insertComment(CommentRequestModel request) throws Exception {
        try{
            //转到Comment类
            Comment comment=new Comment();
            comment.setAnimeId(request.getAnimeId());
            comment.setUserId(request.getUserId());
            comment.setContent(request.getContent());

            //查询缺失ID，如果有的话
            Long missingId=commentMapper.findMissingId();
            if(missingId !=null){
                comment.setId(missingId);
                commentMapper.insertCommentWithId(comment);
                Long currentTimeSec = getCurrentTimestampInSeconds();
                lastUpdateMapper.updateCommentLastUpdate(String.valueOf(currentTimeSec));
            }else {
                commentMapper.insertComment(comment);
                Long currentTimeSec = getCurrentTimestampInSeconds();
                lastUpdateMapper.updateCommentLastUpdate(String.valueOf(currentTimeSec));
            }
        }catch (Exception e){
            throw new Exception("评论无法正常添加！"+e.getMessage());
        }
    }

    @Schema(description = "更新评论列表，仅限管理员可用")
    @Transactional
    public void updateComment(Comment request) throws Exception {
        try{
            commentMapper.updateComment(request);
            Long currentTimeSec = getCurrentTimestampInSeconds();
            lastUpdateMapper.updateCommentLastUpdate(String.valueOf(currentTimeSec));
        }catch (Exception e){
            throw new Exception("评论无法正常更新！"+e.getMessage());
        }
    }

    @Schema(description = "删除评论列表，仅限管理员可用")
    @Transactional
    public void deleteCommentByID(Long id) throws Exception {
        try{
            commentMapper.deleteComment(id);
            Long currentTimeSec = getCurrentTimestampInSeconds();
            lastUpdateMapper.updateCommentLastUpdate(String.valueOf(currentTimeSec));
        }catch (Exception e){
            throw new Exception("评论无法正常更新！"+e.getMessage());
        }
    }
}
