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
        if (!TokenUtils.verify(authorization)) {
            response.setStatus(999);
            return "no";
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("ALI")) {
            String bucketName = map.get("bucketName");
            String storageClass = map.get("storageClass");
            String dataRedundancyType = map.get("dataRedundancyType");
            String cannedACL = map.get("cannedACL");
            System.out.println(BucketUtils.regex(0, 4, storageClass));
            System.out.println(BucketUtils.regex(0, 1, dataRedundancyType));
            System.out.println(BucketUtils.regex(0, 2, cannedACL));
            System.out.println(bucketName != null && !bucketName.equals(""));
            int result;
            if (BucketUtils.regex(0, 4, storageClass) && BucketUtils.regex(0, 1, dataRedundancyType)
                    && BucketUtils.regex(0, 2, cannedACL) && bucketName != null && !bucketName.equals("")) {
                result = bucketController.createBucket(BucketUtils.addPrefix(bucketName), Integer.parseInt(storageClass),
                        Integer.parseInt(dataRedundancyType), Integer.parseInt(cannedACL));
            } else {
                return "format wrong";
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
}
