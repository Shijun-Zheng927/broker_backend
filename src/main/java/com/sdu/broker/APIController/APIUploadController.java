package com.sdu.broker.APIController;

import com.obs.services.model.PartEtag;
import com.sdu.broker.aliyun.oss.AliUploadController;
import com.sdu.broker.huaweiyun.HuaweiUploadController;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.pojo.req.CompleteMultipartUpload;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.ChargeService;
import com.sdu.broker.service.PlatformService;
import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.FileUtils;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@CrossOrigin
public class APIUploadController {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private AliUploadController aliUploadController;
    @Autowired
    private HuaweiUploadController huaweiUploadController;
    @Autowired
    private PlatformService platformService;
    @Autowired
    private ChargeService chargeService;

    @ResponseBody
    @RequestMapping(value = "/putString", method = RequestMethod.POST)
    public String putString(@RequestBody Map<String, String> map,
                            @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("putString");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
//        String platform = platformService.getPlatform(userId);
        String bucketName = map.get("bucketName");
        String platform = bucketService.getPlatform(bucketName);
        System.out.println(platform);
        if (platform.equals("ALI")) {
//            String bucketName = map.get("bucketName");
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

            double stringSize = FileUtils.getStringSize(content);
            chargeService.operate(bucketName, stringSize, "/putString", userId, "upload");

            return result;
        } else {
//            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String content = map.get("content");
            String objectPath = map.get("objectPath");
            if ("".equals(content) || "".equals(objectPath)) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.putString(content, bucketName, objectPath);

            double stringSize = FileUtils.getStringSize(content);
            chargeService.operate(bucketName, stringSize, "/putString", userId, "upload");

            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/putBytes", method = RequestMethod.POST)
    public String putBytes(@RequestBody Map<String, String> map,
                           @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("putBytes");
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

            double stringSize = FileUtils.getStringSize(content);
            chargeService.operate(bucketName, stringSize, "/putBytes", userId, "upload");
            return result;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/putStream", method = RequestMethod.POST)
    public String putStream(@RequestBody Map<String, String> map,
                            @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("putStream");
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

            chargeService.operate(bucketName, 0, "/putStream", userId, "upload");
            return result;
        } else {
            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String inputUrl = map.get("inputUrl");
            String objectPath = map.get("objectPath");
            if ("".equals(inputUrl) || "".equals(objectPath)) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.putStream(inputUrl, bucketName, objectPath);

            chargeService.operate(bucketName, 0, "/putStream", userId, "upload");
            return result;
        }
    }

    @RequestMapping(value = "/putFileStream")
    public String putFileStream(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                                @RequestParam("file") MultipartFile file,
                                @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("putFileStream");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
//        System.out.println(bn);
//        System.out.println(objectPath);
//        String filePath = "D:/IDEA/broker/src/main/resources/file/";
//        String uuid = UUID.randomUUID().toString();
//        String fileName = file.getOriginalFilename();
//        File f = new File(filePath + fileName);
//        try {
//            file.transferTo(f);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String path = "";
        String fileSize = "";
        try {
            String filePath = "D:/IDEA/broker/src/main/resources/file/";
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            fileSize = filePath + fileName;
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

            double stringSize = FileUtils.getFileSize(fileSize);
            chargeService.operate(bucketName, stringSize, "/putFileStream", userId, "upload");
            return result;
        } else {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if (objectPath == null || objectPath.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.uploadFile(path, bucketName, objectPath);

            double stringSize = FileUtils.getFileSize(fileSize);
            chargeService.operate(bucketName, stringSize, "/putFileStream", userId, "upload");
            return result;
        }
    }

    @RequestMapping(value = "/putFile")
    public String putFile(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                          @RequestParam("file") MultipartFile file,
                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("putFile");
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
        String fileSize = "";
        try {
            String filePath = "D:/IDEA/broker/src/main/resources/file/";
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            fileSize = filePath + fileName;
            File f = new File(filePath + fileName);
//                result = "http://localhost:8443/imgs/" + fileName;
            path = filePath + fileName;
            file.transferTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(bn);
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

            double stringSize = FileUtils.getFileSize(fileSize);
            chargeService.operate(bucketName, stringSize, "/putFile", userId, "upload");
            return result;
        } else {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if ("".equals(objectPath)) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.putFile(path, bucketName, objectPath);

            double stringSize = FileUtils.getFileSize(fileSize);
            chargeService.operate(bucketName, stringSize, "/putFile", userId, "upload");
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/appendObjectStreamFirst", method = RequestMethod.POST)
    public String appendObjectStreamFirst(@RequestBody Map<String, String> map,
                                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("appendObjectStreamFirst");
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

            double stringSize = FileUtils.getStringSize(content);
            chargeService.operate(bucketName, stringSize, "/appendObjectStreamFirst", userId, "upload");
            return result;
        } else {
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
            String result = huaweiUploadController.appendObjectStreamFirst(bucketName, objectPath, content);

            double stringSize = FileUtils.getStringSize(content);
            chargeService.operate(bucketName, stringSize, "/appendObjectStreamFirst", userId, "upload");
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/appendObjectStream", method = RequestMethod.POST)
    public String appendObjectStream(@RequestBody Map<String, String> map,
                                     @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("appendObjectStream");
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

            double stringSize = FileUtils.getStringSize(content);
            chargeService.operate(bucketName, stringSize, "/appendObjectStream", userId, "upload");
            return result;
        } else {
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
            String result = huaweiUploadController.appendObjectStream(Integer.parseInt(givenPosition), bucketName, objectPath, content);

            double stringSize = FileUtils.getStringSize(content);
            chargeService.operate(bucketName, stringSize, "/appendObjectStream", userId, "upload");
            return result;
        }
    }

    @RequestMapping(value = "/appendObjectFileFirst")
    public String appendObjectFileFirst(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                                        @RequestParam("contentType") String contentType, HttpServletRequest request,
                                        @RequestParam("file") MultipartFile file,
                                        @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("appendObjectFileFirst");
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

            double stringSize = FileUtils.getFileSize(path);
            chargeService.operate(bucketName, stringSize, "/appendObjectFileFirst", userId, "upload");
            return result;
        } else {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String uploadId = request.getParameter("uploadId");
            if ("".equals(objectPath) || "".equals(contentType) || "".equals(uploadId)) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.uploadPartFirst(path, bucketName, objectPath, uploadId);

            double stringSize = FileUtils.getFileSize(path);
            chargeService.operate(bucketName, stringSize, "/appendObjectFileFirst", userId, "upload");
            return result;
        }
    }

    @RequestMapping(value = "/appendObjectFile")
    public String appendObjectFile(@RequestParam("bucketName") String bn, @RequestParam("objectPath") String objectPath,
                                   @RequestParam("contentType") String contentType,
                                   @RequestParam("givenPosition") String givenPosition, HttpServletRequest request,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("appendObjectFile");
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

            double stringSize = FileUtils.getFileSize(path);
            chargeService.operate(bucketName, stringSize, "/appendObjectFile", userId, "upload");
            return result;
        } else {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String uploadId = request.getParameter("uploadId");
            String partNum = request.getParameter("partNum");
            if ("".equals(objectPath) || "".equals(contentType) || "".equals(uploadId) || "".equals(partNum)) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.uploadParts(Integer.parseInt(partNum), path, bucketName, objectPath, uploadId);

            double stringSize = FileUtils.getFileSize(path);
            chargeService.operate(bucketName, stringSize, "/appendObjectFile", userId, "upload");
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/completeMultipartUpload", method = RequestMethod.POST)
    public String completeMultipartUpload(@RequestBody CompleteMultipartUpload req,
                                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("completeMultipartUpload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        String bucketName = req.getBucketName();
        if (verifyBucketName(response, userId, platform, bucketName)) {
            return null;
        }
        if (platform.equals("HUAWEI")) {
            List<String> etags = req.getEtag();
            List<Integer> partNumber = req.getPartNumber();
            if ("".equals(req.getUploadId()) || "".equals(req.getObjectKey()) ||
                    etags.size() == 0 || partNumber.size() == 0) {
                response.setStatus(777);
                return null;
            }
            List<PartEtag> partEtags = new ArrayList<>();
            for (int i = 0; i < etags.size(); i++) {
                PartEtag p = new PartEtag(etags.get(i), partNumber.get(i));
                partEtags.add(p);
            }
            String result = huaweiUploadController.CompleteMultipartUpload(partEtags, bucketName, req.getObjectKey(), req.getUploadId());
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
        System.out.println("checkPointUpload");
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

            double stringSize = FileUtils.getFileSize(path);
            chargeService.operate(bucketName, stringSize, "/checkPointUpload", userId, "upload");
            return result;
        } else {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if (objectPath == null || objectPath.equals("")) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.CheckpointUpload(path, bucketName, objectPath);

            double stringSize = FileUtils.getFileSize(path);
            chargeService.operate(bucketName, stringSize, "/checkPointUpload", userId, "upload");
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/initiateMultipartUpload", method = RequestMethod.POST)
    public String initiateMultipartUpload(@RequestBody Map<String, String> map,
                                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("initiateMultipartUpload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("HUAWEI")) {
            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String objectPath = map.get("objectPath");
            String contentType = map.get("contentType");
            if ("".equals(objectPath) || "".equals(contentType)) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.InitiateMultipartUpload(bucketName, objectPath, contentType);
            return result;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/multipartUpload")
    public String multipartUpload(@RequestParam("bucketName") String bn, @RequestParam("objectName") String objectName,
                                  @RequestParam("file") MultipartFile file,
                                  @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("multipartUpload");
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

            double stringSize = FileUtils.getFileSize(path);
            chargeService.operate(bucketName, stringSize, "/multipartUpload", userId, "upload");
            return result;
        } else {
            String bucketName = bn;
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            if ("".equals(objectName)) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.concurrentMultipartUpload(path, bucketName, objectName);

            double stringSize = FileUtils.getFileSize(path);
            chargeService.operate(bucketName, stringSize, "/multipartUpload", userId, "upload");
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/abortMultipartUpload", method = RequestMethod.POST)
    public String abortMultipartUpload(@RequestBody Map<String, String> map,
                                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("abortMultipartUpload");
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
            String result = huaweiUploadController.AbortMultipartUpload(bucketName, objectName, uploadId);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/createFolder", method = RequestMethod.POST)
    public String createFolder(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("createFolder");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String platform = platformService.getPlatform(userId);
        if (platform.equals("HUAWEI")) {
            String bucketName = map.get("bucketName");
            if (verifyBucketName(response, userId, platform, bucketName)) {
                return null;
            }
            String pathname = map.get("pathname");
            if ("".equals(pathname)) {
                response.setStatus(777);
                return null;
            }
            String result = huaweiUploadController.createFolder(pathname, bucketName);
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
