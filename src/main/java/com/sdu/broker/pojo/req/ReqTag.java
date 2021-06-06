package com.sdu.broker.pojo.req;

import java.util.List;

public class ReqTag {
    private List<String> tagKey;
    private List<String> tagValue;
    private String bucketName;

    public List<String> getTagKey() {
        return tagKey;
    }

    public void setTagKey(List<String> tagKey) {
        this.tagKey = tagKey;
    }

    public List<String> getTagValue() {
        return tagValue;
    }

    public void setTagValue(List<String> tagValue) {
        this.tagValue = tagValue;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
