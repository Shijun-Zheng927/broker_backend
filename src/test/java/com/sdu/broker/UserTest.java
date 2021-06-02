package com.sdu.broker;

import com.sdu.broker.pojo.User;
import com.sdu.broker.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {
    @Autowired
    private UserService userService;

    @Test
    public void headTest() {
        userService.setHead("http://localhost:8443/head/groot.jpg", 1);
    }

    @Test
    public void login() {
        User user = new User();
        user.setPhone("15662651871");
        user.setPassword("666");
        User login = userService.login(user);
        System.out.println(login);
    }
}
