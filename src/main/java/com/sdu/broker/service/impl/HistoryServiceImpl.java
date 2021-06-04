package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.HistoryMapper;
import com.sdu.broker.pojo.History;
import com.sdu.broker.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (upload == null) {
            return "None";
        }
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
        if (upload == null) {
            return "None";
        }
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
    public Map<String, String> getBucketFlow(String bucketName) {
        Double bucketUpFlow = historyMapper.getBucketUpFlow(bucketName);
        Double bucketDownFlow = historyMapper.getBucketDownFlow(bucketName);

        Map<String, String> result = new HashMap<>();
        if (bucketUpFlow == null) {
            result.put("upload", "None");
        } else {
            result.put("upload", transfor(bucketUpFlow));
        }
        if (bucketDownFlow == null) {
            result.put("download", "None");
        } else {
            result.put("download", transfor(bucketDownFlow));
        }
        return result;
    }

    public String transfor(Double flow) {
        String result = "";
        if (flow >= 1.0) {
            result = flow + "GB";
            return result;
        }
        flow *= 1024;
//        System.out.println(upload);
        if (flow >= 1.0) {
            result = flow + "MB";
            return result;
        }
        flow *= 1024;
//        System.out.println(upload);
        if (flow >= 1.0) {
            result = flow + "KB";
            return result;
        }
        flow *= 1024;
//        System.out.println(upload);
        result = flow + "B";
        return result;
    }
}
