package com.sdu.broker;

import com.sdu.broker.utils.BucketUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsTest {
    @Test
    public void addPrefix() {
        String result = BucketUtils.addPrefix("hello");
        System.out.println(result);
    }

    @Test
    public void deletePrefix() {
        String result = BucketUtils.deletePrefix("broker-system-sdu-hello");
        System.out.println(result);
    }
}
