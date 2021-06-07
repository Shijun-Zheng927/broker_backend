package com.sdu.broker.service;

import com.sdu.broker.pojo.User;

public interface UserService {
    User login(User user);

    Integer register(User user);

    Integer selectPhone(String str);

    Integer setHead(String head, Integer id);

    Integer setPhone(String phone, Integer id);

    Integer setPassword(String password, Integer id);
}
