package com.sdu.broker.utils;

import java.io.File;

public class FileUtils {
    public static double getStringSize(String s) {
        byte[] buff = s.getBytes();
        double size = (double) buff.length / 1024 / 1024 / 1024;     //GB
//        System.out.println(size);
        return size;
    }

    public static double getFileSize(String path) {
        File f = new File(path);
        long by = f.length();
        double size = (double) by / 1024 / 1024 / 1024;     //GB
//        System.out.println(size);
        return size;
    }
}
