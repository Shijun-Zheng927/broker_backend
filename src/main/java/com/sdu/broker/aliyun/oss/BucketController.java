package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
//import org.springframework.boot.context.properties.ConfigurationProperties;

//@ConfigurationProperties(prefix = 'ali')
public class BucketController {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";

    public static void  createBucket(String bucketName, int storageClass, int dataRedundancyType, int cannedACL){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try{
            if(ossClient.doesBucketExist(bucketName)){
                System.out.println("您已创建Bucket:" + bucketName + "。");
            }else {
                System.out.println("您的Bucket不存在，创建Bucket:" + bucketName + "。");

                CreateBucketRequest bucketRequest = new CreateBucketRequest(bucketName);

                switch (storageClass){
                    case 1:
                        bucketRequest.setStorageClass(StorageClass.Standard);
                        break;
                    case 2:
                        bucketRequest.setStorageClass(StorageClass.IA);
                        break;
                    case 3:
                        bucketRequest.setStorageClass(StorageClass.Archive);
                    case 4:
                        bucketRequest.setStorageClass(StorageClass.ColdArchive);
                }

                switch (dataRedundancyType){
                    case 0:
                        bucketRequest.setDataRedundancyType(DataRedundancyType.LRS);
                        break;
                    case 1:
                        bucketRequest.setDataRedundancyType(DataRedundancyType.ZRS);
                        break;
                }

                switch (cannedACL){
                    case 1:
                        bucketRequest.setCannedACL(CannedAccessControlList.Private);
                        break;
                    case 2:
                        bucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                    case 3:
                        bucketRequest.setCannedACL(CannedAccessControlList.PublicReadWrite);
                }
                ossClient.createBucket(bucketRequest);
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
        String bucket_name = "xmsx-002";
        createBucket(bucket_name,1,1,2);
    }

}

