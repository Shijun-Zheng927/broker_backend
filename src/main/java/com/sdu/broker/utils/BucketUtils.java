package com.sdu.broker.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BucketUtils {
    public static String addPrefix(String s) {
        return "broker-system-sdu-" + s;
    }

    public static String deletePrefix(String s) {
        return s.substring(18);
    }

    public static boolean regex(int start, int end, String s) {
        Pattern pattern = Pattern.compile("[" + start + "-" + end + "]*");
        Matcher isRight = pattern.matcher(s);
        if (!isRight.matches()) {
            return false;
        }
        return true;
    }
}
