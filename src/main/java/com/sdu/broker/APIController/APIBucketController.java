package com.sdu.broker.APIController;

import com.sdu.broker.aliyun.oss.BucketController;
import com.sdu.broker.huaweiyun.HuaweiController;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.PlatformService;
import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@CrossOrigin
public class APIBucketController {
    @Autowired
    private BucketController bucketController;
    @Autowired
    private BucketService bucketService;
    @Autowired
    private PlatformService platformService;
    @Autowired
    private HuaweiController huaweiController;

    @ResponseBody
    @RequestMapping(value = "/createBucket", method = RequestMethod.POST)
    public String createBucket(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("ALI")) {
            String bucketName = map.get("bucketName");
            String storageClass = map.get("storageClass");
            String dataRedundancyType = map.get("dataRedundancyType");
            String cannedACL = map.get("cannedACL");
            int result;
            if (storageClass == null || dataRedundancyType == null || cannedACL == null || storageClass.equals("") 
                    || dataRedundancyType.equals("") || cannedACL.equals("")) {
                response.setStatus(777);
                return null;
            }
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
            String bucketName = map.get("bucketName");
            String rwPolicy = map.get("rwPolicy");
            String storageClass = map.get("storageClass");
            if (bucketName == null || rwPolicy == null || storageClass == null || bucketName.equals("")
                    || rwPolicy.equals("") || storageClass.equals("")) {
                response.setStatus(777);
                return null;
            }
            int result;
            if (BucketUtils.regex(0, 4, rwPolicy) && BucketUtils.regex(0, 2, storageClass)) {
                result = huaweiController.createBucket(bucketName, Integer.parseInt(rwPolicy), Integer.parseInt(storageClass));
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
        }

    }

    @ResponseBody
    @RequestMapping(value = "/listAllBucket", method = RequestMethod.GET)
    public List<String> listAllBucket(@RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("ALI")) {
            List<com.aliyun.oss.model.Bucket> result = bucketController.listAllBuckets();
            if (result.size() == 0) {
                return null;
            }
            List<com.aliyun.oss.model.Bucket> buckets = getBuckets(userId, platform, result);
            return bucketToString(buckets);
        } else {
            return null;
        }

    }

    @ResponseBody
    @RequestMapping(value = "/listRequestBucket", method = RequestMethod.POST)
    public List<String> listRequestBucket(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
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
            if (result.size() == 0) {
                return null;
            }
            List<com.aliyun.oss.model.Bucket> buckets = getBuckets(userId, platform, result);
            return bucketToString(buckets);
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/doesBucketExist", method = RequestMethod.POST)
    public boolean doesBucketExist(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return false;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return false;
        }
        if (platform.equals("ALI")) {
            boolean result = bucketController.doesBucketExist(bucketName);
            return result;
        } else {
            return false;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketLocation", method = RequestMethod.POST)
    public String getBucketLocation(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("ALI")) {
            String result = bucketController.getBucketLocation(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketInfo", method = RequestMethod.POST)
    public Map<String, String> getBucketInfo(@RequestBody Map<String, String> map,
                                                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
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
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
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
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("ALI")) {
            String acl = map.get("acl");
            if (acl == null || !BucketUtils.regex(0, 3, acl)) {
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
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
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
        if (!verifyIdentity(response, authorization)) {
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
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
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
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("ALI")) {
            Map<String, String> result = bucketController.getBucketTagging(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listBucketByTag", method = RequestMethod.POST)
    public List<String> listBucketByTag(@RequestBody Map<String, String> map,
                                                             @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
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
            List<com.aliyun.oss.model.Bucket> buckets = getBuckets(userId, platform, result);
            return bucketToString(buckets);
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteBucketTagging", method = RequestMethod.DELETE)
    public String deleteBucketTagging(@RequestBody Map<String, String> map,
                                      @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("ALI")) {
            String result = bucketController.deleteBucketTagging(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/setBucketInventoryConfiguration", method = RequestMethod.POST)
    public String setBucketInventoryConfiguration(@RequestBody Map<String, String> map,
                                      @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        String inventoryId = map.get("inventoryId");
        if (inventoryId == null || inventoryId.equals("")) {
            System.out.println(inventoryId);
            response.setStatus(777);
            return null;
        }
        String inventoryFrequency = map.get("inventoryFrequency");
        if (inventoryFrequency == null || !BucketUtils.regex(1, 2, inventoryFrequency)) {
            response.setStatus(777);
            return null;
        }
        String inventoryIncludedObjectVersions = map.get("inventoryIncludedObjectVersions");
        if (inventoryIncludedObjectVersions == null || !BucketUtils.regex(1, 2, inventoryIncludedObjectVersions)) {
            response.setStatus(777);
            return null;
        }
        String isEnabled = map.get("isEnabled");
        if (isEnabled == null || !BucketUtils.regex(0, 1, isEnabled)) {
            response.setStatus(777);
            return null;
        }
        String objPrefix = map.get("objPrefix");
        if (objPrefix == null || objPrefix.equals("")) {
            response.setStatus(777);
            return null;
        }
        String destinationPrefix = map.get("destinationPrefix");
        if (destinationPrefix == null || destinationPrefix.equals("")) {
            response.setStatus(777);
            return null;
        }
        String bucketFormat = map.get("bucketFormat");
        if (bucketFormat == null || !BucketUtils.regex(1, 1, bucketFormat)) {
            response.setStatus(777);
            return null;
        }
        String accountId = map.get("accountId");
        if (accountId == null || accountId.equals("")) {
            response.setStatus(777);
            return null;
        }
        String roleArn = map.get("roleArn");
        if (roleArn == null || roleArn.equals("")) {
            response.setStatus(777);
            return null;
        }
        String destBucketName = map.get("destBucketName");
        if (destBucketName == null || destBucketName.equals("")) {
            response.setStatus(777);
            return null;
        }
        if (platform.equals("ALI")) {
            String result = bucketController.setBucketInventoryConfiguration(bucketName, inventoryId, Integer.parseInt(inventoryFrequency),
                    Integer.parseInt(inventoryIncludedObjectVersions), Integer.parseInt(isEnabled),
                    objPrefix, destinationPrefix, Integer.parseInt(bucketFormat), accountId, roleArn, destBucketName);
            return result;
        } else {
            return null;
        }
    }
    

    public boolean verifyIdentity(HttpServletResponse response, String token) {
        if (!TokenUtils.verify(token)) {
            response.setStatus(999);
            return false;
        }
        return true;
    }

    public boolean verifyBucketName(HttpServletResponse response, Integer userId, String platform, String bucketName) {
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

    private List<com.aliyun.oss.model.Bucket> getBuckets(Integer userId, String platform, List<com.aliyun.oss.model.Bucket> result) {
        Bucket b = new Bucket();
        b.setId(userId);
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
//        for (com.aliyun.oss.model.Bucket bucket : result) {
//            b.setName(bucket.getName());
//            Integer legal = bucketService.isLegal(b);
//            if (legal == null) {
//                result.remove(bucket);
//            }
//        }
        return result;
    }

    private List<String> bucketToString(List<com.aliyun.oss.model.Bucket> list) {
        List<String> result = new ArrayList<>();
        for (com.aliyun.oss.model.Bucket bucket : list) {
            result.add(bucket.toString());
        }
        return result;
    }
}
