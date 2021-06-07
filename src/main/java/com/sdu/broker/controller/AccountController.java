package com.sdu.broker.controller;

import com.sdu.broker.service.AccountService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
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
    public String getAccount(@RequestHeader("Authorization") String authorization,
                             HttpServletResponse response) {
//        System.out.println(map.get("id"));
        if (!TokenUtils.verify(authorization)) {
            response.setStatus(999);
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        DecimalFormat df = new DecimalFormat("#.00");
        Double result = accountService.getAccount(userId.toString());
        return df.format(result);
    }
}
