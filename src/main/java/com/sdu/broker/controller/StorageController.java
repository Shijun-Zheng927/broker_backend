package com.sdu.broker.controller;

import com.sdu.broker.pojo.StorageType;
import com.sdu.broker.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StorageController {
    @Autowired
    private StorageService storageService;

    @CrossOrigin
    @RequestMapping(value = "/getAli", method = RequestMethod.GET)
    public List<StorageType> getAli() {
        return storageService.getAli();
    }

    @CrossOrigin
    @RequestMapping(value = "/getHuawei", method = RequestMethod.GET)
    public List<StorageType> getHuawei() {
        return storageService.getHuawei();
    }
}
