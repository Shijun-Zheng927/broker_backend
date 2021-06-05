package com.sdu.broker.APIController;

import com.sdu.broker.aliyun.oss.AliUploadController;
import com.sdu.broker.huaweiyun.HuaweiUploadController;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.PlatformService;
import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@CrossOrigin
public class APIUploadSelectController {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private AliUploadController aliUploadController;
    @Autowired
    private HuaweiUploadController huaweiUploadController;
    @Autowired
    private PlatformService platformService;

    @ResponseBody
    @RequestMapping(value = "/simpleListParts", method = RequestMethod.POST)
    public List<Map<String, String>> simpleListParts(@RequestBody Map<String, String> map,
                                                     @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("simpleListParts");
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
            String objectName = map.get("objectName");
            String uploadId = map.get("uploadId");
            if (objectName == null || objectName.equals("") || uploadId == null || uploadId.equals("")) {
                response.setStatus(777);
                return null;
            }
            List<Map<String, String>> result = aliUploadController.simpleListParts(bucketName, objectName, uploadId);
            return result;
        } else {
            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String objectName = map.get("objectName");
            String uploadId = map.get("uploadId");
            if (objectName == null || objectName.equals("") || uploadId == null || uploadId.equals("")) {
                response.setStatus(777);
                return null;
            }
            List<Map<String, String>> result = huaweiUploadController.simpleListPart(bucketName, objectName, uploadId);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listPartsAll", method = RequestMethod.POST)
    public List<Map<String, String>> listPartsAll(@RequestBody Map<String, String> map,
                                                  @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("listPartsAll");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        String platform = bucketService.getPlatform(bucketName);
        if (platform == null) {
            response.setStatus(666);
            return null;
        }
        if (platform.equals("ALI")) {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String objectName = map.get("objectName");
            String uploadId = map.get("uploadId");
            if (objectName == null || objectName.equals("") || uploadId == null || uploadId.equals("")) {
                response.setStatus(777);
                return null;
            }
            List<Map<String, String>> result = aliUploadController.listPartsAll(bucketName, objectName, uploadId);
            return result;
        } else {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String objectName = map.get("objectName");
            String uploadId = map.get("uploadId");
            if (objectName == null || objectName.equals("") || uploadId == null || uploadId.equals("")) {
                response.setStatus(777);
                return null;
            }
            List<Map<String, String>> result = huaweiUploadController.listPartsAll(bucketName, objectName, uploadId);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listPartsByPaper", method = RequestMethod.POST)
    public List<Map<String, String>> listPartsByPaper(@RequestBody Map<String, String> map,
                                                      @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("listPartsByPaper");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        String platform = bucketService.getPlatform(bucketName);
        if (platform == null) {
            response.setStatus(666);
            return null;
        }
        if (platform.equals("ALI")) {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String objectName = map.get("objectName");
            String uploadId = map.get("uploadId");
            String maxParts = map.get("maxParts");
            String marker = map.get("marker");
            if (objectName == null || objectName.equals("") || uploadId == null || uploadId.equals("") || maxParts == null ||
                    maxParts.equals("") || marker == null || marker.equals("") || !BucketUtils.isNumber(maxParts) ||
                    !BucketUtils.isNumber(marker)) {
                response.setStatus(777);
                return null;
            }
            List<Map<String, String>> result = aliUploadController.listPartsByPaper(bucketName, objectName, uploadId,
                    Integer.parseInt(maxParts), Integer.parseInt(marker));
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleListMultipartUploads", method = RequestMethod.POST)
    public List<Map<String, String>> simpleListMultipartUploads(@RequestBody Map<String, String> map,
                                                                @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("simpleListMultipartUploads");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        String platform = bucketService.getPlatform(bucketName);
        if (platform == null) {
            response.setStatus(666);
            return null;
        }
        if (platform.equals("ALI")) {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            List<Map<String, String>> result = aliUploadController.simpleListMultipartUploads(bucketName);
            return result;
        } else {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            List<Map<String, String>> result = huaweiUploadController.simpleListMultipartUploads(bucketName);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listMultipartUploads", method = RequestMethod.POST)
    public List<Map<String, String>> listMultipartUploads(@RequestBody Map<String, String> map,
                                                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("listMultipartUploads");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        String platform = bucketService.getPlatform(bucketName);
        if (platform == null) {
            response.setStatus(666);
            return null;
        }
        if (platform.equals("ALI")) {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            List<Map<String, String>> result = aliUploadController.listMultipartUploads(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listMultipartUploadsByPapper", method = RequestMethod.POST)
    public List<Map<String, String>> listMultipartUploadsByPapper(@RequestBody Map<String, String> map,
                                                                  @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("listMultipartUploadsByPapper");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        String platform = bucketService.getPlatform(bucketName);
        if (platform == null) {
            response.setStatus(666);
            return null;
        }
        if (platform.equals("ALI")) {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String maxUploads = map.get("maxUploads");
            if (maxUploads == null || maxUploads.equals("") || !BucketUtils.isNumber(maxUploads)) {
                response.setStatus(777);
                return null;
            }
            List<Map<String, String>> result = aliUploadController.listMultipartUploadsByPapper(bucketName, Integer.parseInt(maxUploads));
            return result;
        } else {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String maxUploads = map.get("maxUploads");
            if (maxUploads == null || maxUploads.equals("") || !BucketUtils.isNumber(maxUploads)) {
                response.setStatus(777);
                return null;
            }
            List<Map<String, String>> result = huaweiUploadController.listMultipartUploadsByPapper(bucketName);
            return result;
        }
    }

//    @RequestMapping(value = "/testc", method = RequestMethod.POST)
//    @ResponseBody
//    public String testc(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
//        if (file == null) {
//            System.out.println("jfdlsaj");
//        } else {
//            System.out.println("sjdf");
//        }
//        System.out.println(request.getParameter("a"));
//        System.out.println(request.getParameter("b"));
//        return "su";
//    }


    //工具方法
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
