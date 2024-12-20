package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.model.History;
import com.computerapplicationtechnologycnus.sakura_anime.model.User;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.*;
import com.computerapplicationtechnologycnus.sakura_anime.services.AnimeService;
import com.computerapplicationtechnologycnus.sakura_anime.services.HistoryService;
import com.computerapplicationtechnologycnus.sakura_anime.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/user")
@Schema(description = "用户API入口")
public class UserController {
    private final UserService userService;
    private final HistoryService historyService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, HistoryService historyService){
        this.userService=userService;
        this.historyService=historyService;
    }

    @Operation(description = "用户登录")
    @PostMapping("/login")
    public ResultMessage<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        try{
           UserLoginResponse token = userService.authenticateUser(request);
           return ResultMessage.message(token,true,"登录成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"登录失败！",e.getMessage());
        }
    }

    @Operation(description = "更改密码(用户侧)")
    @PostMapping("/modPassword")
    @AuthRequired(minPermissionLevel = 1)
    public ResultMessage<String> userModPassword(@RequestBody UserModPasswordRequestModel request, HttpServletRequest requestHeader){
        try{
            // 从请求中获取 username
            String usernameFromToken = (String) requestHeader.getAttribute("username");
            Long uidFromDatabase = userService.findUIDByUsername(usernameFromToken);
            if(!uidFromDatabase.equals(request.getId())){
                return ResultMessage.message(false,"修改密码失败，请本人登录！");
            }
            userService.updatePassword(request.getId(),request.getPassword());
            return ResultMessage.message(true,"修改密码成功，请重新登陆！");
        }catch (Exception e){
            return ResultMessage.message(false,"修改密码失败，请联系管理员！", e.getMessage());
        }
    }

    @Operation(description = "更新用户的信息")
    @PostMapping("/updateUser")
    @AuthRequired(minPermissionLevel = 0) //管理员可用，更改用户的全部信息
    public ResultMessage<User> userUpdate(@RequestBody User request){
        try{
            userService.updateUser(request);
            User modAfterUser = userService.getUserByUID(request.getId());
            return ResultMessage.message(modAfterUser,true,"用户更新成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"用户修改失败！可能存在冲突用户名和邮箱",e.getMessage());
        }
    }

    @Operation(description = "根据UID查询用户详情")
    @GetMapping("/getDetail")
    @AuthRequired(minPermissionLevel = 1)
    public ResultMessage<User> getDetail(HttpServletRequest requestHeader){
        try{
            // 从请求中获取 username
            String usernameFromToken = (String) requestHeader.getAttribute("username");
            Long uidFromDatabase = userService.findUIDByUsername(usernameFromToken);
            User userDetail = userService.getUserByUID(uidFromDatabase);
            return ResultMessage.message(userDetail,true,"获取成功");
        }catch (Exception e){
            return ResultMessage.message(false,"用户查询失败，请登录！",e.getMessage());
        }
    }
    @Operation(description = "用户注册")
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

    @Operation(description = "获取用户所有信息的接口")
    @AuthRequired(minPermissionLevel = 0)  // 使用 AuthRequired 注解，进行认证，只有管理员可用
    @GetMapping("/getAllUsers")
    public ResultMessage<List<User>> getAllUsers(){
        try{
            List<User> userList = userService.getAllUsers();
            return ResultMessage.message(userList,true,"访问成功");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据库，原因如下", e.getMessage());
        }
    }

    /**
     * 首页，用户一共可以分多少页数据
     * 根据页面大小请求可以分多少页
     *
     * @param size 每页多少个数据，默认12个
     * @return int 多少页
     */
    @Operation(description = "根据页面大小请求可以分多少页")
    @GetMapping("/countUserPage")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage<Integer> countAllAnime(@RequestParam(defaultValue = "30") long size){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            int animeList = userService.getUserPageTotally(size);
            return ResultMessage.message(animeList,true,"访问成功！");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据，原因如下："+e.getMessage());
        }
    }

    /**
     *
     * @param page 分页页数
     * @param size 分页列表大小
     * @return
     */
    @Operation(description = "分页获取用户所有信息的接口")
    @AuthRequired(minPermissionLevel = 0)  // 使用 AuthRequired 注解，进行认证，仅管理员可访问
    @GetMapping("/getUserList")
    public ResultMessage<List<User>> getUsersByPage(
            @RequestParam(defaultValue = "0") long page,
            @RequestParam(defaultValue = "10") long size
    ){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100 || page<0){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            List<User> userList = userService.getUsersByPage(size,page);
            return ResultMessage.message(userList,true,"访问成功");
        }catch (Exception e){
            return ResultMessage.message(false,"无法访问数据库，原因如下", e.getMessage());
        }
    }

    @GetMapping("/deleteUser/{id}")
    @AuthRequired(minPermissionLevel = 0) //仅限管理员可删除用户
    public ResultMessage deleteUser(@PathVariable("id") Long userId) {
        boolean success = userService.deleteUserWithComments(userId);
        if (success) {
            return ResultMessage.message(true,"删除成功，UID："+userId);
        } else {
            return ResultMessage.message(false,"用户删除失败");
        }
    }

    @Operation(description = "新增历史记录")
    @PostMapping("/createHistory")
    @AuthRequired(minPermissionLevel = 1)
    public ResultMessage<String> createVideoHistory(@RequestBody HistoryRequestModel request, HttpServletRequest requestHeader) throws Exception {
        try{
            if(request.getUserId()<1 || request.getAnimeId()<1 || request.getEpisodes()<1){
                return ResultMessage.message(false,"参数不正确！");
            }
            // 从请求中获取 username
            String usernameFromToken = (String) requestHeader.getAttribute("username");
            Long uidFromDatabase = userService.findUIDByUsername(usernameFromToken);
            request.setUserId(uidFromDatabase);
            historyService.insertHistory(request);
            return ResultMessage.message(true,"新增历史记录成功");
        }catch (Exception e){
            return ResultMessage.message(false,"无法新增历史，原因如下", e.getMessage());
        }
    }

    @Operation(description = "获取历史记录列表")
    @GetMapping("/getHistory")
    @AuthRequired(minPermissionLevel = 1)
    public ResultMessage<List<HistoryResponseModel>> getHistoryList(
            @RequestParam(defaultValue = "0") long page,
            @RequestParam(defaultValue = "30") long size,
            HttpServletRequest requestHeader
    ){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100 || page<0){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            String usernameFromToken = (String) requestHeader.getAttribute("username");
            Long uidFromDatabase = userService.findUIDByUsername(usernameFromToken);
            List<HistoryResponseModel> historyList = historyService.getHistoryListByUID(uidFromDatabase,size,page);
            return ResultMessage.message(historyList,true,"获取历史记录成功");
        }catch (Exception e){
            return ResultMessage.message(false,"无法查找到历史记录！", e.getMessage());
        }
    }

    @Operation(description = "获取历史记录总页数")
    @GetMapping("/countHistory")
    @AuthRequired(minPermissionLevel = 1)
    public ResultMessage<Integer> countHistoryList(
            @RequestParam(defaultValue = "30") long size,
            HttpServletRequest requestHeader
    ){
        try{
            //处理可能存在刁民给你搬来巨大或错误参数拖累性能
            if(size>100){
                return ResultMessage.message(false,"您的查询参数过于巨大或不正确，请重试");
            }
            //查询执行
            String usernameFromToken = (String) requestHeader.getAttribute("username");
            Long uidFromDatabase = userService.findUIDByUsername(usernameFromToken);
            int historyCount = historyService.countHistoryByUID(uidFromDatabase,size);
            return ResultMessage.message(historyCount,true,"获取历史记录成功");
        }catch (Exception e){
            return ResultMessage.message(false,"无法查找到历史记录！", e.getMessage());
        }
    }
}


