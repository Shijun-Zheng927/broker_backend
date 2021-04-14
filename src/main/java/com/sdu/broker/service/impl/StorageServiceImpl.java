package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.StorageMapper;
import com.sdu.broker.pojo.StorageType;
import com.sdu.broker.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageMapper storageMapper;

    @Override
    public List<StorageType> getAli() {
        return storageMapper.getAli();
    }

    @Override
    public List<StorageType> getHuawei() {
        return storageMapper.getHuawei();
    }
}
