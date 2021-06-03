package com.sdu.broker.APIController;

import com.obs.services.model.CopyObjectRequest;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ObsObject;
import com.sdu.broker.huaweiyun.HuaweiObjectController;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class APIObjectController {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private HuaweiObjectController huaweiObjectController;

    @ResponseBody
    @RequestMapping(value = "/demo", method = RequestMethod.POST)
    public String demo(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            //在此获取其他参数并验证
            String acl = map.get("rwPolicy");
            if ("".equals(acl)) {
                //设置默认值
                acl = "0";
            }

            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return "result";
        } else {
            String rwPolicy = map.get("rwPolicy");
            if ("".equals(rwPolicy)) {
                //设置默认值
                rwPolicy = "0";
            }

            //华为云在此进行方法调用
//            huaweiController.setBucketAcl(bucketName, Integer.parseInt(rwPolicy));

            //返回结果
            return "result";
        }
    }


    @ResponseBody
    @RequestMapping(value = "/ifObjectExist", method = RequestMethod.POST)
    public String ifObjectExist(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String ObjectKey = map.get("ObjectKey");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            //在此获取其他参数并验证
            String acl = map.get("rwPolicy");
            if ("".equals(acl)) {
                //设置默认值
                acl = "0";
            }

            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return "result";
        }
        else {
            boolean ifExist = huaweiObjectController.ifObjectExist(bucketName,ObjectKey);
            if(ifExist) return "true";
            else return "false";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleList", method = RequestMethod.POST)
    public List<String> simpleList(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            List<String> result = new ArrayList<>();


            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return result;
        }
        else {
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.simpleList(request);
            List<String> result = new ArrayList<>();
            for(ObsObject o : list){result.add(o.toString());}
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleListWithNum", method = RequestMethod.POST)
    public List<String> simpleListWithNum(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        int number = Integer.parseInt(map.get("number"));
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            List<String> result = new ArrayList<>();


            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return result;
        }
        else {
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.simpleList(request,number);
            List<String> result = new ArrayList<>();
            for(ObsObject o : list){result.add(o.toString());}
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleListWithPrefix", method = RequestMethod.POST)
    public List<String> simpleListWithPrefix(@RequestBody Map<String, String> map,
                                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String prefix = map.get("prefix");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            List<String> result = new ArrayList<>();


            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return result;
        }
        else {
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.simpleList(request,prefix);
            List<String> result = new ArrayList<>();
            for(ObsObject o : list){result.add(o.toString());}
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleListWithNumPrefix", method = RequestMethod.POST)
    public List<String> simpleListWithNumPrefix(@RequestBody Map<String, String> map,
                                             @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        int number = Integer.parseInt(map.get("number"));
        String prefix = map.get("prefix");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            List<String> result = new ArrayList<>();


            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return result;
        }
        else {
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.simpleList(request,number,prefix);
            List<String> result = new ArrayList<>();
            for(ObsObject o : list){result.add(o.toString());}
            return result;
        }
    }
    @ResponseBody
    @RequestMapping(value = "/pagingList", method = RequestMethod.POST)
    public List<String> pagingList(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            List<String> result = new ArrayList<>();


            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return result;
        }
        else {
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.pagingList(request);
            List<String> result = new ArrayList<>();
            for(ObsObject o : list){result.add(o.toString());}
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/pagingListWithPrefix", method = RequestMethod.POST)
    public List<String> pagingListWithPrefix(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String prefix = map.get("prefix");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            List<String> result = new ArrayList<>();


            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return result;
        }
        else {
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.pagingList(request,prefix);
            List<String> result = new ArrayList<>();
            for(ObsObject o : list){result.add(o.toString());}
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteObject", method = RequestMethod.POST)
    public String deleteObject(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            //在此获取其他参数并验证
            String acl = map.get("rwPolicy");
            if ("".equals(acl)) {
                //设置默认值
                acl = "0";
            }

            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return "result";
        } else {
            String result = huaweiObjectController.deleteObject(bucketName,objectKey);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/copyObject", method = RequestMethod.POST)
    public String copyObject(@RequestBody Map<String, String> map,
                               @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            //在此获取其他参数并验证
            String acl = map.get("rwPolicy");
            if ("".equals(acl)) {
                //设置默认值
                acl = "0";
            }

            //阿里云在此调用方法
//            String result = bucketController.setBucketAcl(bucketName, Integer.parseInt(acl));

            //返回结果
            return "result";
        } else {
            /*
            如果复制成功则返回新对象的etag
            失败则返回“copy failed”
             */
            String sourceBucketName = map.get("sourceBucketName");
            String sourceObjectName = map.get("sourceObjectName");
            String destBucketName = map.get("destBucketName");
            String destObjectName = map.get("destObjectName");
            CopyObjectRequest request = new CopyObjectRequest(sourceBucketName,sourceObjectName,destBucketName,destObjectName);
            String result = huaweiObjectController.copyObject(request);
            return result;
        }
    }


    //工具方法
    public boolean verify(HttpServletResponse response, Integer userId, String bucketName) {
        if ("".equals(bucketName)) {
            response.setStatus(777);
            return true;
        }
//        Bucket bucket = new Bucket(userId, platform, bucketName);
        Integer legal = bucketService.verify(userId.toString(), bucketName);
        if (legal == null) {
            response.setStatus(666);
            return true;
        }
        return false;
    }

    public boolean verifyIdentity(HttpServletResponse response, String token) {
        if (!TokenUtils.verify(token)) {
            response.setStatus(999);
            return false;
        }
        return true;
    }
}
