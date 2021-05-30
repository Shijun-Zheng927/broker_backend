package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;

import java.util.ArrayList;
import java.util.List;

public class HuaweiObjectController {
    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    /* 设置对象属性 */
    /* 新建设置属性请求 */
    public SetObjectMetadataRequest newSetRequest(String bucketName,String objectKey){
        SetObjectMetadataRequest request = new SetObjectMetadataRequest(bucketName,objectKey);
        return request;
    }

    /* 设置对象HTTP头域 */
    public String setHttp(SetObjectMetadataRequest request,String contentType,String expires){
        request.setContentType("ContentType");
        request.setExpires("Expires");
        obsClient.setObjectMetadata(request);
        return "success";
    }

    /* 设置自定义元数据 */
    public String addUserMetadata(SetObjectMetadataRequest request,String property,String propertyValue){
        request.addUserMetadata(property, propertyValue);
        return "success";
    }

    /* 获取对象属性 */
    public ObjectMetadata getMetadata(String bucketName,String objectKey){
        ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName, objectKey);
        return metadata;
    }

    /* 列举对象 */
    /* 新建列举请求 */
    public ListObjectsRequest newListRequest(String bucketName){
        ListObjectsRequest request = new ListObjectsRequest(bucketName);
        return  request;
    }
    /* 无参数简单列举 */
    public List<ObsObject> simpleList(ListObjectsRequest request){
        ObjectListing result = obsClient.listObjects(request);
        List<ObsObject> list = new ArrayList<>();
        for(ObsObject obsObject : result.getObjects()){
            list.add(obsObject);
        }
        return list;
    }
    /* 指定数目列举 */
    public List<ObsObject> simpleList(ListObjectsRequest request,int n){
        request.setMaxKeys(n);
        List<ObsObject> list = simpleList(request);
        return list;
    }
    /* 指定前缀列举 */
    public List<ObsObject> simpleList(ListObjectsRequest request,String prefix){
        request.setMarker(prefix);
        List<ObsObject> list = simpleList(request);
        return list;
    }
    /* 指定前缀和数目的列举 */
    public List<ObsObject> simpleList(ListObjectsRequest request,int n,String prefix){
        request.setMaxKeys(n);
        request.setMarker(prefix);
        List<ObsObject> list = simpleList(request);
        return list;
    }

    /* 分页列举全部对象 */
    public List<ObsObject> pagingList(ListObjectsRequest request){
        request.setMaxKeys(100);
        List<ObsObject> list = new ArrayList<>();
        ObjectListing result;
        do{
            result = obsClient.listObjects(request);
            for(ObsObject obsObject : result.getObjects()){
                list.add(obsObject);
            }
            request.setMarker(result.getNextMarker());
        }while(result.isTruncated());
        return list;
    }
    /* 列举文件夹中的所有对象 */
    public List<ObsObject> pagingList(ListObjectsRequest request,String prefix){
        request.setPrefix(prefix);
        List<ObsObject> list = pagingList(request);
        return list;
    }

    /* 删除对象 */
    /* 删除单个对象 */
    public String deleteObject(String bucketName,String objectKey){
        obsClient.deleteObject(bucketName, objectKey);
        return "success";
    }

    /* 批量删除对象 */
    public String deleteall(String bucketName){
        ListVersionsRequest request = new ListVersionsRequest(bucketName);
        // 每次批量删除100个对象
        request.setMaxKeys(100);
        ListVersionsResult result;
        do {
            result = obsClient.listVersions(request);

            DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName);

            for(VersionOrDeleteMarker v  : result.getVersions()) {
                deleteRequest.addKeyAndVersion(v.getKey(), v.getVersionId());
            }

            DeleteObjectsResult deleteResult = obsClient.deleteObjects(deleteRequest);
            // 获取删除成功的对象
            System.out.println(deleteResult.getDeletedObjectResults());
            // 获取删除失败的对象
            System.out.println(deleteResult.getErrorResults());

            request.setKeyMarker(result.getNextKeyMarker());
            request.setVersionIdMarker(result.getNextVersionIdMarker());
        }while(result.isTruncated());
        return "success";
    }

    /* 复制对象 */
    /* 简单复制 */
    public String copyObject(String sourceBucketName,String sourceObjectName,String destBucketName,String destObjectName){
        try{
            CopyObjectResult result = obsClient.copyObject(sourceBucketName, sourceObjectName, destBucketName,destObjectName);

            System.out.println("\t" + result.getStatusCode());
            System.out.println("\t" + result.getEtag());
            return result.getEtag();
        }
        catch (ObsException e)
        {
            // 复制失败
            System.out.println("HTTP Code: " + e.getResponseCode());
            System.out.println("Error Code:" + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());

            System.out.println("Request ID:" + e.getErrorRequestId());
            System.out.println("Host ID:" + e.getErrorHostId());
            return "copy failed";
        }
    }

}
