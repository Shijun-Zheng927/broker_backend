package com.sdu.broker.APIController;

import com.aliyun.oss.model.BucketInfo;
import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.ObsBucket;
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
            List<com.aliyun.oss.model.Bucket> buckets = getBucketsAli(userId, platform, result);
            return bucketToStringAli(buckets);
        } else {
            List<ObsBucket> result = huaweiController.listBucket();
            if (result.size() == 0) {
                return null;
            }
            List<ObsBucket> buckets = getBucketHuawei(userId, platform, result);
            return bucketToStringHuawei(buckets);
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
            List<com.aliyun.oss.model.Bucket> buckets = getBucketsAli(userId, platform, result);
            return bucketToStringAli(buckets);
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
        boolean result;
        if (platform.equals("ALI")) {
            result = bucketController.doesBucketExist(bucketName);
        } else {
            result = huaweiController.existBucket(bucketName);
        }
        return result;
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
        String result;
        if (platform.equals("ALI")) {
            result = bucketController.getBucketLocation(bucketName);
        } else {
            result = huaweiController.getlocation(bucketName);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketInfo", method = RequestMethod.POST)
    public Object getBucketInfo(@RequestBody Map<String, String> map,
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
            BucketInfo result = bucketController.getBucketInfo(bucketName);
            return result;
        } else {
            BucketMetadataInfoResult result = huaweiController.getresult(bucketName);
            return result;
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
        String result;
        if (platform.equals("ALI")) {
            result = bucketController.getBucketAcl(bucketName);
        } else {
            result = huaweiController.getBucketAcl(bucketName);
        }
        return result;
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
            String rwPolicy = map.get("rwPolicy");
            if (rwPolicy == null || !BucketUtils.regex(0, 4, rwPolicy)) {
                response.setStatus(777);
                return null;
            }
            huaweiController.setBucketAcl(bucketName, Integer.parseInt(rwPolicy));
            return "success";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/setBucketPolicy", method = RequestMethod.POST)
    public String setBucketPolicy(@RequestBody Map<String, String> map,
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
        if (platform.equals("HUAWEI")) {
            String policy = map.get("policy");
            if (policy == null || policy.equals("")) {
                response.setStatus(777);
                return null;
            }
            huaweiController.setBucketPolicy(bucketName, policy);
            return "success";
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketPolicy", method = RequestMethod.POST)
    public String getBucketPolicy(@RequestBody Map<String, String> map,
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
        if (platform.equals("HUAWEI")) {
            String result = huaweiController.getBucketPolicy(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteBucketPolicy", method = RequestMethod.POST)
    public String deleteBucketPolicy(@RequestBody Map<String, String> map,
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
        if (platform.equals("HUAWEI")) {
            huaweiController.deleteBucketPolicy(bucketName);
            return "success";
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketStorageInfo", method = RequestMethod.POST)
    public Map<String, String> getBucketStorageInfo(@RequestBody Map<String, String> map,
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
        if (platform.equals("HUAWEI")) {
            Map<String, String> result = huaweiController.getBucketStorageInfo(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/setBucketQuota", method = RequestMethod.POST)
    public String setBucketQuota(@RequestBody Map<String, String> map,
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
        if (platform.equals("HUAWEI")) {
            String size = map.get("size");
            if (size == null || !BucketUtils.isNumber(size)) {
                response.setStatus(777);
                return null;
            }
            huaweiController.setBucketQuota(bucketName, Long.parseLong(size));
            return "success";
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketQuota", method = RequestMethod.POST)
    public String getBucketQuota(@RequestBody Map<String, String> map,
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
        if (platform.equals("HUAWEI")) {
            long result = huaweiController.getBucketQuota(bucketName);
            return Long.toString(result);
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/setBucketStoragePolicy", method = RequestMethod.POST)
    public String setBucketStoragePolicy(@RequestBody Map<String, String> map,
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
        if (platform.equals("HUAWEI")) {
            String storageClass = map.get("storageClass");
            if (storageClass == null || !BucketUtils.regex(0, 2, storageClass)) {
                response.setStatus(777);
                return null;
            }
            huaweiController.setBucketStoragePolicy(bucketName, Integer.parseInt(storageClass));
            return "success";
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketStorageClass", method = RequestMethod.POST)
    public String getBucketStorageClass(@RequestBody Map<String, String> map,
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
        if (platform.equals("HUAWEI")) {
            String result = huaweiController.getBucketStorageClass(bucketName);
            return result;
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
            int result = huaweiController.removeBucket(bucketName);
            if (result == 1) {
                Bucket bucket = new Bucket(platform, bucketName);
                bucketService.deleteBucket(bucket);
                return "删除存储空间成功";
            } else {
                return "失败";
            }
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

    private List<com.aliyun.oss.model.Bucket> getBucketsAli(Integer userId, String platform, List<com.aliyun.oss.model.Bucket> result) {
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

    private List<ObsBucket> getBucketHuawei(Integer userId, String platform, List<ObsBucket> result) {
        Bucket b = new Bucket();
        b.setId(userId);
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
}
