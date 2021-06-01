package com.sdu.broker.controller;

import com.sdu.broker.service.IntroduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class IntroduceController {
    @Autowired
    private IntroduceService introduceService;

    @CrossOrigin
    @RequestMapping("/uploadIntroduce")
    public String uploadIntroduce(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        String result = "";
        if (file != null && !"".equals(name)) {
            try {
                String filePath = "D:/IDEA/broker/src/main/resources/static/md/";
                String fileName = file.getOriginalFilename();
//                System.out.println(filePath);
//                System.out.println(fileName);
                File f = new File(filePath + fileName);
                result = "http://localhost:8443/md/" + fileName;
                System.out.println(result);
                file.transferTo(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

            introduceService.addIntroduce(name, result);
        }
        return result;
    }

//    @CrossOrigin
    @GetMapping(value = "/getIntroduceName", params = {"name"})
    @ResponseBody
    public String uploadIntroduce(@RequestParam String name) {
        String path = introduceService.getPath(name);
        return path;
    }

    @CrossOrigin
    @RequestMapping("/uploadImg")
    public String uploadImg(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        String result = "";
        if (file != null) {
            try {
                String filePath = "D:/IDEA/broker/src/main/resources/static/img/";
                String fileName = file.getOriginalFilename();
//                System.out.println(filePath);
//                System.out.println(fileName);
                File f = new File(filePath + fileName);
                result = "http://localhost:8443/img/" + fileName;
                System.out.println(result);
                file.transferTo(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
