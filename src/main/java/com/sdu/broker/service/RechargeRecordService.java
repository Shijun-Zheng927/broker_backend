package com.sdu.broker.service;

import com.sdu.broker.pojo.RechargeRecord;

import java.util.List;

public interface RechargeRecordService {
    List<RechargeRecord> getRecord(String id);
}
