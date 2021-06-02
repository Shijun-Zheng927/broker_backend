package com.sdu.broker.service.impl;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.sdu.broker.mapper.AccountMapper;
import com.sdu.broker.mapper.UserMapper;
import com.sdu.broker.pojo.User;
import com.sdu.broker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AccountMapper accountMapper;

    @Override
    public User login(User user) {
        return userMapper.login(user);
    }

    @Override
    public Integer register(User user) {
        Integer hasPhone = userMapper.selectPhone(user.getPhone());
//        System.out.println(hasPhone);
        if (hasPhone == null) {
            user.setHead("null");
            userMapper.register(user);
            accountMapper.register(user.getId());
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public Integer setHead(String head, Integer id) {
        return userMapper.setHead(head, id);
    }
}
