package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.RechargeRecordMapper;
import com.sdu.broker.pojo.RechargeRecord;
import com.sdu.broker.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RechargeRecordServiceImpl implements RechargeRecordService {
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public List<RechargeRecord> getRecord(String id) {
        return rechargeRecordMapper.getRecord(id);
    }
}
