package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//文件管理/对象管理
@Component
public class AliObjectController {
    private static final String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static final String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static final String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";

    //判断文件是否存在
    public boolean doesObjectExist(String bucketName, String objectPath){
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        boolean found = ossClient.doesObjectExist(bucketName,objectPath);
        System.out.println(found);
        ossClient.shutdown();
        return found;
    }

    //管理文件访问权限

    //设置文件访问权限
    public int setObjectAcl(String bucketName,String objectPath, int acl){
        //acl:  1--私有  2--公共读   3-公共读写
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        switch (acl){
            case 1:
                ossClient.setObjectAcl(bucketName,objectPath, CannedAccessControlList.Private);
            case 2:
                ossClient.setObjectAcl(bucketName,objectPath, CannedAccessControlList.PublicRead);
            case 3:
                ossClient.setObjectAcl(bucketName,objectPath, CannedAccessControlList.PublicReadWrite);
        }
        ossClient.shutdown();

        return acl;
    }

    //获取文件访问权限
    public  String getObjectAcl(String bucketName,String objectPath){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ObjectAcl objectAcl = ossClient.getObjectAcl(bucketName, objectPath);
        String acl = objectAcl.getPermission().toString();
        System.out.println(acl);

        ossClient.shutdown();
        return acl;
    }

    //列举文件
    //简单列举
    public  List<String>  simpleListObject(String bucketName){
        List<String> objectList = new ArrayList<>();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 列举文件。如果不设置KeyPrefix，则列举存储空间下的所有文件。如果设置KeyPrefix，则列举包含指定前缀的文件。
        ObjectListing objectListing = ossClient.listObjects(bucketName);
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        for (OSSObjectSummary s : sums) {
            System.out.println("\t" + s.getKey());
            objectList.add(s.getKey());
        }
        ossClient.shutdown();
        return objectList;
    }
    //限定前缀列举
    public  List<String>  simpleListObject(String bucketName,String prefix){
        List<String> objectList = new ArrayList<>();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 列举文件。如果不设置KeyPrefix，则列举存储空间下的所有文件。如果设置KeyPrefix，则列举包含指定前缀的文件。
        ObjectListing objectListing = ossClient.listObjects(bucketName, prefix);
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        for (OSSObjectSummary s : sums) {
            System.out.println("\t" + s.getKey());
            objectList.add(s.getKey());
        }
        ossClient.shutdown();
        return objectList;
    }

    //指定数目文件列举
    public  List<String> simpleListObject(String bucketName,int maxKeys) {
        List<String> objectList = new ArrayList<>();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withMaxKeys(maxKeys));
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        for (OSSObjectSummary s : sums) {
            System.out.println("\t" + s.getKey());
            objectList.add(s.getKey());
        }
        ossClient.shutdown();
        return  objectList;
    }

    //列举指定数目和指定前缀的文件
    public  List<String> simpleListObject(String bucketName,String prefix,int maxKeys) {
        List<String> objectList = new ArrayList<>();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withMaxKeys(maxKeys).withPrefix(prefix));
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        for (OSSObjectSummary s : sums) {
            System.out.println("\t" + s.getKey());
            objectList.add(s.getKey());
        }
        ossClient.shutdown();
        return  objectList;
    }
    //分页列举全部文件
    public   List<String> pageObjectList(String bucketName){
        List<String> objectList = new ArrayList<>();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        final int maxKeys = 200;
        String nextMarker = null;
        ObjectListing objectListing;

        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).withMarker(nextMarker).withMaxKeys(maxKeys));

            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
                objectList.add(s.getKey());
            }

            nextMarker = objectListing.getNextMarker();

        } while (objectListing.isTruncated());

        // 关闭OSSClient。
        ossClient.shutdown();
        return objectList;

    }

    //分页列举指定前缀文件
    public  List<String> pageObjectList(String bucketName,String prefix){
        List<String> objectList = new ArrayList<>();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        final int maxKeys = 200;
        String nextMarker = null;
        ObjectListing objectListing;

        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName).
                    withPrefix(prefix).withMarker(nextMarker).withMaxKeys(maxKeys));

            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
                objectList.add(s.getKey());
            }

            nextMarker = objectListing.getNextMarker();

        } while (objectListing.isTruncated());

       // 关闭OSSClient。
        ossClient.shutdown();
        return objectList;

    }


    //列举文件夹下的所有文件
    //创建文件夹
    public  String createDirectory(String bucketName,String dirName){
        //输入的文件夹名称以右斜线（/）结尾
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, dirName,new File("F://broker_backend//dic.txt"));
        return "2333";
    }
    //列举指定文件夹的文件
    public  List<String> dicListObject(String bucketName,String dicName){
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        List<String> objectList = new ArrayList<>();
        // 构造ListObjectsRequest请求。
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        // 设置prefix参数来获取fun目录下的所有文件。
        listObjectsRequest.setPrefix(dicName);

        // 递归列举fun目录下的所有文件。
        ObjectListing listing = ossClient.listObjects(listObjectsRequest);

        // 遍历所有文件。
        System.out.println("Objects:");
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
            objectList.add(objectSummary.getKey());
        }

        // 遍历所有commonPrefix。
        System.out.println("\nCommonPrefixes:");
        for (String commonPrefix : listing.getCommonPrefixes()) {
            System.out.println(commonPrefix);
        }

        // 关闭OSSClient。
        ossClient.shutdown();
        return objectList;
    }

    //简单删除
    public  String deleteObject(String bucketName,String objectPath){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName,objectPath);
        ossClient.shutdown();
        return "hhh";
    }

    //批量删除文件

    //拷贝文件
    public  String simpleCopyObject(String sourceBucketName, String sourceObjectPath,String destinationBucketName,String destinationObjectPath){
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 拷贝文件。
        CopyObjectResult result = ossClient.copyObject(sourceBucketName, sourceObjectPath, destinationBucketName, destinationObjectPath);
        System.out.println("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());
        String etag = result.getETag();
        // 关闭OSSClient。
        ossClient.shutdown();
        return etag;
    }
    public static void main(String[] args) {
    /*
        setObjectAcl("xmsx-001","123.txt",3);
        getObjectAcl("xmsx-001","123.txt");
        simpleListObject("xmsx-001","app");
        simpleListObject("xmsx-001","app",2);
    */
//        dicListObject("xmsx-001","666/");
    }

}

