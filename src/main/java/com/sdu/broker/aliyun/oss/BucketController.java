package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.BucketInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

//@ConfigurationProperties(prefix = 'ali')
public class BucketController {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";

    public static void  createBucket(String bucketName){

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try{
            if(ossClient.doesBucketExist(bucketName)){
                System.out.println("您已创建Bucket:" + bucketName + "。");
            }else {
                System.out.println("您的Bucket不存在，创建Bucket:" + bucketName + "。");
                ossClient.createBucket(bucketName);
            }

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            System.out.println("Bucket" + bucketName + "的信息如下： ");
            System.out.println("\t数据中心：" + info.getBucket().getLocation());
            System.out.println("\t创建时间: " + info.getBucket().getCreationDate());
            System.out.println("\t用户标志：" + info.getBucket().getOwner());
        } catch (OSSException oe){
            oe.printStackTrace();
        } catch (ClientException ce){
            ce.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally{
            ossClient.shutdown();
        }
    }

    public static void main(String[] args) {
        String bucket_name = "xmsx-001";
        createBucket(bucket_name);
    }

}

