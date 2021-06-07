package com.sdu.broker;

import com.sdu.broker.mapper.HistoryMapper;
import com.sdu.broker.pojo.History;
import com.sdu.broker.service.AccountService;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.ChargeService;
import com.sdu.broker.service.HistoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChargeTest {
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private HistoryMapper historyMapper;
    @Autowired
    private BucketService bucketService;
    @Autowired
    private HistoryService historyService;

    @Test
    public void charge() {
        Integer result = chargeService.operate("haha", 0.0003, "/alsdj", 1, "upload");
        System.out.println(result);
    }

    @Test
    public void selectTime() {
        Integer result = historyMapper.selectTime("2021-05-31 22:22:00", "2021-05-31 22:24:00");
        System.out.println(result);
    }

    @Test
    public void haveName() {
        Integer result = bucketService.haveName("jklsj");
        System.out.println(result);
    }


}
