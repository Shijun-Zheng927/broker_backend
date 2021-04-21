package com.sdu.broker.controller;

import com.sdu.broker.service.AccountService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    /**
     * 获取账户余额
     * @param
     * @return
     */
    @CrossOrigin
    @PostMapping( "/getAccount")
    public Double getAccount(@RequestHeader("Authorization") String authorization,
                             HttpServletResponse response) {
//        System.out.println(map.get("id"));
        if (!TokenUtils.verify(authorization)) {
            response.setStatus(999);
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        return accountService.getAccount(userId.toString());
    }
}
