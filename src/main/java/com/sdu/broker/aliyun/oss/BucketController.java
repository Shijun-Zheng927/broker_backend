package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.*;
import com.aliyun.oss.model.*;

import java.util.List;
//import org.springframework.boot.context.properties.ConfigurationProperties;

//@ConfigurationProperties(prefix = 'ali')
public class BucketController {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";

    public static int  createBucket(String bucketName, int storageClass, int dataRedundancyType, int cannedACL){
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
            return 0;
        }  catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally{
            ossClient.shutdown();
        }

        return 1;

    }

    //列举所有Bucket
    public static List<Bucket> listAllBuckets(){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        List<Bucket> buckets = ossClient.listBuckets();
        for (Bucket bucket : buckets){
            System.out.println(" - " + bucket.getName());
        }
        ossClient.shutdown();
        return buckets;
    }


    //列举有参数的Bucket
    public static List<Bucket> listRequestBuckets(String Prefix, String Marker, int maxKeys) {
        //调用该方法需要三个参数中至少有一个不为空
        //Prefix代表列举Bucket的前缀(如果没有，前端传空字符串)
        //Marker代表列举的起始位置(如果没有，前端传空字符串)
        //maxKeys表示列举空间的指定个数，默认值为100,如果传过来0转换成100
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
        try {
            if (!Prefix.isEmpty()) {
                listBucketsRequest.setPrefix(Prefix);
            }
            if (!Marker.isEmpty()) {
                listBucketsRequest.setMarker(Marker);
            }
            if (maxKeys == 0) {
                listBucketsRequest.setMaxKeys(100);
            } else if (maxKeys != 0) {
                listBucketsRequest.setMaxKeys(maxKeys);
            }

        } catch (OSSException oe) {
            return null;
        } catch (Exception e){
            return null;
        }
        BucketList bucketList = ossClient.listBuckets(listBucketsRequest);
        for(Bucket bucket : bucketList.getBucketList()){
            System.out.println(" - " + bucket.getName());
        }
        return bucketList.getBucketList();
    }


    public static void main(String[] args) {
//        listAllBuckets();
//        listRequestBuckets("xmsx","",2);
    }

}

