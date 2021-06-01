package com.sdu.broker;

import com.sdu.broker.pojo.Bucket;
import com.sdu.broker.service.BucketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BucketServiceTest {
    @Autowired
    private BucketService bucketService;

    @Test
    public void test1() {
        Bucket bucket = new Bucket();
        bucket.setPlatform("ALI");
//        bucket.setName("first");
        bucket.setName("second");
        Integer result = bucketService.isExist(bucket);
        System.out.println(result);
    }

    @Test
    public void test2() {
        Bucket bucket = new Bucket();
        bucket.setUserId(1);
        bucket.setPlatform("ALI");
        bucket.setName("first");
        Integer result = bucketService.isLegal(bucket);
        System.out.println(result);
    }

    @Test
    public void test3() {
        Bucket bucket = new Bucket();
        bucket.setUserId(1);
        bucket.setPlatform("HUAWEI");
        bucket.setName("second");
        Integer result = bucketService.addBucket(bucket);
        System.out.println(result);
    }

    @Test
    public void test4() {
        Bucket bucket = new Bucket();
        bucket.setPlatform("HUAWEI");
        bucket.setName("second");
        Integer result = bucketService.deleteBucket(bucket);
        System.out.println(result);
    }

    @Test
    public void getPlatform() {
        String result = bucketService.getPlatform("haha");
        System.out.println(result);
    }
}
