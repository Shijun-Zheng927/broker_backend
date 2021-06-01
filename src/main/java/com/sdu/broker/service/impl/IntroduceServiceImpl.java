package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.IntroduceMapper;
import com.sdu.broker.pojo.Introduce;
import com.sdu.broker.service.IntroduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntroduceServiceImpl implements IntroduceService {
    @Autowired
    private IntroduceMapper introduceMapper;

    @Override
    public Integer addIntroduce(String name, String path) {
        Introduce introduce = new Introduce();
        introduce.setName(name);
        introduce.setFile(path);
        Integer result = introduceMapper.addIntroduce(introduce);
        return result;
    }

    @Override
    public String getPath(String name) {
        return introduceMapper.getPath(name);
    }
}
