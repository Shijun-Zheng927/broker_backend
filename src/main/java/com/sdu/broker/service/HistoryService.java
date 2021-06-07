package com.sdu.broker.service;

import com.sdu.broker.pojo.History;

import java.util.List;
import java.util.Map;

public interface HistoryService {
    List<History> getHistory(Integer user);

    String getUpload(Integer user);

    String getDownload(Integer user);

    Map<String, String> getBucketFlow(String bucketName);
}
