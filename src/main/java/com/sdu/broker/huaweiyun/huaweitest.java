package com.sdu.broker.huaweiyun;

import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.PartEtag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class huaweitest  {
    public static void main(String [] args){
        HuaweiController hwc = new HuaweiController();
        String bucketname = "brokertest-demo";
        String bucketname1 = "brokertest-demo5";
        String pathname1 = "D:\\ ProjectTraining\\brokertest.txt";
        String objectKey = "test5";
        String objectKey1 = "test.mp4";
        String pathname = "";
        String downloadPath = "D:\\ ProjectTraining\\download\\download3.txt";
//        HuaweiDownloadController h = new HuaweiDownloadController();
//        GetObjectRequest r = h.newObjectRequest(bucketname,objectKey);
//        h.rangeDownload(r,pathname,0,1000);
//        hwc.listFile(bucketname);
//        HuaweiDownloadController hdc = new HuaweiDownloadController();
//        GetObjectRequest request = hdc.request(bucketname,objectKey);
//        String s = hdc.streamDownload(request);
//        System.out.println(s);
//        hdc.closeObsClient();
        System.out.println("which");

    }

}
