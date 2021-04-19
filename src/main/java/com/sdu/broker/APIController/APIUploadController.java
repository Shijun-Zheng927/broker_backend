package com.sdu.broker.APIController;

import com.sdu.broker.aliyun.oss.UpdateController;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.PlatformService;
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
public class APIUploadController {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private UpdateController updateController;
    @Autowired
    private PlatformService platformService;

    @ResponseBody
    @RequestMapping(value = "/putString", method = RequestMethod.POST)
    public String putString(@RequestBody Map<String, String> map,
                                  @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("ALI")) {
            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String content = map.get("content");
            String objectPath = map.get("objectPath");
            if (content == null || content.equals("") || objectPath == null || objectPath.equals("")) {
                response.setStatus(777);
                return null;
            }
            String s = updateController.putString(content, bucketName, objectPath);
            return s;
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
}
