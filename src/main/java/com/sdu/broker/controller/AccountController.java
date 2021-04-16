package com.sdu.broker.controller;

import com.sdu.broker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    /**
     * 获取账户余额
     * @param map
     * @return
     */
    @CrossOrigin
    @PostMapping( "/getAccount")
    public Double getAccount(@RequestBody Map<String, Object> map) {
//        System.out.println(map.get("id"));
        return accountService.getAccount(map.get("id").toString());
    }
}
