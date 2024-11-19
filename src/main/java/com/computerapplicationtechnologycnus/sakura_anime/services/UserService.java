package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.computerapplicationtechnologycnus.sakura_anime.mapper.CommentMapper;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.UserMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.User;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.UserLoginRequest;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.UserLoginResponse;
import com.computerapplicationtechnologycnus.sakura_anime.utils.JwtUtil;
import com.computerapplicationtechnologycnus.sakura_anime.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

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

    /**
     * 验证用户身份
     *
     * @param UserLoginRequest 登录请求对象
     * @return 如果验证通过，返回一个包含 Token 和 userId 的响应；否则返回 null
     */
    public UserLoginResponse authenticateUser(UserLoginRequest UserLoginRequest) {
        // 根据用户名查找用户
        User user = userMapper.findByUsernameIncludePassword(UserLoginRequest.getUsername());
        if (user == null) {
            return null; // 用户不存在
        }
        // 使用 SecurityUtils 比较密码（数据库中存储的是散列后的密码）
        String hashedPassword = securityUtils.sha256Hash(UserLoginRequest.getPassword());
        if (!hashedPassword.equals(user.getPassword())) {
            return null; // 密码错误
        }
        // 使用 JwtUtil 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getPermission());
        // 返回包含 Token 和 userId 的响应
        return new UserLoginResponse(token, user.getId());
    }

    /**
     * 注册用户
     *
     * @return 是否成功
     */
    @Transactional //修改表需要使用事务
    public void register(String email, String username, String password, String displayName, String remarks) throws Exception {
        try {
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
