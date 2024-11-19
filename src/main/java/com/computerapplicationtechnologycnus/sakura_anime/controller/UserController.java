package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.model.User;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.UserLoginRequest;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.UserLoginResponse;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.UserRegistryModel;
import com.computerapplicationtechnologycnus.sakura_anime.services.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Schema(description = "用户API入口")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @Schema(description = "用户登录")
    @PostMapping("/login")
    public ResultMessage<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        try{
           UserLoginResponse token = userService.authenticateUser(request);
            return ResultMessage.message(token,true,"登录成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"登录失败！",e.getMessage());
        }
    }

    @Schema(description = "用户注册")
    @PostMapping("/register")
    public ResultMessage register(@RequestBody UserRegistryModel request) {
        try{
            userService.register(request.getEmail(),request.getUsername(),request.getPassword(),request.getDisplayName(),request.getRemarks());
            return ResultMessage.message(true,"注册成功");
        }catch (Exception e){
//            e.printStackTrace(); // 打印完整堆栈，用于调试
            return ResultMessage.message(false,"注册失败！可能存在冲突用户名和邮箱",e.getMessage());
        }
    }

    @Schema(description = "获取用户所有信息的接口")
    @AuthRequired(minPermissionLevel = 1)  // 使用 AuthRequired 注解，进行认证，普通用户即可访问
    @GetMapping("/getAllUsers")
    public ResultMessage<List<User>> getAllUsers(){
        try{
            List<User> userList = userService.getAllUsers();
            return ResultMessage.message(userList,true,"访问成功");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据库，原因如下", e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResultMessage deleteUser(@PathVariable Long userId) {
        boolean success = userService.deleteUserWithComments(userId);
        if (success) {
            return ResultMessage.message(true,"删除成功，UID："+userId);
        } else {
            return ResultMessage.message(false,"用户删除失败");
        }
    }
}
