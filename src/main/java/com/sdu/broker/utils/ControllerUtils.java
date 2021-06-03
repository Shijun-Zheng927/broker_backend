package com.sdu.broker.utils;

import com.obs.services.model.ObsBucket;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ControllerUtils {
    @Autowired
    private static BucketService bucketService;

    public static boolean verifyIdentity(HttpServletResponse response, String token) {
        if (!TokenUtils.verify(token)) {
            response.setStatus(999);
            return false;
        }
        return true;
    }

    public static boolean verifyBucketName(HttpServletResponse response, Integer userId, String platform, String bucketName) {
        if (bucketName == null || bucketName.equals("")) {
            response.setStatus(777);
            return true;
        }
        Bucket bucket = new Bucket(userId, platform, bucketName);
        Integer legal = bucketService.isLegal(bucket);
        if (legal == null) {
            response.setStatus(666);
            return true;
        }
        return false;
    }

    public static List<com.aliyun.oss.model.Bucket> getBucketsAli(Integer userId, String platform, List<com.aliyun.oss.model.Bucket> result) {
        Bucket b = new Bucket();
        b.setUserId(userId);
        b.setPlatform(platform);
        Iterator<com.aliyun.oss.model.Bucket> iterator = result.iterator();
        while (iterator.hasNext()) {
            com.aliyun.oss.model.Bucket bucket = iterator.next();
            b.setName(bucket.getName());
            Integer legal = bucketService.isLegal(b);
            if (legal == null) {
                iterator.remove();
            }
        }
        return result;
    }

    public static List<ObsBucket> getBucketsHuawei(Integer userId, String platform, List<ObsBucket> result) {
        Bucket b = new Bucket();
        b.setUserId(userId);
        b.setPlatform(platform);
        Iterator<ObsBucket> iterator = result.iterator();
        while (iterator.hasNext()) {
            ObsBucket bucket = iterator.next();
            b.setName(bucket.getBucketName());
            Integer legal = bucketService.isLegal(b);
            if (legal == null) {
                iterator.remove();
            }
        }
        return result;
    }

    public static List<String> bucketToStringAli(List<com.aliyun.oss.model.Bucket> list) {
        List<String> result = new ArrayList<>();
        for (com.aliyun.oss.model.Bucket bucket : list) {
            result.add(bucket.toString());
        }
        return result;
    }
}
