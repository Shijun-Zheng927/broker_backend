package com.sdu.broker.controller;

import com.sdu.broker.pojo.User;
import com.sdu.broker.service.UserService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param user
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/login")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<String, Object> login(@RequestBody User user) {
        User login = userService.login(user);
        if (login == null) {
            return null;
        } else {
            String token = TokenUtils.sign(login.getId().toString());
            Map<String, Object> map = new HashMap<>();
            map.put("phone", login.getPhone());
            map.put("username", login.getUsername());
            map.put("token", token);
            return map;
        }

    }

    /**
     * 注册
     * @param user
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/register")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer register(@RequestBody User user) {
        return userService.register(user);
    }
}
