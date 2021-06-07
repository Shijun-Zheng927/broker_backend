package com.sdu.broker;

import com.sdu.broker.mapper.HistoryMapper;
import com.sdu.broker.pojo.History;
import com.sdu.broker.service.HistoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HistoryTest {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private HistoryMapper historyMapper;

    @Test
    public void getHistory() {
        List<History> history = historyService.getHistory(1);
        for (History h : history) {
            System.out.println(h);
        }
    }

    @Test
    public void getUpload() {
        Double result = historyMapper.getUpload(1);
        System.out.println(result);
    }

    @Test
    public void getUploads() {
        String result = historyService.getDownload(12);
        System.out.println(result);
    }

    @Test
    public void getFlow() {
        Map<String, String> result = historyService.getBucketFlow("hh");
        System.out.println(result.get("upload"));
        System.out.println(result.get("download"));
    }
}
