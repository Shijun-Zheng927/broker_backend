package com.sdu.broker.controller;

import com.sdu.broker.pojo.User;
import com.sdu.broker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping(value = "/login")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    public User login(@RequestBody User user) {
        return userService.login(user);
    }

    @CrossOrigin
    @PostMapping(value = "/register")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer register(@RequestBody User user) {
        return userService.register(user);
    }
}
