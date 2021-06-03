package com.sdu.broker.service;

import com.sdu.broker.pojo.History;

import java.util.List;

public interface HistoryService {
    List<History> getHistory(Integer user);

    String getUpload(Integer user);

    String getDownload(Integer user);
}
