package com.sdu.broker.pojo.req;

import java.util.List;

public class CompleteMultipartUpload {
    private List<String> etag;
    private List<Integer> partNumber;
    private String bucketName;
    private String objectKey;
    private String uploadId;

    public List<String> getEtag() {
        return etag;
    }

    public void setEtag(List<String> etag) {
        this.etag = etag;
    }

    public List<Integer> getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(List<Integer> partNumber) {
        this.partNumber = partNumber;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
