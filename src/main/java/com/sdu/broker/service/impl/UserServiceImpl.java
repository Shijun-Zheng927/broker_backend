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
            user.setHead("http://192.168.1.109:8443/head/0b11580a-3330-4150-a7f5-aae807536589logo.png");
            userMapper.register(user);
            accountMapper.register(user.getId());
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public Integer selectPhone(String str) {
        return userMapper.selectPhone(str);
    }

    @Override
    public Integer setHead(String head, Integer id) {
        return userMapper.setHead(head, id);
    }

    @Override
    public Integer setPhone(String phone, Integer id) {
        Integer hasPhone = userMapper.selectPhone(id.toString());
//        System.out.println(hasPhone);
        if (hasPhone == null) {
            Integer result = userMapper.setPhone(phone, id);
            return result;
        }
        return null;
    }

    @Override
    public Integer setPassword(String password, Integer id) {
        Integer result = userMapper.setPassword(password, id);
        return result;
    }
}
