package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.controller.FileController;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.CommentMapper;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.UserMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.User;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.UserLoginRequest;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.UserLoginResponse;
import com.computerapplicationtechnologycnus.sakura_anime.utils.JwtUtil;
import com.computerapplicationtechnologycnus.sakura_anime.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    //构造函式
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final SecurityUtils securityUtils;
    private final JwtUtil jwtUtil;
    public UserService(UserMapper userMapper, CommentMapper commentMapper,SecurityUtils securityUtils,JwtUtil jwtUtil){
        this.userMapper=userMapper;
        this.commentMapper=commentMapper;
        this.securityUtils = securityUtils;
        this.jwtUtil = jwtUtil;
    }

    //查询所有用户
    public List<User> getAllUsers(){
        return userMapper.findAllUsers();
    }

    //查询所有用户，分页查询
    public List<User> getUsersByPage(Long size,Long page){
        if(page<1 || size<1){ //假如出现异常参数，恢复默认
            page = 0L;
            size = 10L;
        }else {
            page = (page-1)*size;
        }
        return userMapper.findUsersUseOffset(size,page);
    }

    //根据UID查询用户
    public User getUserByUID(Long uid){return userMapper.findUserDetailByID(uid);}

    /**
     * 验证用户身份
     *
     * @param UserLoginRequest 登录请求对象
     * @return 如果验证通过，返回一个包含 Token 和 userId 的响应；否则返回 null
     */
    public UserLoginResponse authenticateUser(UserLoginRequest UserLoginRequest) throws Exception {
        // 根据用户名查找用户
        User user = userMapper.findByUsernameIncludePassword(UserLoginRequest.getUsername());
        if (user == null) {
            throw new Exception("用户不存在！"); // 用户不存在
        }
        // 使用 SecurityUtils 比较密码（数据库中存储的是散列后的密码）
        String hashedPassword = securityUtils.sha256Hash(UserLoginRequest.getPassword());
        if (!hashedPassword.equals(user.getPassword())) {
            throw new Exception("用户名或密码不正确，请重试"); // 密码错误
        }
        if(user.getPermission() >= 2){
            throw new Exception("用户已被封锁！");
        }
        // 使用 JwtUtil 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getPermission(),user.getPassword());
        // 返回包含 Token 和 userId 的响应
        return new UserLoginResponse(token, user.getId());
    }

    /**
     * 根据用户名查询UID
     * @param username 唯一用户名
     */
    public Long findUIDByUsername(String username){
        return userMapper.findUserIdByUsername(username);
    }

    /**
     * 注册用户
     *
     * @return 是否成功
     */
    @Transactional //修改表需要使用事务
    public void register(String email, String username, String password, String displayName, String remarks) throws Exception {
        try {
            // 邮箱格式的正则表达式
            String emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
            Pattern pattern = Pattern.compile(emailRegex);
            if(email.isBlank() || username.isBlank() || password.isBlank()){
                throw new Exception("邮箱/用户名/密码 不能为空！");
            }
            if (!pattern.matcher(email).matches()) {
                throw new Exception("邮箱格式不正确！");
            }
            // 对密码进行加密
            String hashedPassword = SecurityUtils.sha256Hash(password);
            // 检查是否存在不连续的 ID
            Long missingId = userMapper.findMissingId();
            // 创建用户对象
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(hashedPassword);
            user.setDisplayName(displayName);  // displayName 可能为 null
            user.setRemarks(remarks);  // remarks 可能为 null
            user.setPermission(1);  // 默认权限级别为 1 (普通用户)

            if (missingId != null) {
                // 如果存在缺失的 ID，则手动指定 ID
                user.setId(missingId);
                userMapper.insertUserWithId(user);
            } else {
                // 正常插入，不指定 ID
                userMapper.insertUser(user);
            }
        } catch (Exception e) {
            throw new Exception("用户注册失败：" + e.getMessage(), e);
        }
    }

    /**
     * 更改密码
     * @param userId 用户ID
     * @param passwd 用户密码
     */
    @Transactional
    public void updatePassword(Long userId,String passwd) throws Exception {
        try{
            String hashedPassword = SecurityUtils.sha256Hash(passwd);
            userMapper.updatePasswordById(userId,hashedPassword);
        }catch (Exception e){
            throw new Exception("更改密码失败："+e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     */
    @Transactional
    public void updateUser(User user) throws Exception {
        try{
            Long UID=user.getId();
            String hashedPassword = SecurityUtils.sha256Hash(user.getPassword());
            if(user.getPassword().isEmpty()){
                //以免发生空密码把用户变成免密登录的事件
                hashedPassword = userMapper.findUserPasskeyByUsername(user.getUsername());
            }
            userMapper.updateUsernameById(UID,user.getUsername());
            userMapper.updateAvatarById(UID,user.getAvatar());
            userMapper.updateEmailById(UID,user.getEmail());
            userMapper.updatePasswordById(UID,hashedPassword);
            userMapper.updatePermissionById(UID,user.getPermission());
            userMapper.updateRemarksById(UID,user.getRemarks());
            userMapper.updateDisplayNameById(UID, user.getDisplayName());
        }catch (Exception e){
            throw new Exception("更改密码失败："+e.getMessage());
        }
    }

    /**
     * 查询数据库总数来确认有多少页数据
     *
     * @param size 每页会显示多少个动漫
     * @return int 这样可以分出来多少页数据
     * @throws Exception
     */
    @Schema(description = "根据页面长度来统计有多少页可以翻页")
    public int getUserPageTotally(Long size) throws Exception {
        try {
            if (size < 1) {
                throw new Exception("每页请求内容不能小于1个！");
            }
            int totalCount = userMapper.countUser();
            // 使用向上取整，确保即使有余数也能分出一页
            return (int) Math.ceil((double) totalCount / size);
        } catch (Exception e) {
            throw new Exception("请求页面失败：" + e.getMessage(), e);
        }
    }

    /**
     * 保存头像文件名到数据库
     * @param userId 用户ID
     * @param filename 文件名
     */
    @Transactional //修改表需要使用事务
    public void saveAvatarToDatabase(Long userId, String filename) {
        userMapper.updateAvatarById(userId,filename);
        logger.info("用户ID: " + userId + " 的头像已更新为: " + filename);
    }

    /**
     * 注销用户
     *
     * @param userId 要注销的用户ID
     * @return 是否成功
     */
    @Transactional
    public boolean deleteUserWithComments(Long userId) {
        // 删除用户的评论
        int deletedComments = commentMapper.deleteCommentsByUserId(userId);
        System.out.println("删除的评论数量: " + deletedComments);

        // 删除用户
        int deletedUser = userMapper.deleteUserById(userId);
        System.out.println("删除的用户数量: " + deletedUser);

        return deletedUser > 0;
    }

}
