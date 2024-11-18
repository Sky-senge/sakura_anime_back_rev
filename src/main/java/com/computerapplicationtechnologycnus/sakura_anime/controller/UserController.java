package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.model.User;
import com.computerapplicationtechnologycnus.sakura_anime.services.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Schema(description = "用户API入口")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @Schema(description = "获取用户所有信息的接口")
    @GetMapping("/getAllUsers")
    public ResultMessage<List<User>> getAllUsers(){
        try{
            List<User> userList = userService.getAllUsers();
            return ResultMessage.message(userList,true,"访问成功");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据库，原因如下", e.getMessage());
        }
    }
}
