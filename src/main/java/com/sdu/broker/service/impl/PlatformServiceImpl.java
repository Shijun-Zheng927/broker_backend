package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.PlatformMapper;
import com.sdu.broker.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatformServiceImpl implements PlatformService {
    @Autowired
    private PlatformMapper platformMapper;

    @Override
    public String getPlatform(Integer id) {
        return platformMapper.getPlatform(id);
    }
}
