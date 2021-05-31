package com.sdu.broker;

import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.TokenUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

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

    @Test
    public void getStringSize() {
        String  str = "你好";
        byte[] buff = str.getBytes();
        double size = (double) buff.length / 1024 / 1024 / 1024;     //GB
        System.out.println(size);
    }

    @Test
    public void getFileSize() {
        File f = new File("D:\\IDEA\\broker\\src\\main\\resources\\file\\0aa881ed-f1b8-497e-abda-f9d97bdb0240test.jpg");
        long by = f.length();
        double size = (double) by / 1024 / 1024 / 1024;     //GB
        System.out.println(size);
    }
}
