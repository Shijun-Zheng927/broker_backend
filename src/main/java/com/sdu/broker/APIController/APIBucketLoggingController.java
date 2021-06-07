package com.sdu.broker.APIController;

import com.sdu.broker.aliyun.oss.AliBucketLoggingController;
import com.sdu.broker.huaweiyun.HuaweiBuckectLoggingController;
import com.sdu.broker.huaweiyun.HuaweiDownloadController;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@Controller
public class APIBucketLoggingController {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private HuaweiBuckectLoggingController huaweiBuckectLoggingController;
    @Autowired
    private AliBucketLoggingController aliBucketLoggingController;

    @ResponseBody
    @RequestMapping(value = "/openBucketLogging", method = RequestMethod.POST)
    public String openBucketLogging(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("openBucketLogging");
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
            String logBucketName = map.get("logBucketName");
            String sourceBucketName = map.get("bucketName");
            String logPath = map.get("logpath");
            if (logBucketName == null || logPath == null || "".equals(sourceBucketName) || "".equals(logPath)) {
                response.setStatus(777);
                return null;
            }
            String result = aliBucketLoggingController.openBucketLogging(sourceBucketName, logBucketName, logPath);
            return result;

        } else {
            /*实际上的三个参数两边是一样的，命名不一样
            HUAWEI.targetBucketName=ALI.logBucketName
            HUAWEI.sourceBucketName=ALI.bucketName
            HUAWEI.targetPrefix=ALI.logpath
            按照map返回的参数修改吧
             */
            String targetBucketName = map.get("logBucketName");
            String sourceBucketName = map.get("bucketName");
            String targetPrefix = map.get("logpath");
            if (targetBucketName == null || targetPrefix == null  || "".equals(targetBucketName) || "".equals(targetPrefix)) {
                response.setStatus(777);
                return null;
            }

            String result = huaweiBuckectLoggingController.setBuckectLogging(targetBucketName,targetPrefix,sourceBucketName);

            //result的结果为“success”或“ObsException”
            //返回结果
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getBucketLogging", method = RequestMethod.POST)
    public Map<String,String> getBucketLogging(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getBucketLogging");
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
            Map<String, String> logMap = aliBucketLoggingController.checkBucketLogging(bucketName);
            return logMap;
        } else {
            /*
            返回的map是
            <"targetBucketName",目标桶名称>
            <"targetPrefix",桶内路径>
            路径指向日志文件夹
             */
//            String sourceBucketName = map.get("sourceBucketName");
            Map<String,String> logMap = huaweiBuckectLoggingController.getBucketLogging(bucketName);
            return logMap;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/closeBucketLogging", method = RequestMethod.POST)
    public String closeBucketLogging(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("closeBucketLogging");
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

            String result = aliBucketLoggingController.closeBucketLogging(bucketName);
            return result;
        } else {
            /*
            result的结果为“success”或“ObsException”
             */
            System.out.println(bucketName);
            String result = huaweiBuckectLoggingController.shutdownBucketLogging(bucketName);
            return result;
        }
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
