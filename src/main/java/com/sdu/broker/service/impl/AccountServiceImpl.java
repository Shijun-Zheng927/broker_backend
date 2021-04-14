package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.AccountMapper;
import com.sdu.broker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Double getAccount(String id) {
        return accountMapper.getAccount(Integer.valueOf(id));
    }
}
