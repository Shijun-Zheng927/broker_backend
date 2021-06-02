package com.sdu.broker.controller;

import com.sdu.broker.pojo.UrlPath;
import com.sdu.broker.pojo.User;
import com.sdu.broker.service.UserService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UrlPath urlPath;

    /**
     * 登录
     * @param user
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/login")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<String, Object> login(@RequestBody User user) {
        User login = userService.login(user);
        if (login == null) {
            return null;
        } else {
            String token = TokenUtils.sign(login.getId().toString());
            Map<String, Object> map = new HashMap<>();
            map.put("phone", login.getPhone());
            map.put("username", login.getUsername());
            map.put("token", token);
            map.put("head", login.getHead());
            map.put("email", login.getEmail());
            map.put("company", login.getCompany());
            return map;
        }

    }

    /**
     * 注册
     * @param user
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/register")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer register(@RequestBody User user) {
        return userService.register(user);
    }

    @RequestMapping("/setHead")
    public String setHead(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authorization,
                          HttpServletResponse response) {
        if (!TokenUtils.verify(authorization)) {
            response.setStatus(999);
            return "fail";
        }
        String result = "";
        if (file != null) {
            try {
                String filePath = "D:/IDEA/broker/src/main/resources/static/head/";
                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + file.getOriginalFilename();
//                System.out.println(filePath);
//                System.out.println(fileName);
                File f = new File(filePath + fileName);
                result = urlPath.getUrlPath() + "head/" + fileName;
                System.out.println(result);
                file.transferTo(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Integer userId = Integer.parseInt(TokenUtils.getUserId(authorization));
            userService.setHead(result, userId);
        }
        return result;
    }
}
