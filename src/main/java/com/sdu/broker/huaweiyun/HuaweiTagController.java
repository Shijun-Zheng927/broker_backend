package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.BucketTagInfo;

import java.util.HashMap;
import java.util.Map;

public class HuaweiTagController {
    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    public BucketTagInfo.TagSet newTagSet(){
        BucketTagInfo.TagSet tagSet = new BucketTagInfo.TagSet();
        return tagSet;
    }

    public BucketTagInfo.TagSet addTag(BucketTagInfo.TagSet tagSet , String tag , String value){
        tagSet.addTag(tag, value);
        return tagSet;
    }

    public String setBucketTag(BucketTagInfo.TagSet tagSet,String bucketName){
        try {
            BucketTagInfo bucketTagInfo = new BucketTagInfo();
            bucketTagInfo.setTagSet(tagSet);
            obsClient.setBucketTagging(bucketName, bucketTagInfo);
        }catch (ObsException e){
            return "failed";
        }
        return "success";
    }

    public Map<String,String> getBucketTagging(String bucketName){
        BucketTagInfo bucketTagInfo = obsClient.getBucketTagging(bucketName);
        Map<String,String> map = new HashMap<>();
        for(BucketTagInfo.TagSet.Tag tag : bucketTagInfo.getTagSet().getTags()){
            System.out.println("\t" + tag.getKey() + ":" + tag.getValue());
            map.put(tag.getKey(),tag.getValue());
        }
        return map;
    }

    public String deleteBucketTagging(String bucketName){
        try {
            obsClient.deleteBucketTagging(bucketName);
        }catch (ObsException e){
            return "failed";
        }
        return "success";
    }
}
