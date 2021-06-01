package com.sdu.broker.service;

import com.sdu.broker.pojo.Introduce;

public interface IntroduceService {
    Integer addIntroduce(String name, String path);

    String getPath(String name);
}
