package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.AccountMapper;
import com.sdu.broker.mapper.RechargeRecordMapper;
import com.sdu.broker.pojo.RechargeRecord;
import com.sdu.broker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public Double getAccount(String id) {
        return accountMapper.getAccount(Integer.valueOf(id));
    }

    @Override
    public Integer recharge(String id, Double amount) {
        Double money = accountMapper.getAccount(Integer.valueOf(id));
        amount += money;
        Integer result = accountMapper.recharge(Integer.valueOf(id), amount);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());// new Date()为获取当前系统时间
        if (result > 0) {
            RechargeRecord record = new RechargeRecord(Integer.parseInt(id), amount - money, time, "success");
            rechargeRecordMapper.addRecord(record);
            return 1;
        } else {
            RechargeRecord record = new RechargeRecord(Integer.parseInt(id), amount - money, time, "fail");
            rechargeRecordMapper.addRecord(record);
            return 0;
        }
    }
}
