package com.sdu.broker.utils;

public class BucketUtils {
    public static String addPrefix(String s) {
        return "broker-system-sdu-" + s;
    }

    public static String deletePrefix(String s) {
        return s.substring(18);
    }
}
