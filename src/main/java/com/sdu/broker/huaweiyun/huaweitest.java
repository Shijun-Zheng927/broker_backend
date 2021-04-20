package com.sdu.broker.huaweiyun;

import com.obs.services.model.BucketMetadataInfoResult;

import java.io.IOException;

public class huaweitest  {
    public static void main(String [] args) throws IOException {
        HuaweiController hwc = new HuaweiController();
        String bucketname = "brokertest-demo";
        String bucketname1 = "brokertest-demo4";
        String pathname = "D:\\ ProjectTraining\\brokertest.txt";
        String objectKey = "test0";
        String downloadPath = "D:\\ ProjectTraining\\download\\download2.txt";
//        System.out.println(hwc.createBucket(bucketname1,0,1));
//        System.out.println(hwc.removeBucket(bucketname));
//        hwc.listBucket();
//        hwc.uploadFile(pathname,bucketname1,objectKey);
//        hwc.listFile(bucketname1);

//        hwc.getFile(bucketname1,objectKey,downloadPath);
//        hwc.deleteObject(bucketname1,objectKey);
//        System.out.println(hwc.getBucketPolicy(bucketname));
//        System.out.println(hwc.getBucketAcl(bucketname));
//        BucketMetadataInfoResult result1 = hwc.result(bucketname);
//        hwc.removeBucket(bucketname1);
//        hwc.getlocation(bucketname);
//        hwc.getBucketStorageInfo(bucketname);
//        hwc.setBucketQuota(bucketname,1024);
//        System.out.println(hwc.getBucketStorageClass(bucketname));
//        hwc.setBucketPolicy(bucketname,"Allow");
//        hwc.getBucketStorageClass(bucketname);
//        hwc.getresult(bucketname);
//        System.out.println(hwc.getBucketAcl(bucketname));
//        System.out.println(hwc.getBucketPolicy(bucketname));
        System.out.println(hwc.getBucketQuota(bucketname));
        hwc.closeObsClient();

    }

}
