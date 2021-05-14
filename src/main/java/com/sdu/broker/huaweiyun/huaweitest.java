package com.sdu.broker.huaweiyun;

import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.PartEtag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class huaweitest  {
    public static void main(String [] args) throws IOException {
        HuaweiController hwc = new HuaweiController();
        String bucketname = "brokertest-demo";
        String bucketname1 = "brokertest-demo5";
        String pathname1 = "D:\\ ProjectTraining\\brokertest.txt";
        String objectKey = "test5";
        String objectKey1 = "test.mp4";
        String downloadPath = "D:\\ ProjectTraining\\download\\download2.txt";

        HuaweiUploadController hwc1 = new HuaweiUploadController();
//        Map<String ,String > map = hwc1.appendObjectStreamFirst(bucketname,objectKey,"hello");
//        hwc1.appendObjectStream(Integer.parseInt(map.get("NextPosition")),bucketname,objectKey,"hello1");
        String pathname2 = "D:\\ ProjectTraining\\test.mp4";
        hwc1.concurrentMultipartUpload(pathname2,bucketname1,objectKey1);

//        String uploadid = hwc1.InitiateMultipartUpload(bucketname1,objectKey1,"mp4");
//        List<PartEtag> list = hwc1.uploadPartFirst(pathname2,bucketname1,objectKey1,uploadid);
//        PartEtag pe = hwc1.uploadParts(2,pathname2,bucketname1,objectKey1,uploadid);
//        list.add(pe);
//        pe = hwc1.uploadParts(3,pathname2,bucketname1,objectKey1,uploadid);
//        list.add(pe);
//        pe = hwc1.uploadParts(4,pathname2,bucketname1,objectKey1,uploadid);
//        list.add(pe);
//        hwc1.listPartsAll(bucketname1,objectKey1,uploadid);
//        System.out.println("\t"+ "uploadid:"+ uploadid);
//        String result = hwc1.CompleteMultipartUpload(list,bucketname1,objectKey1,uploadid);
//        System.out.println(result);
//        String s = hwc1.AbortMultipartUpload(bucketname1,objectKey1,"000001796B98CCA947C723F2CD76E0E8");

//        hwc1.listPartsAll(bucketname1,objectKey1,"0000001796BA19D7144CAFE2C6AD95B5C");
//        hwc1.uploadFile(pathname1,bucketname1,objectKey);
//        System.out.println(hwc.removeBucket(bucketname1));
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
//        System.out.println(hwc.getBucketQuota(bucketname));
        hwc.closeObsClient();

    }

}
