package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.computerapplicationtechnologycnus.sakura_anime.config.HistoryConfig;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.HistoryMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.History;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.HistoryRequestModel;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.HistoryResponseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoryService {
    //实例化Mapper
    private final HistoryMapper historyMapper;
    private final HistoryConfig historyConfig;

    public HistoryService(HistoryMapper historyMapper, HistoryConfig historyConfig){
        this.historyMapper=historyMapper;
        this.historyConfig = historyConfig;
    }

    /**
     * 插入新的历史记录
     * @param requestModel 历史记录新增请求体
     * @throws Exception
     */
    @Schema(description = "插入新的历史记录")
    @Transactional
    public void insertHistory(HistoryRequestModel requestModel) throws Exception {
        try{
            int maxHistoryNumber = historyConfig.getMaxSingleUser();
            Long UID = requestModel.getUserId();
            Long AID = requestModel.getAnimeId();
            Long episodes = requestModel.getEpisodes();
            //准备对象
            History history=new History();
            history.setUserId(UID);
            history.setAnimeId(AID);
            history.setEpisodes(episodes);
            // 检查用户历史记录数量
            int userHistoryCount= historyMapper.countByUserId(UID);
            if(userHistoryCount>=maxHistoryNumber){
                // 如果超过 300 条，删除最老的一条记录
                Long oldestHistoryId = historyMapper.findOldestHistoryIdByUserId(UID);
                historyMapper.deleteById(oldestHistoryId);
            }
            //查询缺失ID，并吧数据插在缺失处
            Long missingId=historyMapper.findMissingId();
            if(missingId != null){
                history.setId(missingId);
                historyMapper.insertHistoryWithId(history);
            }else {
                historyMapper.insertHistory(history);
            }
        }catch (Exception e){
            throw new Exception("无法添加历史记录！"+e.getMessage());
        }
    }

    /**
     * 获取用户历史记录表，支持分页查询
     *
     * @param UID 用户ID
     * @param size 查询长度
     * @param offset 查询第几页
     * @return List<History>
     */
    @Schema(description = "获取用户历史记录表")
    public List<HistoryResponseModel> getHistoryListByUID(Long UID, Long size, Long offset){
        if(offset<1 || size<1){ //假如出现异常参数，恢复默认
            offset = 0L;
            size = 10L;
        }else {
            offset = (offset-1)*size;
        }
        return historyMapper.findByUserIdWithDetails(UID,size,offset);
    }

    /**
     * 查询某用户拥有的历史记录条数
     * @param UID 用户ID
     * @param size 查询大小
     * @return int 总页数
     */
    @Schema(description = "获取某用户的历史记录总条数")
    public int countHistoryByUID(Long UID,Long size){
        if(size<1){ //假如出现异常参数，恢复默认
            size = 10L;
        }
        int totalCount = historyMapper.countByUserId(UID);
        return (int) Math.ceil((double) totalCount / size);
    }
}
