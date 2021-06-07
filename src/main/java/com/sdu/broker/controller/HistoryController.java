package com.sdu.broker.controller;

import com.sdu.broker.pojo.RechargeRecord;
import com.sdu.broker.service.HistoryService;
import com.sdu.broker.service.RechargeRecordService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class HistoryController {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RechargeRecordService rechargeRecordService;

    @CrossOrigin
    @RequestMapping("/getUpload")
    public String getUpload(@RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getUpload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String result = historyService.getUpload(userId);
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getDownload")
    public String getDownload(@RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getDownload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String result = historyService.getDownload(userId);
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getBucketFlow")
    public Map<String, String> getBucketFlow(@RequestBody Map<String, String> map,
                                             @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getBucketFlow");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        String bucketName = map.get("bucketName");
//        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        Map<String, String> result = historyService.getBucketFlow(bucketName);
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getRecharge")
    public List<RechargeRecord> getRecharge(@RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getRecharge");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        List<RechargeRecord> result = rechargeRecordService.getRecord(userId.toString());
        return result;
    }


    //工具类
    public boolean verifyIdentity(HttpServletResponse response, String token) {
        if (!TokenUtils.verify(token)) {
            response.setStatus(999);
            return false;
        }
        return true;
    }
}
