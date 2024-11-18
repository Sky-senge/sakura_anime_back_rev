package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.computerapplicationtechnologycnus.sakura_anime.mapper.UserMapper;
import com.computerapplicationtechnologycnus.sakura_anime.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper){
        this.userMapper=userMapper;
    }

    public List<User> getAllUsers(){
        return userMapper.findAllUsers();
    }
}
