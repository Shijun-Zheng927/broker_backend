package com.sdu.broker.controller;

import com.sdu.broker.service.BucketService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@RestController
public class BucketController {
    @Autowired
    private BucketService bucketService;

    @CrossOrigin
    @RequestMapping("/getPlatform")
    public String getPlatform(@RequestBody Map<String, String> map,
                              @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getUpload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        return platform;
    }

    //工具类
    public boolean verifyIdentity(HttpServletResponse response, String token) {
        if (!TokenUtils.verify(token)) {
            response.setStatus(999);
            return false;
        }
        return true;
    }

    public boolean verify(HttpServletResponse response, Integer userId, String bucketName) {
        if ("".equals(bucketName)) {
            response.setStatus(777);
            return true;
        }
//        Bucket bucket = new Bucket(userId, platform, bucketName);
        Integer legal = bucketService.verify(userId.toString(), bucketName);
        if (legal == null) {
            response.setStatus(666);
            return true;
        }
        return false;
    }
}
