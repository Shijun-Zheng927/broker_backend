package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.HistoryMapper;
import com.sdu.broker.pojo.History;
import com.sdu.broker.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService {
    @Autowired
    private HistoryMapper historyMapper;

    @Override
    public List<History> getHistory(Integer user) {
        return historyMapper.getHistory(user);
    }

    @Override
    public String getUpload(Integer user) {
        Double upload = historyMapper.getUpload(user);
//        System.out.println(upload);
        String result = "";
        if (upload >= 1.0) {
            result = upload + "GB";
            return result;
        }
        upload *= 1024;
//        System.out.println(upload);
        if (upload >= 1.0) {
            result = upload + "MB";
            return result;
        }
        upload *= 1024;
//        System.out.println(upload);
        if (upload >= 1.0) {
            result = upload + "KB";
            return result;
        }
        upload *= 1024;
//        System.out.println(upload);
        result = upload + "B";
        return result;
    }

    @Override
    public String getDownload(Integer user) {
        Double upload = historyMapper.getDownload(user);
//        System.out.println(upload);
        String result = "";
        if (upload >= 1.0) {
            result = upload + "GB";
            return result;
        }
        upload *= 1024;
//        System.out.println(upload);
        if (upload >= 1.0) {
            result = upload + "MB";
            return result;
        }
        upload *= 1024;
//        System.out.println(upload);
        if (upload >= 1.0) {
            result = upload + "KB";
            return result;
        }
        upload *= 1024;
//        System.out.println(upload);
        result = upload + "B";
        return result;
    }
}
