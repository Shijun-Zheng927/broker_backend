package com.sdu.broker.controller;

import com.sdu.broker.pojo.UrlPath;
import com.sdu.broker.service.IntroduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;
import java.util.UUID;

@Controller
public class IntroduceController {
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private UrlPath urlPath;

    @CrossOrigin
    @RequestMapping("/uploadIntroduce")
    @ResponseBody
    public String uploadIntroduce(@RequestBody Map<String, String> map) {
        String file = map.get("file");
        String name = map.get("name");
        String result = "";
        if (!"".equals(file) && !"".equals(name)) {
            try {
                String filePath = "D:/IDEA/broker/src/main/resources/static/md/";
                String path = filePath + name + ".txt";
                result = path;
//                System.out.println(filePath);
//                System.out.println(fileName);
                File f = new File(path);
                BufferedWriter bw = new BufferedWriter(new FileWriter(path));
                bw.write(file);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            introduceService.addIntroduce(name, result);
        }
        return urlPath.getUrlPath() + "md/" + name + ".txt";
    }

    @CrossOrigin
    @GetMapping(value = "/getIntroduceName", params = {"name"})
    @ResponseBody
    public String uploadIntroduce(@RequestParam String name) {
        return null;
//        String path = introduceService.getPath(name);
//        String markdown = "";
//        String thisLine;
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
//            while((thisLine = br.readLine()) != null) {
//                markdown += thisLine + "\n";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        System.out.println(markdown);
//
//        return markdown;
    }

    @CrossOrigin
    @RequestMapping("/uploadImg")
    @ResponseBody
    public String uploadImg(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            System.out.println("file is null");
            return null;
        }
        String result = "";
        if (file != null) {
            try {
                String filePath = "D:/IDEA/broker/src/main/resources/static/img/";
                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + file.getOriginalFilename();
//                System.out.println(filePath);
//                System.out.println(fileName);
                File f = new File(filePath + fileName);
                result = urlPath.getUrlPath() + "img/" + fileName;
                System.out.println(result);
                file.transferTo(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
