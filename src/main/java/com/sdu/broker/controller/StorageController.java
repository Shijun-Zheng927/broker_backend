package com.sdu.broker.controller;

import com.sdu.broker.pojo.StorageType;
import com.sdu.broker.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StorageController {
    @Autowired
    private StorageService storageService;

    @CrossOrigin
    @GetMapping(value = "/getAli")
    public List<StorageType> getAli() {
        return storageService.getAli();
    }

    @CrossOrigin
    @GetMapping(value = "/getHuawei")
    public List<StorageType> getHuawei() {
        return storageService.getHuawei();
    }
}
