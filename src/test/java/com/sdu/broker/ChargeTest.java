package com.sdu.broker;

import com.sdu.broker.service.AccountService;
import com.sdu.broker.service.ChargeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChargeTest {
    @Autowired
    private ChargeService chargeService;

    @Test
    public void charge() {
        Integer result = chargeService.operate("haha", 0.0003, "/alsdj", 1);
        System.out.println(result);
    }
}
