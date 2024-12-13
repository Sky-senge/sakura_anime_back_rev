package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.LastUpdateMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.LastUpdate;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LastUpdateController {
    private final LastUpdateMapper lastUpdateMapper;

    public LastUpdateController(LastUpdateMapper lastUpdateMapper){
        this.lastUpdateMapper=lastUpdateMapper;
    }
    @Operation(description = "查询最后更新时间")
    @GetMapping("/getLastUpdate")
    @AuthRequired(minPermissionLevel = 1) //登陆后可操作
    public ResultMessage<LastUpdate> getLastUpdateTimestap(){
        try{
            LastUpdate result = lastUpdateMapper.getLastUpdate();
            //筛选并去掉用户表更新的数据，保护隐私
            LastUpdate sendResult = new LastUpdate();
            sendResult.setId(result.getId());
            sendResult.setCommentLastUpdate(result.getCommentLastUpdate());
            sendResult.setVideoLastUpdate(result.getVideoLastUpdate());
            sendResult.setUserLastUpdate("0");
            return ResultMessage.message(sendResult,true, "获取成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"获取失败，请联系管理员", e.getMessage());
        }
    }

}
