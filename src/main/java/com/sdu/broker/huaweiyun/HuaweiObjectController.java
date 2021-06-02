package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class HuaweiObjectController {
    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);
    public static ObjectMetadata newObjectMetadata = new ObjectMetadata();

    /* 设置对象属性 */
    /* 新建设置属性请求 */
    public SetObjectMetadataRequest newSetRequest(String bucketName,String objectKey){
        SetObjectMetadataRequest request = new SetObjectMetadataRequest(bucketName,objectKey);
        return request;
    }

    /* 设置对象HTTP头域 */
    public String setHttp(SetObjectMetadataRequest request,String contentType,String expires){
        try {
            request.setContentType(contentType);
            request.setExpires(expires);
            obsClient.setObjectMetadata(request);
        }catch (ObsException e ){

        }

        return "success";
    }

    /* 设置自定义元数据 */
    public String addUserMetadata(SetObjectMetadataRequest request,String property,String propertyValue){
        request.addUserMetadata(property, propertyValue);
        obsClient.setObjectMetadata(request);
        return "success";
    }

    /* 获取对象属性 */
    public ObjectMetadata getMetadata(String bucketName,String objectKey){
        ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName, objectKey);
        return metadata;
    }

    /* 获取对象权限 */
    public AccessControlList getObjectAcl(String bucketName,String objectKey){
        AccessControlList acl = obsClient.getObjectAcl(bucketName, objectKey);
        return acl;
    }

    //对象是否存在
    public boolean ifObjectExist(String bucketName,String objectKey){
        boolean exist = obsClient.doesObjectExist(bucketName,objectKey);
        return  exist;
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
    /* 新建复制请求 */
    public CopyObjectRequest newCopyRequest(String sourceBucketName,String sourceObjectName,String destBucketName,String destObjectName){
        CopyObjectRequest request = new CopyObjectRequest(sourceBucketName,sourceObjectName,destBucketName,destObjectName);
        request.setReplaceMetadata(false);
        return request;
    }
    /* 简单复制 */
    public String copyObject(CopyObjectRequest request){
        try{
            CopyObjectResult result = obsClient.copyObject(request);

            System.out.println("\t" + result.getStatusCode());
            System.out.println("\t" + result.getEtag());
            return result.getEtag();
        }
        catch (ObsException e)
        {
            // 复制失败
//            System.out.println("HTTP Code: " + e.getResponseCode());
//            System.out.println("Error Code:" + e.getErrorCode());
//            System.out.println("Error Message: " + e.getErrorMessage());
//
//            System.out.println("Request ID:" + e.getErrorRequestId());
//            System.out.println("Host ID:" + e.getErrorHostId());
            return "copy failed";
        }
    }


    /* 复制时重写对象属性 */
    //重写类型
    public CopyObjectRequest resetContentType(CopyObjectRequest request,String type){
        request.setReplaceMetadata(true);
        newObjectMetadata.setContentType(type);
        return request;
    }
    //重写自定义元数据
    public CopyObjectRequest resetUserMetadata(CopyObjectRequest request,String property,String propertyValue){
        request.setReplaceMetadata(true);
        newObjectMetadata.addUserMetadata(property,propertyValue);
        return request;
    }
    //重写存储类型
    public CopyObjectRequest resetStorageClass(CopyObjectRequest request,int type){
        request.setReplaceMetadata(true);
        switch (type){
            case 0:
            {newObjectMetadata.setObjectStorageClass(StorageClassEnum.STANDARD);                    break;}
            case 1:
            {newObjectMetadata.setObjectStorageClass(StorageClassEnum.COLD);                    break;}
            case 2:
            {newObjectMetadata.setObjectStorageClass(StorageClassEnum.WARM);                    break;}
        }
        return request;
    }
    //重写属性后的复制
    public CopyObjectRequest resetCopyObject(CopyObjectRequest request){
        request.setNewObjectMetadata(newObjectMetadata);
        newObjectMetadata = new ObjectMetadata();
        copyObject(request);
        return request;
    }

    /* 限定条件复制 */
    public CopyObjectRequest  setIfModifiedSince(CopyObjectRequest request, Date date){
        request.setIfModifiedSince(date);
        return request;
    }
    public CopyObjectRequest  setIfUnModifiedSince(CopyObjectRequest request, Date date){
        request.setIfUnmodifiedSince(date);
        return request;
    }
    public CopyObjectRequest  setIfMatchTag(CopyObjectRequest request,String etag){
        request.setIfMatchTag(etag);
        return request;
    }
    public CopyObjectRequest  setIfNoneMatchTag(CopyObjectRequest request,String etag){
        request.setIfNoneMatchTag(etag);
        return request;
    }

    /* 重写对象访问权限 */
    public CopyObjectRequest setAcl(CopyObjectRequest request,int acl){
        switch (acl){
            case 0:{
                request.setAcl(AccessControlList.REST_CANNED_PRIVATE);                    break;
            }
            case 1:{
                request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);                    break;
            }
            case 2:{
                request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ_DELIVERED);                    break;
            }
            case 3:{
                request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE);                    break;
            }
            case 4:{
                request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED);                    break;
            }
        }
        return request;
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
