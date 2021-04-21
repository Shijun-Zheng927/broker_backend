package com.sdu.broker.APIController;

import com.sdu.broker.aliyun.oss.AliUploadController;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.PlatformService;
import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
@CrossOrigin
public class APIUploadController {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private AliUploadController aliUploadController;
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
            String result = aliUploadController.putString(content, bucketName, objectPath);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/putBytes", method = RequestMethod.POST)
    public String putBytes(@RequestBody Map<String, String> map,
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
            byte[] bytes;
            bytes = content.getBytes(StandardCharsets.UTF_8);
            String result = aliUploadController.putBytes(bytes, bucketName, objectPath);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/putStream", method = RequestMethod.POST)
    public String putStream(@RequestBody Map<String, String> map,
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
            String inputUrl = map.get("inputUrl");
            String objectPath = map.get("objectPath");
            if (inputUrl == null || inputUrl.equals("") || objectPath == null || objectPath.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.putStream(inputUrl, bucketName, objectPath);
            return result;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/putFileStream")
    public String putFileStream(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                                @RequestParam("file") MultipartFile file,
                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String path = "";
        try {
            String filePath = "D:/IDEA/broker/src/main/resources/file/";
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            File f = new File(filePath + fileName);
//                result = "http://localhost:8443/imgs/" + fileName;
            path = filePath + fileName;
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(bn);
        if (platform.equals("ALI")) {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if (objectPath == null || objectPath.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.putFileStream(path, bucketName, objectPath);
            return result;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/putFile")
    public String putFile(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                                @RequestParam("file") MultipartFile file,
                                @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String path = "";
        try {
            String filePath = "D:/IDEA/broker/src/main/resources/file/";
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            File f = new File(filePath + fileName);
//                result = "http://localhost:8443/imgs/" + fileName;
            path = filePath + fileName;
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(bn);
        if (platform.equals("ALI")) {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if (objectPath == null || objectPath.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.putFile(path, bucketName, objectPath);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/appendObjectStreamFirst", method = RequestMethod.POST)
    public String appendObjectStreamFirst(@RequestBody Map<String, String> map,
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
            String objectPath = map.get("objectPath");
            String contentType = map.get("contentType");
            String content = map.get("content");
            if (objectPath == null || objectPath.equals("") || contentType == null || contentType.equals("")
                    || content == null || content.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.appendObjectStreamFirst(bucketName, objectPath, contentType, content);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/appendObjectStream", method = RequestMethod.POST)
    public String appendObjectStream(@RequestBody Map<String, String> map,
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
            String objectPath = map.get("objectPath");
            String contentType = map.get("contentType");
            String content = map.get("content");
            String givenPosition = map.get("givenPosition");
            if (objectPath == null || objectPath.equals("") || contentType == null || contentType.equals("")
                    || content == null || content.equals("") || givenPosition == null || givenPosition.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.appendObjectStream(bucketName, objectPath, contentType, content, givenPosition);
            return result;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/appendObjectFileFirst")
    public String appendObjectFileFirst(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                                        @RequestParam("contentType") String contentType,
                          @RequestParam("file") MultipartFile file,
                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String path = "";
        try {
            String filePath = "D:/IDEA/broker/src/main/resources/file/";
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            File f = new File(filePath + fileName);
//                result = "http://localhost:8443/imgs/" + fileName;
            path = filePath + fileName;
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (platform.equals("ALI")) {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if (objectPath == null || objectPath.equals("") || contentType == null || contentType.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.appendObjectFileFirst(bucketName, objectPath, contentType, path);
            return result;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/appendObjectFile")
    public String appendObjectFile(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                                        @RequestParam("contentType") String contentType,
                                   @RequestParam("givenPosition") String givenPosition,
                                        @RequestParam("file") MultipartFile file,
                                        @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String path = "";
        try {
            String filePath = "D:/IDEA/broker/src/main/resources/file/";
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            File f = new File(filePath + fileName);
//                result = "http://localhost:8443/imgs/" + fileName;
            path = filePath + fileName;
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (platform.equals("ALI")) {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if (objectPath == null || objectPath.equals("") || contentType == null || contentType.equals("") ||
                    givenPosition == null || givenPosition.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.appendObjectFile(bucketName, objectPath, contentType, path, givenPosition);
            return result;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/checkPointUpload")
    public String checkPointUpload(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                                   @RequestParam("contentType") String contentType,
                                   @RequestParam("taskNum") String taskNum, @RequestParam("partSize") String partSize,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String path = "";
        try {
            String filePath = "D:/IDEA/broker/src/main/resources/file/";
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            File f = new File(filePath + fileName);
//                result = "http://localhost:8443/imgs/" + fileName;
            path = filePath + fileName;
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (platform.equals("ALI")) {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if (objectPath == null || objectPath.equals("") || contentType == null || contentType.equals("") ||
                    taskNum == null || taskNum.equals("") || partSize == null || partSize.equals("") ||
                    !BucketUtils.isNumber(taskNum) || !BucketUtils.isNumber(partSize)) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.checkPointUpload(bucketName, objectPath, path, contentType,
                    Integer.parseInt(taskNum), Integer.parseInt(partSize));
            return result;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/multipartUpload")
    public String multipartUpload(@RequestParam("bucketName") String bn, @RequestParam("objectName") String objectName,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String path = "";
        try {
            String filePath = "D:/IDEA/broker/src/main/resources/file/";
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            File f = new File(filePath + fileName);
//                result = "http://localhost:8443/imgs/" + fileName;
            path = filePath + fileName;
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (platform.equals("ALI")) {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if (objectName == null || objectName.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.multipartUpload(bucketName, objectName, path);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/abortMultipartUpload", method = RequestMethod.POST)
    public String abortMultipartUpload(@RequestBody Map<String, String> map,
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
            String objectName = map.get("objectName");
            String uploadId = map.get("uploadId");
            if (objectName == null || objectName.equals("") || uploadId == null || uploadId.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = aliUploadController.abortMultipartUpload(bucketName, objectName, uploadId);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleListParts", method = RequestMethod.POST)
    public List<Map<String,String>> simpleListParts(@RequestBody Map<String, String> map,
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
            String objectName = map.get("objectName");
            String uploadId = map.get("uploadId");
            if (objectName == null || objectName.equals("") || uploadId == null || uploadId.equals("")) {
                response.setStatus(777);
                return null;
            }
            List<Map<String,String>> result = aliUploadController.simpleListParts(bucketName, objectName, uploadId);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listPartsAll", method = RequestMethod.POST)
    public List<Map<String,String>> listPartsAll(@RequestBody Map<String, String> map,
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
            String objectName = map.get("objectName");
            String uploadId = map.get("uploadId");
            if (objectName == null || objectName.equals("") || uploadId == null || uploadId.equals("")) {
                response.setStatus(777);
                return null;
            }
            List<Map<String,String>> result = aliUploadController.listPartsAll(bucketName, objectName, uploadId);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listPartsByPaper", method = RequestMethod.POST)
    public List<Map<String,String>> listPartsByPaper(@RequestBody Map<String, String> map,
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
    public List<Map<String,String>> simpleListMultipartUploads(@RequestBody Map<String, String> map,
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
            List<Map<String, String>> result = aliUploadController.simpleListMultipartUploads(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listMultipartUploads", method = RequestMethod.POST)
    public List<Map<String,String>> listMultipartUploads(@RequestBody Map<String, String> map,
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
            List<Map<String, String>> result = aliUploadController.listMultipartUploads(bucketName);
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/listMultipartUploadsByPapper", method = RequestMethod.POST)
    public List<Map<String,String>> listMultipartUploadsByPapper(@RequestBody Map<String, String> map,
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
            String maxUploads = map.get("maxUploads");
            if (maxUploads == null || maxUploads.equals("") || !BucketUtils.isNumber(maxUploads)) {
                response.setStatus(777);
                return null;
            }
            List<Map<String, String>> result = aliUploadController.listMultipartUploadsByPapper(bucketName, Integer.parseInt(maxUploads));
            return result;
        } else {
            return null;
        }
    }



//    @RequestMapping(value = "/puttest")
//    public String puttest(@RequestParam("bucketName") String bn, @RequestParam("file") MultipartFile file,
//                            @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
//        if (!verifyIdentity(response, authorization)) {
//            return null;
//        }
//        if (file == null) {
//            System.out.println("file is null");
//            return null;
//        }
//        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
//        try {
//            String filePath = "D:/IDEA/broker/src/main/resources/file/";
//            String uuid = UUID.randomUUID().toString();
//            String fileName = uuid + file.getOriginalFilename();
//            File f = new File(filePath + fileName);
////                result = "http://localhost:8443/imgs/" + fileName;
//            String path = filePath + fileName;
//            file.transferTo(f);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(bn);
//        if (platform.equals("ALI")) {
//            String bucketName = bn;
//            if (verifyBucketName(response, userId, platform, bucketName)) {
//                return null;
//            }
//            System.out.println(bucketName);
//            return null;
//        } else {
//            return null;
//        }
//
//    }





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
