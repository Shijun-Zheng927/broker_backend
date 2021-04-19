package com.sdu.broker.huaweiyun.oss;

import com.obs.services.ObsClient;

import java.io.IOException;

public class huaweitest  {
    public static void main(String [] args) throws IOException {
        HuaweiController hwc = new HuaweiController();
        String bucketname = "brokertest-demo";
        String bucketname1 = "brokertest-demo2";
        String pathname = "D:\\ ProjectTraining\\brokertest.txt";
        String objectKey = "test0";
        String downloadPath = "D:\\ ProjectTraining\\download\\download2.txt";
        hwc.createBucket(bucketname1,0,1);
//        hwc.listBucket();
//        hwc.uploadFile(pathname,bucketname1,objectKey);
//        hwc.listFile(bucketname1);

//        hwc.getFile(bucketname1,objectKey,downloadPath);
//        hwc.deleteObject(bucketname1,objectKey);
        hwc.removeBucket(bucketname1);
        hwc.closeObsClient();

    }

}
