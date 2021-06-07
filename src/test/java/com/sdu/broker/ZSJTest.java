package com.sdu.broker;

import com.sdu.broker.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZSJTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void test1() {
        accountService.recharge("1", 30.0, "");
    }

    @Test
    public void test2() {
        System.out.println(UUID.randomUUID());
    }
}
