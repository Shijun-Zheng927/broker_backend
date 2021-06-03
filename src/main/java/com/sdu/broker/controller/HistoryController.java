package com.sdu.broker.controller;

import com.sdu.broker.service.HistoryService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@RestController
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    @CrossOrigin
    @RequestMapping("/getUpload")
    public String getUpload(@RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getUpload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String result = historyService.getUpload(userId);
        return result;
    }

    @CrossOrigin
    @RequestMapping("/getDownload")
    public String getDownload(@RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("getDownload");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String result = historyService.getDownload(userId);
        return result;
    }


    //工具类
    public boolean verifyIdentity(HttpServletResponse response, String token) {
        if (!TokenUtils.verify(token)) {
            response.setStatus(999);
            return false;
        }
        return true;
    }
}
