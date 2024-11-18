package com.computerapplicationtechnologycnus.sakura_anime.mapper;

import com.computerapplicationtechnologycnus.sakura_anime.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id,avatar,email,username,permission,display_name,remarks FROM users")
    List<User> findAllUsers(); //用于返回式查询，默认隐藏密码

    @Select("SELECT * FROM users")
    List<User> findAllUsersIncludePasswords(); //用于管理员查询，【包含密码！需要注意使用】

    @Select("SELECT id,avatar,email,username,permission,display_name,remarks from users WHERE id=#{id}")
    User findUserDetailByID(Long id);

    @Select("SELECT * from users WHERE id=#{id}")
    User findUserDetailByIDIncludePassword(Long id); //用于管理员查询，【包含密码！需要注意使用】

    @Update("UPDATE users SET password=#{newpasswd} WHERE id=#{id}")
    void updatePasswordById(Long id,String newpasswd);

    @Update("UPDATE users SET permission=#{permission} WHERE id=#{id}")
    void updatePermissionById(Long id,Integer permission);

    @Update("UPDATE users SET email=#{email} WHERE id=#{id}")
    void updateEmailById(Long id,String email);

    @Update("UPDATE users SET username=#{username} WHERE id=#{id}")
    void updateUsernameById(Long id,String username);

    @Update("UPDATE users SET display_name=#{displayName} WHERE id=#{id}")
    void updateDisplayNameById(Long id,String displayName);

    @Update("UPDATE users SET remarks=#{remarks} WHERE id=#{id}")
    void updateRemarksById(Long id,String remarks);


}
