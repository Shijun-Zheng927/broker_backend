package com.sdu.broker.controller;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class HuaweiController {

    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    private static final String g_bucketName = "brokertest-demo";
    private static final String g_bucketLoc  = "cn-north-1";
    public static void main(String[] args)
    {
        System.out.println("welcome to china!!!");

        /* 创建OBS客户端 */
        ObsClient obsClient = new ObsClient(ak,sk,endPoint);
        ObsBucket obsBucket;
//
        ObsBucket obsBucket1 = new ObsBucket("brokertest-demo","cn-north-1");
        String objectkey = "hello2";
        try
        {
            /* 创建一个桶 */
//            obsBucket = createBucket(obsClient);

            /* 从本地笔记本上传文件到桶里面 */
//            uploadFile(obsClient, obsBucket1);

            /* 列举对象（文件）的信息 */
//            listFile(obsClient,obsBucket1);

            /* 列举所有桶 */
//            listBucket(obsClient);

            /* 删除对象 */
//            deleteObject(obsClient,obsBucket1,objectkey);

            /* 下载文件 */
            getFile(obsClient,obsBucket1,objectkey);

        }
        catch (ObsException | IOException e)
        {
            System.out.println("main 函数出现错误！！！");
        }

        /* 关闭obs客户端  */
        closeObsClient(obsClient);
    }


    /* 创建一个桶 */
    private static ObsBucket createBucket(ObsClient obsClient) throws ObsException
    {
        ObsBucket obsBucket = new ObsBucket(g_bucketName,g_bucketLoc);
        obsClient.createBucket(obsBucket);
        System.out.println("Create bucket:" + g_bucketName + " successfully!");
        return obsBucket;
    }

    /* 创建一个对象（文件） */
    private static void uploadFile(ObsClient obsClient, ObsBucket obsBucket) throws ObsException
    {
        File newfile = new File("D:\\ ProjectTraining\\brokertest.txt");
        obsClient.putObject(obsBucket.getBucketName(), "hello1", newfile);
        obsClient.putObject(obsBucket.getBucketName(), "hello2", newfile);
    }

    /* 列举对象（文件）的信息 */
    private static void listFile(ObsClient obsClient, ObsBucket obsBucket) throws ObsException
    {
        System.out.println("start listing objects in bucket");
        ObjectListing objList = obsClient.listObjects(obsBucket.getBucketName());
        for (ObsObject obj : objList.getObjects())
        {
            System.out.println("--:"+obj.getObjectKey()+" (size=" + obj.getMetadata().getContentLength()+")");
        }
    }

    /* 列举所有桶 */
    private static void listBucket(ObsClient obsClient)
    {
        System.out.println("start listing all bucket");
        ListBucketsRequest request = new ListBucketsRequest();
        request.setQueryLocation(true);
        List<ObsBucket> buckets = obsClient.listBuckets(request);
        for (ObsBucket bucket : buckets) {
            System.out.println("Bucket Name:" + bucket.getBucketName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("Create Date:" + bucket.getCreationDate());
            System.out.println("Location:" + bucket.getLocation());
            System.out.println();
        }
    }
    /* 删除对象 */
    private static void deleteObject(ObsClient obsClient, ObsBucket Bucket,String objectKey)
    {
        System.out.println("now start deleting");
        boolean exist = obsClient.doesObjectExist(Bucket.getBucketName(), objectKey);
        DeleteObjectResult result = null;
        if (exist) {
            result = obsClient.deleteObject(Bucket.getBucketName(), objectKey);
            if (result.isDeleteMarker() == false){
                System.out.println("delete "+objectKey+" success!");
            }
        }
    }

    /* 下载文件 */
    private static void getFile(ObsClient obsClient,ObsBucket Bucket,String objectKey) throws IOException {
        System.out.println("now downloading file");
        ObsObject object = null;
        boolean exist = obsClient.doesObjectExist(Bucket.getBucketName(), objectKey);
        if (exist) {
            object = obsClient.getObject(Bucket.getBucketName(), objectKey);
        }
        if (object != null) {
            InputStream is = object.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File("D:\\ ProjectTraining\\download\\download2.txt"));
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            System.out.println("download success!");
            is.close();
            fos.close();
            return;
        }
 }



    /* 关闭客户端 */
    private static void closeObsClient(ObsClient obsClient)
    {
        try
        {
            obsClient.close();
        }
        catch (IOException e)
        {
            System.out.println("close obs client error.");
        }

    }

}
