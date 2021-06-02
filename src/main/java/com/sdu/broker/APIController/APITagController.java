package com.sdu.broker.APIController;

import com.obs.services.model.ObsBucket;
import com.sdu.broker.aliyun.oss.BucketController;
import com.sdu.broker.huaweiyun.HuaweiTagController;
import com.sdu.broker.service.PlatformService;
import com.sdu.broker.utils.ControllerUtils;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@CrossOrigin
public class APITagController {
    @Autowired
    private PlatformService platformService;
    @Autowired
    private BucketController bucketController;
    private HuaweiTagController huaweiTagController = new HuaweiTagController();


    @ResponseBody
    @RequestMapping(value = "/setBucketTagging", method = RequestMethod.POST)
    public String setBucketTagging(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!ControllerUtils.verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        String tagKey = map.get("tagKey");
        String tagValue = map.get("tagValue");
        if (tagKey == null || tagKey.equals("") || tagValue == null || tagValue.equals("")) {
            response.setStatus(777);
            return "format wrong";
        }
        if (ControllerUtils.verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("ALI")) {
            String result = bucketController.setBucketTagging(bucketName, tagKey, tagValue);
            return result;
        } else {
            String result = huaweiTagController.setOneTag(bucketName, tagKey, tagValue);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketTagging", method = RequestMethod.POST)
    public Map<String,String> getBucketTagging(@RequestBody Map<String, String> map,
                                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!ControllerUtils.verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (ControllerUtils.verifyBucketName(response, userId, platform, bucketName)) {
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
    @RequestMapping(value = "/listBucketByTag", method = RequestMethod.POST)
    public List<String> listBucketByTag(@RequestBody Map<String, String> map,
                                        @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!ControllerUtils.verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String tagKey = map.get("tagKey");
        String tagValue = map.get("tagValue");
        if (tagKey == null || tagKey.equals("") || tagValue == null || tagValue.equals("")) {
            response.setStatus(777);
        }
        if (platform.equals("ALI")) {
            List<com.aliyun.oss.model.Bucket> result = bucketController.listBucketByTag(tagKey, tagValue);
            if (result.size() == 0) {
                return null;
            }
            List<com.aliyun.oss.model.Bucket> buckets = ControllerUtils.getBucketsAli(userId, platform, result);
            return ControllerUtils.bucketToStringAli(buckets);
        } else {
            List<ObsBucket> result0 = huaweiTagController.listBucketByTag(tagKey, tagValue);
            if (result0.size() == 0) {
                return null;
            }
            List<String> result = new ArrayList<>();
            for (ObsBucket o : result0){
                result.add(o.getBucketName());
            }
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteBucketTagging", method = RequestMethod.DELETE)
    public String deleteBucketTagging(@RequestBody Map<String, String> map,
                                      @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!ControllerUtils.verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (ControllerUtils.verifyBucketName(response, userId, platform, bucketName)) {
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
}
