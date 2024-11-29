package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.computerapplicationtechnologycnus.sakura_anime.mapper.HistoryMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.History;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.HistoryRequestModel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoryService {
    //实例化Mapper
    private final HistoryMapper historyMapper;
    public HistoryService(HistoryMapper historyMapper){
        this.historyMapper=historyMapper;
    }

    @Schema(description = "插入新的历史记录")
    @Transactional
    public void insertHistory(HistoryRequestModel requestModel) throws Exception {
        try{
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
            if(userHistoryCount>=300){
                // 如果超过 300 条，删除最老的一条记录
                Long oldestHistoryId = historyMapper.findOldestHistoryIdByUserId(UID);
                historyMapper.deleteById(oldestHistoryId);
            }
            //查询缺失ID，并吧数据插在缺失处
            Long missingId=historyMapper.findMissingId();
            if(missingId !=null){
                history.setId(missingId);
                historyMapper.insertHistoryWithId(history);
            }else {
                historyMapper.insertHistory(history);
            }
        }catch (Exception e){
            throw new Exception("无法添加历史记录！"+e.getMessage());
        }
    }

    @Schema(description = "获取用户历史记录表")
    public List<History> getHistoryListByUID(Long UID,Long size,Long offset){
        if(offset<=1 || size<1){ //假如出现异常参数，恢复默认
            offset = 0L;
            size = 10L;
        }else {
            offset = (offset-1)*size;
        }
        return historyMapper.findByUserId(UID,size,offset);
    }
}
