package com.sdu.broker.service;

import com.sdu.broker.pojo.StorageType;
import org.springframework.stereotype.Service;

import java.util.List;

public interface StorageService {
    List<StorageType> getAli();

    List<StorageType> getHuawei();
}
