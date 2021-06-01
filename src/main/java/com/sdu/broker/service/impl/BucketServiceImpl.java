package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.BucketMapper;
import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BucketServiceImpl implements BucketService {
    @Autowired
    private BucketMapper bucketMapper;

    @Override
    public Integer isExist(Bucket bucket) {
        return bucketMapper.isExist(bucket);
    }

    @Override
    public Integer isLegal(Bucket bucket) {
        return bucketMapper.isLegal(bucket);
    }

    @Override
    public Integer addBucket(Bucket bucket) {
        return bucketMapper.addBucket(bucket);
    }

    @Override
    public Integer deleteBucket(Bucket bucket) {
        return bucketMapper.deleteBucket(bucket);
    }

    @Override
    public Integer haveName(String name) {
        return bucketMapper.haveName(name);
    }

    @Override
    public String getPlatform(String name) {
        return bucketMapper.getPlatform(name);
    }
}
