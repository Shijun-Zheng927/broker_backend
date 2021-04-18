package com.sdu.broker;

import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.TokenUtils;
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

    @Test
    public void regex() {
        boolean result = BucketUtils.regex(1, 1, "2");
        System.out.println(result);
    }

    @Test
    public void sign() {
        String result = TokenUtils.sign("1");
        System.out.println(result);
    }

    @Test
    public void isNumber() {
        boolean result = BucketUtils.isNumber("100");
        System.out.println(result);
    }
}
