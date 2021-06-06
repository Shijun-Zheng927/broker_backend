package com.sdu.broker.pojo.resp;

import com.aliyun.oss.model.BucketInfo;
import com.obs.services.model.BucketMetadataInfoResult;

import java.util.Date;

public class RespBucket {
    private String bucketName;
    private String storageClass;
    private String location;
    private String creationDate;
    private String extranetEndpoint;
    private String intranetEndpoint;
    private String region;

    public RespBucket(BucketInfo info) {
        this.bucketName = info.getBucket().getName();
        this.storageClass = info.getBucket().getStorageClass().toString();
        this.location = info.getBucket().getLocation();
        this.creationDate = info.getBucket().getCreationDate().toString();
        this.extranetEndpoint = info.getBucket().getExtranetEndpoint();
        this.intranetEndpoint = info.getBucket().getIntranetEndpoint();
        this.region = info.getBucket().getRegion();
    }

    public RespBucket(BucketMetadataInfoResult info) {
        this.storageClass = info.getBucketStorageClass().toString();
        this.location = info.getLocation();
    }

    @Override
    public String toString() {
        return "RespBucket{" +
                "bucketName='" + bucketName + '\'' +
                ", storageClass='" + storageClass + '\'' +
                ", location='" + location + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", extranetEndpoint='" + extranetEndpoint + '\'' +
                ", intranetEndpoint='" + intranetEndpoint + '\'' +
                ", region='" + region + '\'' +
                '}';
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getExtranetEndpoint() {
        return extranetEndpoint;
    }

    public void setExtranetEndpoint(String extranetEndpoint) {
        this.extranetEndpoint = extranetEndpoint;
    }

    public String getIntranetEndpoint() {
        return intranetEndpoint;
    }

    public void setIntranetEndpoint(String intranetEndpoint) {
        this.intranetEndpoint = intranetEndpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
