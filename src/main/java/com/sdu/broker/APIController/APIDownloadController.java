package com.sdu.broker.APIController;

import com.sdu.broker.pojo.req.DownloadFile;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.GetObjectRequest;
import com.sdu.broker.huaweiyun.HuaweiDownloadController;
import com.sdu.broker.huaweiyun.HuaweiObjectController;
import com.sdu.broker.service.BucketService;
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

    @ResponseBody
    @RequestMapping(value = "/demo", method = RequestMethod.POST)
    public String demo(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
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
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");
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
            if (huaweiObjectController.ifObjectExist(bucketName, objectKey)){
                GetObjectRequest request = huaweiDownloadController.newObjectRequest(bucketName,objectKey);
                String s = huaweiDownloadController.streamDownload(request);
                return s;
            }else {
                return null;
            }
        }
    }

    //范围下载
    @ResponseBody
    @RequestMapping(value = "/rangeDownload", method = RequestMethod.POST)
    public String rangeDownload(@RequestBody Map<String, String> map,String localFile,int begin,int end,
                                 @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");
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
            if (huaweiObjectController.ifObjectExist(bucketName, objectKey)){
                GetObjectRequest request = huaweiDownloadController.newObjectRequest(bucketName,objectKey);
                String s = huaweiDownloadController.rangeDownload(request,localFile,begin,end);
                if (s.equals("OBS exception!")||s.equals("IO exception!")){
                    return null;
                }
                return s;
            }else {
                return null;
            }
        }
    }

    //断点续传下载
    @ResponseBody
    @RequestMapping(value = "/checkPointDownload", method = RequestMethod.POST)
    public String checkPointDownload(@RequestBody Map<String, String> map,String localFile, int partSize,int taskNum,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");
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
            if (huaweiObjectController.ifObjectExist(bucketName, objectKey)){
                DownloadFileRequest request = huaweiDownloadController.newDownloadRequest(bucketName, objectKey);
                String s = huaweiDownloadController.checkPointDownload(request,localFile,partSize,taskNum);
                if (s.equals("download failed")){
                    return null;
                }
                return s;
            }else {
                return null;
            }
        }
    }
    @ResponseBody
    @RequestMapping(value = "/downloadTest")
    public byte[] downloadTest() {
        byte[] bytes = null;
        try {
            File f = new File("D:\\IDEA\\broker\\src\\main\\resources\\static\\head\\groot.jpg");
            FileInputStream inputStream = new FileInputStream(f);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            FileOutputStream fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return new DownloadFile("groot.jpg", bytes);
        return bytes;
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
