package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.UserMapper;
import com.sdu.broker.pojo.User;
import com.sdu.broker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(User user) {
        return userMapper.login(user);
    }
}
