package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class AliBucketLoggingController {
    private static final String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static final String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static final String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";
    //访问日志
    //开启访问日志
    public  String openBucketLogging(String bucketName,String logBucketName,String logPath){
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            SetBucketLoggingRequest request = new SetBucketLoggingRequest(bucketName);
            // 设置存放日志文件的存储空间。
            request.setTargetBucket(logBucketName);
            // 设置日志文件存放的目录。
            request.setTargetPrefix(logPath);
            ossClient.setBucketLogging(request);
        } catch (OSSException | ClientException ossException) {
            ossException.printStackTrace();
            return "fail";
        }


        // 关闭OSSClient。
        ossClient.shutdown();
        return "success";

    }

    //查看访问日志
    public Map<String,String> checkBucketLogging(String bucketName){
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        Map<String,String> map = new HashMap<>();
        BucketLoggingResult result = ossClient.getBucketLogging(bucketName);
        map.put(result.getTargetBucket(),result.getTargetPrefix());
        System.out.println(result.getTargetBucket());
        System.out.println(result.getTargetPrefix());

        // 关闭OSSClient。
        ossClient.shutdown();
        return map;
    }

    //关闭访问日志
    public String closeBucketLogging(String bucketName){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try{
            SetBucketLoggingRequest request = new SetBucketLoggingRequest(bucketName);
            request.setTargetBucket(null);
            request.setTargetPrefix(null);
            ossClient.setBucketLogging(request);
        } catch (OSSException ossException) {
            ossException.printStackTrace();
            return "ossException";
        } catch (ClientException e) {
            e.printStackTrace();
            return "clientException";
        }


        // 关闭OSSClient。
        ossClient.shutdown();
        return   "success";
    }
}
