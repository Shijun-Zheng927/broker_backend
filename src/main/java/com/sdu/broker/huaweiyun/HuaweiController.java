package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HuaweiController {

    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    private static final String default_bucketLoc  = "cn-north-1";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    /* 创建桶 */
    public int createBucket(String bucketName,int rwPolicy,int storageClass)
    {
        ObsBucket obsBucket = new ObsBucket();
        //设置桶名字
        obsBucket.setBucketName(bucketName);
        // 设置桶访问权限为公共读，默认是私有读写
        switch (rwPolicy) {
            case 0:{
                //私有读写
                obsBucket.setAcl(AccessControlList.REST_CANNED_PRIVATE);
                break;
            }
            case 1:{
                //公共读私有写
                obsBucket.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
                break;
            }
            case 2:{
                //桶公共读，桶内对象公共读
                obsBucket.setAcl(AccessControlList.	REST_CANNED_PUBLIC_READ_DELIVERED);
                break;
            }
            case 3:{
                //公共读写
                obsBucket.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE);
                break;
            }
            case 4:{
                //桶公共读写，桶内对象公共读写
                obsBucket.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED);
                break;
            }
        }
        // 设置桶的存储类型为归档存储
        switch (storageClass) {
            case 0:{
                //标准
                obsBucket.setBucketStorageClass(StorageClassEnum.STANDARD);
                break;
            }
            case 1:{
                //低频访问
                obsBucket.setBucketStorageClass(StorageClassEnum.WARM);
                break;
            }
            case 2:{
                //归档
                obsBucket.setBucketStorageClass(StorageClassEnum.COLD);
                break;
            }
        }

        try {
            // 设置桶区域位
            obsBucket.setLocation(default_bucketLoc);
            obsClient.createBucket(obsBucket);
            System.out.println("create bucket:" + bucketName + " success！");
            return 1;
        }
        catch (ObsException e){
            return 0;
        }

    }

    /* 列举桶 */
    public List<ObsBucket> listBucket()
    {
        System.out.println("start listing all bucket");
        ListBucketsRequest request = new ListBucketsRequest();
        request.setQueryLocation(true);
        List<ObsBucket> buckets = obsClient.listBuckets(request);

//        closeObsClient();
        return buckets;
    }



    /* 删除桶 */
    public int removeBucket(String bucketName)
    {
        boolean exist = existBucket(bucketName);
        if (exist) {
            obsClient.deleteBucket(bucketName);
            System.out.println("delete bucket : " + bucketName + "success");
//            try {
//                obsClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return 0;
//            }
            return 1;
        } else {
            System.out.println("Not exist:" + bucketName);
            return 0;
        }

    }

    /* 判断桶是否存在 */
    public boolean existBucket(String bucketName)
    {
        boolean exist = obsClient.headBucket(bucketName);
        return exist;
    }

    /* 获取桶元数据 */
    public BucketMetadataInfoResult getresult(String bucketName)
    {
        BucketMetadataInfoRequest request = new BucketMetadataInfoRequest(bucketName);
        request.setOrigin("http://www.a.com");
        BucketMetadataInfoResult result = obsClient.getBucketMetadata(request);
        System.out.println("\t:" + result.getDefaultStorageClass());
        System.out.println("\t:" + result.getAllowOrigin());
        System.out.println("\t:" + result.getMaxAge());
        System.out.println("\t:" + result.getAllowHeaders());
        System.out.println("\t:" + result.getAllowMethods());
        System.out.println("\t:" + result.getExposeHeaders());
        return result;
    }

    /* 为桶设置预定义访问策略 */
    public void setBucketAcl(String bucketName,int rwPolicy)
    {
        switch (rwPolicy) {
            case 0: {
                //私有读写
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PRIVATE);
                break;
            }
            case 1: {
                //公共读私有写
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PUBLIC_READ);
                break;
            }
            case 2: {
                //桶公共读，桶内对象公共读
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PUBLIC_READ_DELIVERED);
                break;
            }
            case 3: {
                //公共读写
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PUBLIC_READ_WRITE);
                break;
            }
            case 4: {
                //桶公共读写，桶内对象公共读写
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED);
                break;
            }
        }
    }

    /* 设置log组访问策略 */
    public String setBucketAclForLog(String bucketName){
        try {
            obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_LOG_DELIVERY_WRITE);
        }catch (ObsException e){
            return "ObsException";
        }
        return "success";


    }

    /* 获取桶访问权限 */
    public String getBucketAcl(String bucketName){
        AccessControlList acl = obsClient.getBucketAcl(bucketName);
        String result = acl.toString();
        return result;
    }

    /* 设置桶策略 */
    public void setBucketPolicy(String bucketName,String policy)
    {
        ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        obsClient.setBucketPolicy(bucketName, policy);
    }

    /* 获取桶策略 */
    public String getBucketPolicy(String bucketName)
    {
        try{
            String policy = obsClient.getBucketPolicy(bucketName);
            System.out.println("\t" + policy);
            return policy;
        }catch (ObsException e){
            return "no policy";
        }

    }

    /* 删除桶策略 */
    public void deleteBucketPolicy(String bucketName){
        obsClient.deleteBucketPolicy(bucketName);
    }

    /* 获取桶的区域位置 */
    public String getlocation(String bucketName){
        String location = obsClient.getBucketLocation(bucketName);
        System.out.println("\t:" + location);
        return location;
    }

    /* 获取桶存量信息 */
    public Map<String, String> getBucketStorageInfo(String bucketName){
        BucketStorageInfo storageInfo = obsClient.getBucketStorageInfo(bucketName);
        Map<String, String> result = new HashMap<>();
        result.put("objectNmuber", Long.toString(storageInfo.getObjectNumber()));
        result.put("size", Long.toString(storageInfo.getSize()));
        System.out.println("\t" + storageInfo.getObjectNumber());
        System.out.println("\t" + storageInfo.getSize());
        return result;
    }

    /* 设置桶配额 */
    public void setBucketQuota(String bucketName,long size){
        BucketQuota quota = new BucketQuota(size);
        obsClient.setBucketQuota(bucketName, quota);
        System.out.println("set quota success");
    }

    /* 获取桶配额 */
    public long getBucketQuota(String bucketName){
        BucketQuota quota = obsClient.getBucketQuota(bucketName);
//        System.out.println("\t" + quota.getBucketQuota());

        return quota.getBucketQuota();
    }

    /* 设置桶存储类型 */
    public void setBucketStoragePolicy(String bucketName,int policy){
        BucketStoragePolicyConfiguration storgePolicy = new BucketStoragePolicyConfiguration();
        switch (policy){
            case 0:{
                storgePolicy.setBucketStorageClass(StorageClassEnum.STANDARD);
                break;
            }
            case 1:{
                storgePolicy.setBucketStorageClass(StorageClassEnum.WARM);
                break;
            }
            case 2:{
                storgePolicy.setBucketStorageClass(StorageClassEnum.COLD);
                break;
            }
        }
        obsClient.setBucketStoragePolicy(bucketName, storgePolicy);
    }

    /* 获取桶存储类型 */
    public String getBucketStorageClass(String bucketName){
        BucketStoragePolicyConfiguration storagePolicy = obsClient.getBucketStoragePolicy(bucketName);
//        System.out.println("\t" + storagePolicy.getBucketStorageClass());
        return storagePolicy.getBucketStorageClass().toString();
    }

    /* 列举对象（文件）的信息 */
    public static void listFile(String BucketName) throws ObsException
    {
        System.out.println("start listing objects in bucket");
        ObjectListing objList = obsClient.listObjects(BucketName);
        for (ObsObject obj : objList.getObjects())
        {
            System.out.println("--:"+obj.getObjectKey()+" (size=" + obj.getMetadata().getContentLength()+")");
        }
    }

    /* 删除对象 */
    public static void deleteObject(String BucketName,String objectKey)
    {
        System.out.println("now start deleting");
        boolean exist = obsClient.doesObjectExist(BucketName, objectKey);
        DeleteObjectResult result = null;
        if (exist) {
            result = obsClient.deleteObject(BucketName, objectKey);
            if (result.isDeleteMarker() == false){
                System.out.println("delete "+objectKey+" success!");
            }
        }
        else{
            System.out.println("object : "+ objectKey + "not found");
        }
    }

    /* 关闭客户端 */
    public static void closeObsClient()
    {
        try
        {
            obsClient.close();
            System.out.println("close obs client success");
        }
        catch (IOException e)
        {
            System.out.println("close obs client error.");
        }

    }

}
