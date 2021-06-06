package com.sdu.broker.pojo.resp;

import com.aliyun.oss.model.OSSObjectSummary;
import com.obs.services.model.ObsObject;

public class RespObject {
    private String bucketName;
    private String key;
    private String eTag;
    private long size;
    private String lastModified;
    private String storageClass;
    private String owner;
    private String type;
    private String metadata;

    public RespObject(OSSObjectSummary objectSummary) {
        this.bucketName = objectSummary.getBucketName();
        this.key = objectSummary.getKey();
        this.eTag = objectSummary.getETag();
        this.size = objectSummary.getSize();
        this.lastModified = objectSummary.getLastModified().toString();
        this.storageClass = objectSummary.getStorageClass();
        this.owner = objectSummary.getOwner().toString();
        this.type = objectSummary.getType();
    }

    public RespObject(ObsObject obsObject) {
        this.bucketName = obsObject.getBucketName();
        this.key = obsObject.getObjectKey();
        this.metadata = obsObject.getMetadata().toString();
        this.owner = obsObject.getOwner().toString();
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
