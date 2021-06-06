package com.sdu.broker.APIController;

import com.obs.services.model.ObsBucket;
import com.sdu.broker.aliyun.oss.BucketController;
import com.sdu.broker.huaweiyun.HuaweiTagController;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.PlatformService;
import com.sdu.broker.utils.ControllerUtils;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@CrossOrigin
public class APITagController {
    @Autowired
    private PlatformService platformService;
    @Autowired
    private BucketController bucketController;
    @Autowired
    private HuaweiTagController huaweiTagController;
    @Autowired
    private BucketService bucketService;

    @ResponseBody
    @RequestMapping(value = "/setBucketTagging", method = RequestMethod.POST)
    public String setBucketTagging(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("setBucketTagging");
        if (!ControllerUtils.verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        String tagKey = map.get("tagKey");
        String tagValue = map.get("tagValue");
        if (tagKey == null || tagKey.equals("") || tagValue == null || tagValue.equals("")) {
            response.setStatus(777);
            return "format wrong";
        }
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("ALI")) {
            String result = bucketController.setBucketTagging(bucketName,tagKey, tagValue);
            return result;
        } else {
            String result = huaweiTagController.setOneTag(bucketName, tagKey, tagValue);
            return result;
        }
    }

    @ResponseBody
//    @RequestMapping(value = "/getBucketTagging", method = RequestMethod.POST)
    @GetMapping(value = "/getBucketTagging", params = {"bucketName"})
    public Map<String, String> getBucketTagging(@RequestParam String bucketName,
                                                @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getBucketTagging");
        if (!ControllerUtils.verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
//        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("ALI")) {
            Map<String, String> result = bucketController.getBucketTagging(bucketName);
            return result;
        } else {
            Map<String, String> result = huaweiTagController.getBucketTagging(bucketName);
            return result;
        }
    }

    @ResponseBody
//    @RequestMapping(value = "/listBucketByTag", method = RequestMethod.POST)
    @GetMapping(value = "/listBucketByTag", params = {"tagKey", "tagValue"})
    public List<String> listBucketByTag(@RequestParam String tagKey, @RequestParam String tagValue,
                                        @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("listBucketByTag");
        if (!ControllerUtils.verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);

//        String platform = map.get("bucketName");
//        String tagKey = map.get("tagKey");
//        String tagValue = map.get("tagValue");
        if (tagKey == null || tagKey.equals("") || tagValue == null || tagValue.equals("")) {
            response.setStatus(777);
        }

        List<String> allBuckets = new ArrayList<>();

        List<com.aliyun.oss.model.Bucket> result = bucketController.listBucketByTag(tagKey, tagValue);
        System.out.println(result.size());
//        if (result.size() == 0) {
//            return null;
//        }
        List<com.aliyun.oss.model.Bucket> buckets = getBucketsAli(userId, "ALI", result);


        List<ObsBucket> result0 = huaweiTagController.listBucketByTag(tagKey, tagValue);
        System.out.println(result0.size());
//        if (result0.size() == 0) {
//            return null;
//        }
        List<ObsBucket> huawei = getBucketsHuawei(userId, "HUAWEI", result0);

        if (buckets != null) {
            allBuckets.addAll(bucketToStringAli(buckets));
        }
        if (huawei != null) {
            allBuckets.addAll(bucketToStringHuawei(huawei));
        }
        return allBuckets;

    }

    @ResponseBody
    @RequestMapping(value = "/deleteBucketTagging", method = RequestMethod.DELETE)
    public String deleteBucketTagging(@RequestBody Map<String, String> map,
                                      @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("deleteBucketTagging");
        if (!ControllerUtils.verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("ALI")) {
            String result = bucketController.deleteBucketTagging(bucketName);
            return result;
        } else {
            String result = huaweiTagController.deleteBucketTagging(bucketName);
            return result;
        }

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

    private List<String> bucketToStringAli(List<com.aliyun.oss.model.Bucket> list) {
        List<String> result = new ArrayList<>();
        for (com.aliyun.oss.model.Bucket bucket : list) {
            result.add(bucket.toString());
        }
        return result;
    }

    private List<String> bucketToStringHuawei(List<ObsBucket> list) {
        List<String> result = new ArrayList<>();
        for (ObsBucket bucket : list) {
            result.add(bucket.toString());
        }
        return result;
    }

    public boolean verifyBucketName(HttpServletResponse response, Integer userId, String platform, String bucketName) {
        if (bucketName == null || bucketName.equals("")) {
            response.setStatus(777);
            return true;
        }
        Bucket bucket = new Bucket(userId, platform, bucketName);
        System.out.println(userId);
        System.out.println(platform);
        System.out.println(bucketName);
        Integer legal = bucketService.isLegal(bucket);
        if (legal == null) {
            response.setStatus(666);
            return true;
        }
        return false;
    }

    public List<com.aliyun.oss.model.Bucket> getBucketsAli(Integer userId, String platform, List<com.aliyun.oss.model.Bucket> result) {
        if (result.size() == 0) {
            return null;
        }
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

    public List<ObsBucket> getBucketsHuawei(Integer userId, String platform, List<ObsBucket> result) {
        if (result.size() == 0) {
            return null;
        }
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
}
