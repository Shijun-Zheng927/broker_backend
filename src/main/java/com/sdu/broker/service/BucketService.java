package com.sdu.broker.service;

import com.sdu.broker.pojo.Bucket;

public interface BucketService {
    Integer isExist(Bucket bucket);

    Integer isLegal(Bucket bucket);

    Integer addBucket(Bucket bucket);

    Integer deleteBucket(Bucket bucket);

    Integer haveName(String name);
}
