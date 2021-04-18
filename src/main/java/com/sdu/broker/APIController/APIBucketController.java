package com.sdu.broker.APIController;

import com.sdu.broker.aliyun.oss.BucketController;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.PlatformService;
import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@CrossOrigin
public class APIBucketController {
    @Autowired
    private BucketController bucketController;
    @Autowired
    private BucketService bucketService;
    @Autowired
    private PlatformService platformService;

    @ResponseBody
    @RequestMapping(value = "/createBucket", method = RequestMethod.POST)
    public String createBucket(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("ALI")) {
            String bucketName = map.get("bucketName");
            String storageClass = map.get("storageClass");
            String dataRedundancyType = map.get("dataRedundancyType");
            String cannedACL = map.get("cannedACL");
            int result;
            if (BucketUtils.regex(0, 4, storageClass) && BucketUtils.regex(0, 1, dataRedundancyType)
                    && BucketUtils.regex(0, 2, cannedACL) && bucketName != null && !bucketName.equals("")) {
                result = bucketController.createBucket(BucketUtils.addPrefix(bucketName), Integer.parseInt(storageClass),
                        Integer.parseInt(dataRedundancyType), Integer.parseInt(cannedACL));
            } else {
                response.setStatus(777);
                return null;
            }
            if (result == 1) {
                Bucket bucket = new Bucket();
                bucket.setName(bucketName);
                bucket.setUserId(userId);
                bucket.setPlatform(platform);
                bucketService.addBucket(bucket);
                return "success";
            } else {
                return "fail";
            }
        } else {
            return "HUAWEI";
        }

    }

    @ResponseBody
    @RequestMapping(value = "/listAllBucket", method = RequestMethod.GET)
    public List<com.aliyun.oss.model.Bucket> listAllBucket(@RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("ALI")) {
            List<com.aliyun.oss.model.Bucket> result = bucketController.listAllBuckets();
            if (result == null) {
                return null;
            }
            return getBuckets(userId, platform, result);
        } else {
            return null;
        }

    }

    @ResponseBody
    @RequestMapping(value = "/listRequestBucket", method = RequestMethod.POST)
    public List<com.aliyun.oss.model.Bucket> listRequestBucket(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("ALI")) {
            String prefix = map.get("prefix");
            if (prefix == null) {
                prefix = "";
            }
            String market = map.get("market");
            if (market == null) {
                market = "";
            }
            String maxKeys = map.get("maxKeys");
            if (!BucketUtils.isNumber(maxKeys)) {
                response.setStatus(777);
                return null;
            }
            List<com.aliyun.oss.model.Bucket> result = bucketController.listRequestBuckets(prefix, market, Integer.parseInt(maxKeys));
            if (result == null) {
                return null;
            }
            return getBuckets(userId, platform, result);
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketInfo", method = RequestMethod.POST)
    public Map<String, String> getBucketInfo(@RequestBody Map<String, String> map,
                                                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        verifyBucketName(response, userId, platform, bucketName);
        if (platform.equals("ALI")) {
            Map<String, String> result = bucketController.getBucketInfo(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketAcl", method = RequestMethod.POST)
    public String getBucketAcl(@RequestBody Map<String, String> map,
                                             @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        verifyBucketName(response, userId, platform, bucketName);
        if (platform.equals("ALI")) {
            String result = bucketController.getBucketAcl(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/setBucketAcl", method = RequestMethod.POST)
    public String setBucketAcl(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        verifyBucketName(response, userId, platform, bucketName);
        if (platform.equals("ALI")) {
            String acl = map.get("acl");
            if (!BucketUtils.regex(0, 3, acl)) {
                response.setStatus(777);
                return null;
            }
            String s = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));
            return s;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteBucket", method = RequestMethod.DELETE)
    public String deleteBucket(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        verifyBucketName(response, userId, platform, bucketName);
        if (platform.equals("ALI")) {
            String result = bucketController.deleteBucket(bucketName);
            if (result.equals("删除存储空间成功")) {
                Bucket bucket = new Bucket(platform, bucketName);
                bucketService.deleteBucket(bucket);
            }
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/setBucketTagging", method = RequestMethod.POST)
    public String setBucketTagging(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        String tagKey = map.get("tagKey");
        String tagValue = map.get("tagValue");
        if (tagKey == null || tagKey.equals("") || tagValue == null || tagValue.equals("")) {
            response.setStatus(777);
            return "format wrong";
        }
        verifyBucketName(response, userId, platform, bucketName);
        if (platform.equals("ALI")) {
            String result = bucketController.setBucketTagging(bucketName, tagKey, tagValue);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketTagging", method = RequestMethod.POST)
    public Map<String,String> getBucketTagging(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        verifyBucketName(response, userId, platform, bucketName);
        if (platform.equals("ALI")) {
            Map<String, String> result = bucketController.getBucketTagging(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listBucketByTag", method = RequestMethod.POST)
    public List<com.aliyun.oss.model.Bucket> listBucketByTag(@RequestBody Map<String, String> map,
                                                             @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        verifyIdentity(response, authorization);
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String tagKey = map.get("tagKey");
        String tagValue = map.get("tagValue");
        if (tagKey == null || tagKey.equals("") || tagValue == null || tagValue.equals("")) {
            response.setStatus(777);
        }
        if (platform.equals("ALI")) {
            List<com.aliyun.oss.model.Bucket> result = bucketController.listBucketByTag(tagKey, tagValue);
            if (result == null) {
                return null;
            }
            return getBuckets(userId, platform, result);
        } else {
            return null;
        }
    }




    public void verifyIdentity(HttpServletResponse response, String token) {
        if (!TokenUtils.verify(token)) {
            response.setStatus(999);
        }
    }

    public void verifyBucketName(HttpServletResponse response, Integer userId, String platform, String bucketName) {
        if (bucketName == null || bucketName.equals("")) {
            response.setStatus(777);
            return;
        }
        Bucket bucket = new Bucket(userId, platform, bucketName);
        Integer legal = bucketService.isLegal(bucket);
        if (legal == null) {
            response.setStatus(666);
        }
    }

    private List<com.aliyun.oss.model.Bucket> getBuckets(Integer userId, String platform, List<com.aliyun.oss.model.Bucket> result) {
        Bucket b = new Bucket();
        b.setId(userId);
        b.setPlatform(platform);
        for (com.aliyun.oss.model.Bucket bucket : result) {
            b.setName(bucket.getName());
            Integer legal = bucketService.isLegal(b);
            if (legal == null) {
                result.remove(bucket);
            }
        }
        return result;
    }
}
