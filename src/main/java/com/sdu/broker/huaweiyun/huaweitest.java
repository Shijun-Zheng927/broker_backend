package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.model.*;
import com.sun.org.apache.xalan.internal.xsltc.dom.SimpleResultTreeImpl;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class huaweitest  {
    public static void main(String [] args) throws IOException {
        HuaweiController hwc = new HuaweiController();
        HuaweiDownloadController hdc = new HuaweiDownloadController();
        HuaweiBuckectLoggingController hblc = new HuaweiBuckectLoggingController();
        HuaweiUploadController huc = new HuaweiUploadController();
        HuaweiObjectController hoc = new HuaweiObjectController();
        HuaweiTagController htc = new HuaweiTagController();

        String bucketname1 = "brokertest-standard";
        String bucketname2 = "brokertest-warm";
        String bucketname3 = "brokertest-cold";
        String pathname1 = "D:\\ ProjectTraining\\brokertest.txt";
        String pathname2 = "D:\\ ProjectTraining\\test.mp4";
        String pathname3 = "D:\\ ProjectTraining\\test2.mp4";
        String objectKey = "test1.txt";
        String objectKey1 = "test.mp4";
        String objectKey2 = "test2.mp4";
        String objectKeynotexist = "notexist";
        String downloadPath = "D:\\ ProjectTraining\\download\\download4.txt";
        String downloadPath1 = "D:\\ ProjectTraining\\download\\test.mp4";


//        huc.concurrentMultipartUpload(pathname3,bucketname1,objectKey2);

        String uploadid = huc.InitiateMultipartUpload(bucketname1,objectKey2,"video");
        String etag1 = huc.uploadPartFirst(pathname3,bucketname1,objectKey2,uploadid);
        String etag2 = huc.uploadParts(2,pathname3,bucketname1,objectKey2,uploadid);
        String etag3 = huc.uploadParts(3,pathname3,bucketname1,objectKey2,uploadid);
        System.out.println(etag1);
        System.out.println(etag2);
        System.out.println(etag3);
        PartEtag e1 = new PartEtag(etag1.substring(16,48),Integer.parseInt(etag1.substring(62,63)));
        PartEtag e2 = new PartEtag(etag2.substring(16,48),Integer.parseInt(etag2.substring(62,63)));
        PartEtag e3 = new PartEtag(etag3.substring(16,48),Integer.parseInt(etag3.substring(62,63)));
        List<PartEtag> partEtagList = new ArrayList<PartEtag>();
        partEtagList.add(e1);partEtagList.add(e2);partEtagList.add(e3);
        System.out.println(huc.CompleteMultipartUpload(partEtagList,bucketname1,objectKey2,uploadid));

//        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketname1, objectKey1, uploadid, partEtagList);
//
//        String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
//        String ak           = "XR4PD1I3LLF52K1KDRRG";
//        String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
//        ObsClient obsClient1 = new ObsClient(ak,sk,endPoint);
//        obsClient1.completeMultipartUpload(completeMultipartUploadRequest);
//        obsClient1.close();








//        ListObjectsRequest requestl1 = hoc.newListRequest(bucketname1);
//        List<ObsObject> list1 = hoc.simpleList(requestl1,"prefix/");
//        for (ObsObject o : list1){
//            System.out.println("11:"+o.getObjectKey());
//        }
//        String targetprefix = new String("prefix/");
//        System.out.println(hblc.setBuckectLogging(bucketname1,targetprefix,bucketname1));

//        System.out.println(hblc.shutdownBucketLogging(bucketname1));
//        GetObjectRequest request = hdc.newObjectRequest(bucketname1,objectKeynotexist);
//        System.out.println(hdc.streamDownload(request));
//        System.out.println(hblc.getBucketLogging(bucketname1));


//        System.out.println(hwc.setBucketAclForLog(bucketname1));
//        System.out.println(hblc.shutdownBucketLogging(bucketname1));
//        hwc.setBucketAcl(bucketname1,4);
//        hwc.setBucketAcl(bucketname2,4);

//        BucketTagInfo.TagSet tagSet = htc.newTagSet();
//        htc.addTag(tagSet,"tag1","handsome");
//        htc.addTag(tagSet,"tag2","ugly");
//        System.out.println(htc.setBucketTag(tagSet,bucketname1));
//        Map<String,String> map = htc.getBucketTagging(bucketname1);
//        System.out.println(map);
//        System.out.println(htc.deleteBucketTagging(bucketname1));

//        hoc.deleteObject(bucketname2,objectKey1);
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = sdf.parse("2021-07-01");
//            CopyObjectRequest request = hoc.newCopyRequest(bucketname1,objectKey1,bucketname2,objectKey1);
//            request = hoc.setIfUnModifiedSince(request,date);
//            request = hoc.setAcl(request,3);
//            request = hoc.resetContentType(request,"video");
//            System.out.println(hoc.resetCopyObject(request));
//        }catch (Exception e){}
//        ObjectMetadata m = hoc.getMetadata(bucketname2,objectKey1);
//        System.out.println(m.getContentType());


//        System.out.println("1111:"+hoc.getObjectAcl(bucketname1,objectKey1));
//        System.out.println("2222:"+hoc.getObjectAcl(bucketname2,objectKey1));

//        hoc.deleteall(bucketname1);
//        ListObjectsRequest request = hoc.newListRequest(bucketname1);
//        List<ObsObject> list = hoc.simpleList(request,"test1");
//        for(ObsObject o : list){
//            System.out.println(o.getObjectKey());
//        }

//        DownloadFileRequest request = hdc.newDownloadRequest(bucketname1,objectKey1);
//        System.out.println(hdc.checkpointDownload(request,downloadPath1));

//        System.out.println(huc.concurrentMultipartUpload(pathname2,bucketname1,objectKey1));
//        System.out.println(hdc.getRestoreObject(bucketname3,objectKey,0,1));
//        GetObjectRequest request = new GetObjectRequest(bucketname3,objectKey);
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = sdf.parse("2021-07-01");
//            hdc.setIfUnModifiedSince(request,date);
//            System.out.println(hdc.rangeDownload(request,downloadPath,0,10));
//        }catch (Exception e){
//
//        }
//        上传
//        huc.uploadFile(pathname1,bucketname1,objectKey);
//        huc.uploadFile(pathname1,bucketname2,objectKey);
//        huc.uploadFile(pathname1,bucketname3,objectKey);
//        列举对象
//        ListObjectsRequest requestl1 = hoc.newListRequest(bucketname1);
//        List<ObsObject> list1 = hoc.simpleList(requestl1);
//        for (ObsObject o : list1){
//            System.out.println("11:"+o.getObjectKey());
//        }
//        ListObjectsRequest requestl2 = hoc.newListRequest(bucketname2);
//        List<ObsObject> list2 = hoc.simpleList(requestl2);
//        for (ObsObject o : list2){
//            System.out.println("22:"+o.getObjectKey());
//        }
//        ListObjectsRequest requestl3 = hoc.newListRequest(bucketname3);
//        List<ObsObject> list3 = hoc.simpleList(requestl3);
//        for (ObsObject o : list3){
//            System.out.println("33:"+o.getObjectKey());
//        }

//        创建桶
//        hwc.createBucket(bucketname1,1,0);
//        hwc.createBucket(bucketname2,1,1);
//        hwc.createBucket(bucketname3,1,2);
//        列举桶
//        List<ObsBucket> buckets = hwc.listBucket();
//        for (ObsBucket bucket : buckets){
//            System.out.println("bucketname:"+bucket.getBucketName()+" acl "+hwc.getBucketAcl(bucket.getBucketName()));
//            System.out.println("bucketname:"+bucket.getBucketName()+" StorageClass "+hwc.getBucketStorageClass(bucket.getBucketName()));
//        }




        hwc.closeObsClient();
        hblc.closeObsClient();
        hdc.closeObsClient();
        hoc.closeObsClient();
        huc.closeObsClient();
        htc.closeObsClient();
    }

}
