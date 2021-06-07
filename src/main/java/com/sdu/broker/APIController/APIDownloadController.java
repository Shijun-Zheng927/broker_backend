package com.sdu.broker.APIController;

import com.sdu.broker.aliyun.oss.AliDownloadController;
import com.sdu.broker.aliyun.oss.AliObjectController;
import com.sdu.broker.pojo.UrlPath;
import com.sdu.broker.pojo.req.DownloadFile;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.GetObjectRequest;
import com.sdu.broker.huaweiyun.HuaweiDownloadController;
import com.sdu.broker.huaweiyun.HuaweiObjectController;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.service.ChargeService;
import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.FileUtils;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.Objects;

@Controller
@CrossOrigin
public class APIDownloadController {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private HuaweiDownloadController huaweiDownloadController;
    @Autowired
    private HuaweiObjectController huaweiObjectController;
    @Autowired
    private UrlPath urlPath;
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private AliDownloadController aliDownloadController;
    @Autowired
    private AliObjectController aliObjectController;

    @ResponseBody
    @RequestMapping(value = "/demo", method = RequestMethod.POST)
    public String demo(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("/demo");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            //在此获取其他参数并验证
            String acl = map.get("rwPolicy");
            if ("".equals(acl)) {
                //设置默认值
                acl = "0";
            }

            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return "result";
        } else {
            String rwPolicy = map.get("rwPolicy");
            if ("".equals(rwPolicy)) {
                //设置默认值
                rwPolicy = "0";
            }

            //华为云在此进行方法调用
//            huaweiController.setBucketAcl(bucketName, Integer.parseInt(rwPolicy));

            //返回结果
            return "result";
        }
    }

    //流式下载
    @ResponseBody
    @RequestMapping(value = "/streamDownload", method = RequestMethod.POST)
    public String streamDownload(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("streamDownload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");
        if (verify(response, userId, bucketName)) {
            return "fail";
        }
        if (objectKey == null || "".equals(objectKey)) {
            return "fail";
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {

            //阿里云在此调用方法
            if(aliObjectController.doesObjectExist(bucketName,objectKey)){
                String s = aliDownloadController.streamDownload(bucketName, objectKey);

                double stringSize = FileUtils.getStringSize(s);
                chargeService.operate(bucketName, stringSize, "/streamDownload", userId, "download");
                return s;
            } else {
                return "fail";
            }
        } else {
            if (huaweiObjectController.ifObjectExist(bucketName, objectKey)){
                GetObjectRequest request = huaweiDownloadController.newObjectRequest(bucketName,objectKey);
                String s = huaweiDownloadController.streamDownload(request);

                double stringSize = FileUtils.getStringSize(s);
                chargeService.operate(bucketName, stringSize, "/streamDownload", userId, "download");
                return s;
            }else {
                return "fail";
            }
        }

    }

    //范围下载
    @ResponseBody
    @RequestMapping(value = "/rangeDownload", method = RequestMethod.POST)
    public DownloadFile rangeDownload(@RequestBody Map<String, String> map,
                                 @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("rangeDownload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");

        DownloadFile downloadFile = new DownloadFile("fail", "fail");

        if (verify(response, userId, bucketName)) {
            return downloadFile;
        }
        if (objectKey == null || "".equals(objectKey)) {
            response.setStatus(777);
            return downloadFile;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            //在此获取其他参数并验证

            String begin = map.get("begin");
            String end = map.get("end");
            if ("".equals(begin) || "".equals(end) || !BucketUtils.isNumber(begin) || !BucketUtils.isNumber(end)) {
                response.setStatus(777);
                return downloadFile;
            }
            String path = "D:/IDEA/broker/src/main/resources/static/file/" + objectKey;
            if(aliObjectController.doesObjectExist(bucketName, objectKey)){
                String s = aliDownloadController.rangeDownload(bucketName, objectKey, path,
                        Integer.parseInt(begin), Integer.parseInt(end));
                if(s.equals("false!")){
                    return downloadFile;
                } else {

                    double stringSize = FileUtils.getFileSize(path);
                    chargeService.operate(bucketName, stringSize, "/rangeDownload", userId, "download");

                    downloadFile.setUrl(urlPath.getUrlPath() + "file/" + objectKey);
                    downloadFile.setName(objectKey);
                    return downloadFile;
                }
            } else {
                return downloadFile;
            }
        } else {
            String begin = map.get("begin");
            String end = map.get("end");
            if (begin == null || end == null || "".equals(begin) || "".equals(end) || !BucketUtils.isNumber(begin) || !BucketUtils.isNumber(end)) {
                response.setStatus(777);
                return downloadFile;
            }
            String path = "D:/IDEA/broker/src/main/resources/static/file/" + objectKey;
            if (huaweiObjectController.ifObjectExist(bucketName, objectKey)){
                GetObjectRequest request = huaweiDownloadController.newObjectRequest(bucketName, objectKey);
                String s = huaweiDownloadController.rangeDownload(request, path, Integer.parseInt(begin), Integer.parseInt(end));
                if (s.equals("OBS exception!")||s.equals("IO exception!")){
                    return downloadFile;
                }

                double stringSize = FileUtils.getFileSize(path);
                chargeService.operate(bucketName, stringSize, "/rangeDownload", userId, "download");

                downloadFile.setUrl(urlPath.getUrlPath() + "file/" + objectKey);
                downloadFile.setName(objectKey);
                return downloadFile;
            }else {
                return downloadFile;
            }
        }
    }

    //断点续传下载
    @ResponseBody
    @RequestMapping(value = "/checkPointDownload", method = RequestMethod.POST)
    public DownloadFile checkPointDownload(@RequestBody Map<String, String> map,
                                     @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("checkPointDownload");

        DownloadFile downloadFile = new DownloadFile("fail", "fail");

        if (!verifyIdentity(response, authorization)) {
            return downloadFile;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");
        if (verify(response, userId, bucketName)) {
            return downloadFile;
        }
        if (objectKey == null || "".equals(objectKey)) {
            response.setStatus(777);
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {

            //阿里云在此调用方法
            String partSize = map.get("partSize");
            String taskNum = map.get("taskNum");
            if ("".equals(partSize) || "".equals(taskNum) || !BucketUtils.isNumber(partSize) || !BucketUtils.isNumber(taskNum)) {
                response.setStatus(777);
                return downloadFile;
            }
            String path = "D:/IDEA/broker/src/main/resources/static/file/" + objectKey;
            if(aliObjectController.doesObjectExist(bucketName,objectKey)){
                String s = aliDownloadController.checkPointDownload(bucketName, objectKey, path,
                        Integer.parseInt(partSize), Integer.parseInt(taskNum));

                double stringSize = FileUtils.getFileSize(path);
                chargeService.operate(bucketName, stringSize, "/checkPointDownload", userId, "download");

                downloadFile.setUrl(urlPath.getUrlPath() + "file/" + objectKey);
                downloadFile.setName(objectKey);
                return downloadFile;
            } else {
                return downloadFile;
            }
        } else {
            String partSize = map.get("partSize");
            String taskNum = map.get("taskNum");
            if ("".equals(partSize) || "".equals(taskNum) || !BucketUtils.isNumber(partSize) || !BucketUtils.isNumber(taskNum)) {
                response.setStatus(777);
                return downloadFile;
            }
            String path = "D:/IDEA/broker/src/main/resources/static/file/" + objectKey;
            if (huaweiObjectController.ifObjectExist(bucketName, objectKey)) {
                DownloadFileRequest request = huaweiDownloadController.newDownloadRequest(bucketName, objectKey);
                String s = huaweiDownloadController.checkPointDownload(request, path,
                        Integer.parseInt(partSize), Integer.parseInt(taskNum));
                if (s.equals("download failed")) {
                    return downloadFile;
                }

                double stringSize = FileUtils.getFileSize(path);
                chargeService.operate(bucketName, stringSize, "/checkPointDownload", userId, "download");

                downloadFile.setUrl(urlPath.getUrlPath() + "file/" + objectKey);
                downloadFile.setName(objectKey);
                return downloadFile;
            } else {
                return downloadFile;
            }
        }
    }
    @ResponseBody
    @RequestMapping(value = "/downloadTest")
    public DownloadFile downloadTest() {
        System.out.println("downloadTest");
        byte[] bytes = null;
        try {
            File f = new File("D:/IDEA/broker/src/main/resources/static/file/a.jpg");
            FileInputStream inputStream = new FileInputStream(f);
            bytes = new byte[inputStream.available()];
//            inputStream.read(bytes, 0, inputStream.available());
//            FileOutputStream fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setName("groot.txt");
        downloadFile.setUrl(urlPath.getUrlPath() + "file/a.txt");
        return downloadFile;
//        return bytes;
    }


    //工具方法
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

    public boolean verifyIdentity(HttpServletResponse response, String token) {
        if (!TokenUtils.verify(token)) {
            response.setStatus(999);
            return false;
        }
        return true;
    }
}
