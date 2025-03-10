package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id,avatar,email,username,permission,display_name,remarks FROM users")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    List<User> findAllUsers(); //用于返回式查询，默认隐藏密码

    @Select("SELECT id,avatar,email,username,permission,display_name,remarks FROM users order by id asc limit #{size} OFFSET #{offset}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    List<User> findUsersUseOffset(@Param("size") Long size, @Param("offset") Long offset); //分页查询，用于返回式查询，默认隐藏密码

    @Select("SELECT * FROM users")
    List<User> findAllUsersIncludePasswords(); //用于管理员查询，【包含密码！需要注意使用】

    // 查询缺失的最小 ID
    @Select("SELECT MIN(t1.id + 1) AS missing_id " +
            "FROM users t1 " +
            "LEFT JOIN users t2 ON t1.id + 1 = t2.id " +
            "WHERE t2.id IS NULL")
    Long findMissingId();

    //普通插入用户
    @Insert("INSERT INTO users (avatar,email,username,permission,password,display_name,remarks) " +
            "VALUES (null,#{email},#{username},#{permission},#{password},#{displayName},#{remarks})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    void insertUser(User user);

    // 手动指定 ID 插入用户
    @Insert("INSERT INTO users (id, avatar, email, username, permission, password, display_name, remarks) " +
            "VALUES (#{id}, null, #{email}, #{username}, #{permission}, #{password}, #{displayName}, #{remarks})")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    void insertUserWithId(User user);

    @Select("SELECT id,avatar,email,username,permission,display_name,remarks from users WHERE id=#{id}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    User findUserDetailByID(Long id);

    @Select("SELECT password from users WHERE username=#{username}")
    String findUserPasskeyByUsername(String username);

    @Select("SELECT id,avatar,email,username,permission,display_name,remarks FROM users WHERE username = #{username}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM users WHERE username = #{username}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    User findByUsernameIncludePassword(@Param("username") String username); //用于管理员查询，【包含密码！需要注意使用】

    @Select("SELECT COUNT(*) FROM users")
    int countUser();

    @Select("SELECT * from users WHERE id=#{id}")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    User findUserDetailByIDIncludePassword(Long id); //用于管理员查询，【包含密码！需要注意使用】

    @Select("SELECT * from users WHERE permission = 0 LIMIT 1")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    User findIfAnyAdminUser(); //查询是否存在任意管理员账户

    @Select("SELECT * from users WHERE username=#{username} OR email=#{email} LIMIT 1")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "avatar",column = "avatar"),
            @Result(property = "email",column = "email"),
            @Result(property = "username",column = "username"),
            @Result(property = "permission",column = "permission"),
            @Result(property = "password",column = "password"),
            @Result(property = "displayName",column = "display_name"),
            @Result(property = "remarks",column = "remarks")
    })
    User findIfExistsUser(@Param("username") String username,@Param("email") String email); //查询是否存在任意账户

    @Select("SELECT id from users WHERE username=#{username}")
    Long findUserIdByUsername(String username); //根据用户名反查UID

    @Update("UPDATE users SET password=#{newpasswd} WHERE id=#{id}")
    void updatePasswordById(@Param("id") Long id,@Param("newpasswd") String newpasswd);

    @Update("UPDATE users SET permission=#{permission} WHERE id=#{id}")
    void updatePermissionById(@Param("id") Long id,@Param("permission") Integer permission);

    @Update("UPDATE users SET email=#{email} WHERE id=#{id}")
    void updateEmailById(@Param("id") Long id,@Param("email") String email);

    @Update("UPDATE users SET username=#{username} WHERE id=#{id}")
    void updateUsernameById(@Param("id") Long id,@Param("username") String username);

    @Update("UPDATE users SET display_name=#{displayName} WHERE id=#{id}")
    void updateDisplayNameById(@Param("id") Long id,@Param("displayName") String displayName);

    @Update("UPDATE users SET remarks=#{remarks} WHERE id=#{id}")
    void updateRemarksById(@Param("id") Long id,@Param("remarks") String remarks);

    @Update("UPDATE users SET avatar=#{avatarFileName} WHERE id=#{id}")
    void updateAvatarById(@Param("id") Long id,@Param("avatarFileName") String avatarFileName);

    // 删除指定用户
    @Delete("DELETE FROM users WHERE id = #{userId}")
    int deleteUserById(@Param("userId") Long userId);

    // 根据用户名删除指定用户
    @Delete("DELETE FROM users WHERE username = #{username}")
    int deleteUserByUsername(String username);


}
