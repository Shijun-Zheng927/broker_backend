package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;


import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class UpdateController {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";

    //流式上传：上传字符串、上传数组、上传

    //上传字符串
    public static String putString(String content, String bucketName, String objectPath){
        //objectPath:字符串的保存路径，例如： test.txt(不要带存储空间名称）
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //创建PutObjectRequest对象
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectPath, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            /*
             如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
             ObjectMetadata metadata = new ObjectMetadata();
             metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
             metadata.setObjectAcl(CannedAccessControlList.Private);
             putObjectRequest.setMetadata(metadata);
            */

            //上传字符串
            ossClient.putObject(putObjectRequest);
            ossClient.shutdown();

            System.out.println(content);
        } catch (OSSException ossException) {
            System.out.println("出错了智障");
            return "false";
        }
        
        return "上传字符串成功";
    }
    
    //上传Byte数组
    public static String putBytes(byte[] bytes,String bucketName, String objectPath){
        //输入参数：bytes 需要上传的byte数组
        //        objectPath 存储路径（不包含存储空间名称）
        try {
            //创建ossClient实例
            OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
            //填写输入参数
            ossClient.putObject(bucketName, objectPath, new ByteArrayInputStream(bytes));
            //关闭ossClient
            ossClient.shutdown();
        } catch (OSSException ossException) {
            ossException.printStackTrace();
            return "false";
        } catch (ClientException e) {
            e.printStackTrace();
            return "false";
        }

        return "上传Byte数组成功";
    }

    //上传网络流
    public static String putStream(String inputUrl, String bucketName, String objectPath){
        //输入参数：inputUrl  网络流对应的URL地址
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        try {
            //填写网络流地址
            URL url = new URL(inputUrl);
            InputStream inputStream = url.openStream();
            //填写输入参数
            ossClient.putObject(bucketName, objectPath, inputStream);
            //关闭ossClient
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }

        ossClient.shutdown();
        return "上传网络流成功";
    }

    //上传文件流
    public static String putFileStream(String fileStreamPath, String bucketName, String objectPath){
        //输入参数: fileStreamPath 所上传文件流的本地路径 格式如下：F:\\数值计算\\实验一截图1.png
        //注意是双斜线
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        try {
            InputStream inputStream = new FileInputStream(fileStreamPath);
            ossClient.putObject(bucketName,objectPath,inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "false";
        }

        ossClient.shutdown();
        return "上传文件流成功！";
    }

    //文件上传
    public static String putFile(String filePath, String bucketName, String objectPath){
        //输入参数:filePath:所上传文件在本地的绝对路径
        //格式也是双右斜线\\
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,objectPath,new File(filePath));

        /*
         如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
         ObjectMetadata metadata = new ObjectMetadata();
         metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
         metadata.setObjectAcl(CannedAccessControlList.Private);
         putObjectRequest.setMetadata(metadata);

        */

        //上传文件
        ossClient.putObject(putObjectRequest);
        //关闭ossClient
        ossClient.shutdown();

        return "";

    }


    public static void main(String[] args) {
//        byte[] haha = "Naruto come on".getBytes(StandardCharsets.UTF_8);
//        putString("hello onePiece", "xmsx-00o1", "hello.txt");
//        putBytes(haha,"xmsx-001", "hello2.txt");
//        putStream("https://www.aliyun.com/", "xmsx-001", "testStream.txt");
//        putFileStream("F:\\数值计算\\实验一截图1.png","xmsx-001", "picture1.png");
    }


}
