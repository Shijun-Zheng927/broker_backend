package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.BucketTagInfo;
import com.obs.services.model.ListBucketsRequest;
import com.obs.services.model.ObsBucket;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
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


    public String setOneTag(String bucketName, String key , String value){
        BucketTagInfo.TagSet tag = newTagSet();
        tag.addTag(key,value);
        String result = setBucketTag(tag,bucketName);
        return result;
    }
    public Map<String,String> getBucketTagging(String bucketName){
        Map<String,String> map = new HashMap<>();
        try {
            BucketTagInfo bucketTagInfo = obsClient.getBucketTagging(bucketName);
            if (bucketTagInfo != null){
                for(BucketTagInfo.TagSet.Tag tag : bucketTagInfo.getTagSet().getTags()){
                    System.out.println("\t" + tag.getKey() + ":" + tag.getValue());
                    map.put("tagValue",tag.getValue());
                    map.put("tagKey",tag.getKey());
            }
        }
        }catch (ObsException e){
            return null;
        }
        return map;
    }

    public List<ObsBucket> listBucketByTag(String tagKey, String tagValue){
        ListBucketsRequest request = new ListBucketsRequest();
        request.setQueryLocation(true);
        List<ObsBucket> buckets = obsClient.listBuckets(request);
        List<ObsBucket> result = new ArrayList<>();
        for (ObsBucket bucket : buckets){
            Map<String,String> map = getBucketTagging(bucket.getBucketName());
            if (map != null){
                String value = map.get("tagValue");
                String key = map.get("tagKey");
            if (value.equals(tagValue)&&key.equals(tagKey)){
                result.add(bucket);
            }
            }
        }
        return result;

    }
    public String deleteBucketTagging(String bucketName){
        try {
            obsClient.deleteBucketTagging(bucketName);
        }catch (ObsException e){
            return "failed";
        }
        return "success";
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
