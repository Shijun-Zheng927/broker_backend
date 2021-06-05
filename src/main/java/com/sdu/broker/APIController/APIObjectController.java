package com.sdu.broker.APIController;

import com.obs.services.model.CopyObjectRequest;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ObsObject;
import com.sdu.broker.aliyun.oss.AliObjectController;
import com.sdu.broker.huaweiyun.HuaweiObjectController;
import com.sdu.broker.service.BucketService;
import com.sdu.broker.utils.BucketUtils;
import com.sdu.broker.utils.ControllerUtils;
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
    @Autowired
    private AliObjectController aliObjectController;


    @ResponseBody
    @RequestMapping(value = "/ifObjectExist", method = RequestMethod.POST)
    public String ifObjectExist(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("ifObjectExist");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        if (objectKey == null || "".equals(objectKey)) {
            response.setStatus(777);
            return null;
        }
        String platform = bucketService.getPlatform(bucketName);
        if (platform.equals("ALI")) {
            boolean doesObjectExist = aliObjectController.doesObjectExist(bucketName, objectKey);
            if (doesObjectExist) {
                return "true";
            }
            else return "false";
        }
        else {
            boolean ifExist = huaweiObjectController.ifObjectExist(bucketName, objectKey);
            if (ifExist) {
                return "true";
            }
            else return "false";
        }

    }

    @ResponseBody
    @RequestMapping(value = "/simpleList", method = RequestMethod.POST)
    public List<String> simpleList(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("simpleList");
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


            List<String> listObject = aliObjectController.simpleListObject(bucketName);
            //返回结果
            return listObject;
        }
        else {
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.simpleList(request);
            List<String> result = new ArrayList<>();
            for (ObsObject o : list) {
                result.add(o.toString());
            }
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleListWithNum", method = RequestMethod.POST)
    public List<String> simpleListWithNum(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("simpleListWithNum");
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
            String number = map.get("number");
            if (number == null || "".equals(number) || !BucketUtils.isNumber(number)) {
                response.setStatus(777);
                return null;
            }
            List<String> listObject = aliObjectController.simpleListObject(bucketName,number);
            //返回结果
            return listObject;
        }
        else {
            String number = map.get("number");
            if (number == null || "".equals(number) || !BucketUtils.isNumber(number)) {
                response.setStatus(777);
                return null;
            }
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.simpleList(request, Integer.parseInt(number));
            List<String> result = new ArrayList<>();
            for (ObsObject o : list) {
                result.add(o.toString());
            }
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleListWithPrefix", method = RequestMethod.POST)
    public List<String> simpleListWithPrefix(@RequestBody Map<String, String> map,
                                          @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("simpleListWithPrefix");
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
            String prefix = map.get("prefix");
            if (prefix == null || "".equals(prefix)) {
                response.setStatus(777);
                return null;
            }
            List<String> listObject = aliObjectController.simpleListObject(bucketName, prefix);
            return listObject;
        }
        else {
            String prefix = map.get("prefix");
            if (prefix == null || "".equals(prefix)) {
                response.setStatus(777);
                return null;
            }
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.simpleList(request,prefix);
            List<String> result = new ArrayList<>();
            for (ObsObject o : list) {
                result.add(o.toString());
            }
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/simpleListWithNumPrefix", method = RequestMethod.POST)
    public List<String> simpleListWithNumPrefix(@RequestBody Map<String, String> map,
                                                @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("simpleListWithNumPrefix");
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
            String number = map.get("number");
            if (number == null || "".equals(number) || !BucketUtils.isNumber(number)) {
                response.setStatus(777);
                return null;
            }
            String prefix = map.get("prefix");
            if (prefix == null || "".equals(prefix)) {
                response.setStatus(777);
                return null;
            }
            List<String> listObject = aliObjectController.simpleListObject(bucketName, prefix, Integer.parseInt(number));
            //返回结果
            return listObject;
        } else {
            String number = map.get("number");
            if (number == null || "".equals(number) || !BucketUtils.isNumber(number)) {
                response.setStatus(777);
                return null;
            }
            String prefix = map.get("prefix");
            if (prefix == null || "".equals(prefix)) {
                response.setStatus(777);
                return null;
            }
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.simpleList(request, Integer.parseInt(number), prefix);
            List<String> result = new ArrayList<>();
            for (ObsObject o : list) {
                result.add(o.toString());
            }
            return result;
        }
    }
    @ResponseBody
    @RequestMapping(value = "/pagingList", method = RequestMethod.POST)
    public List<String> pagingList(@RequestBody Map<String, String> map,
                                   @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("pagingList");
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
            List<String> listObject = aliObjectController.pageObjectList(bucketName);
            //返回结果
            return listObject;
        }
        else {
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.pagingList(request);
            List<String> result = new ArrayList<>();
            for (ObsObject o : list) {
                result.add(o.toString());
            }
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/pagingListWithPrefix", method = RequestMethod.POST)
    public List<String> pagingListWithPrefix(@RequestBody Map<String, String> map,
                                             @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("pagingListWithPrefix");
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
            String prefix = map.get("prefix");
            if (prefix == null || "".equals(prefix)) {
                response.setStatus(777);
                return null;
            }
            List<String> listObject = aliObjectController.pageObjectList(bucketName, prefix);
            //返回结果
            return listObject;
        } else {
            String prefix = map.get("prefix");
            if (prefix == null || "".equals(prefix)) {
                response.setStatus(777);
                return null;
            }
            ListObjectsRequest request = huaweiObjectController.newListRequest(bucketName);
            List<ObsObject> list = huaweiObjectController.pagingList(request, prefix);
            List<String> result = new ArrayList<>();
            for (ObsObject o : list) {
                result.add(o.toString());
            }
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteObject", method = RequestMethod.DELETE)
    public String deleteObject(@RequestBody Map<String, String> map,
                       @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("deleteObject");
        if (!verifyIdentity(response, authorization)) {
            return null;
        }
        Integer userId = Integer.valueOf(Objects.requireNonNull(TokenUtils.getUserId(authorization)));
        String bucketName = map.get("bucketName");
        String objectKey = map.get("objectKey");
        String objectPath = map.get("objectKey");
        if (verify(response, userId, bucketName)) {
            return null;
        }
        if (objectKey == null || "".equals(objectKey)) {
            response.setStatus(777);
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
           if(aliObjectController.doesObjectExist(bucketName, objectKey)){
               String s = aliObjectController.deleteObject(bucketName,objectPath);
               return s;
           }else return "false";


        } else {
            String result = huaweiObjectController.deleteObject(bucketName,objectKey);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/copyObject", method = RequestMethod.POST)
    public String copyObject(@RequestBody Map<String, String> map,
                             @RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        System.out.println("copyObject");
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
//            String sourceBucketName = map.get("sourceBucketName");
            String sourceObjectName = map.get("sourceObjectName");
            String destBucketName = map.get("destBucketName");
            String destObjectName = map.get("destObjectName");
            String etag = aliObjectController.simpleCopyObject(bucketName, sourceObjectName, destBucketName, destObjectName);
            return etag;
        } else {
            /*
            如果复制成功则返回新对象的etag
            失败则返回“copy failed”
             */
//            String sourceBucketName = map.get("sourceBucketName");
            String sourceObjectName = map.get("sourceObjectName");
            String destBucketName = map.get("destBucketName");
            String destObjectName = map.get("destObjectName");
            if (sourceObjectName == null || destBucketName == null || destObjectName == null) {
                response.setStatus(777);
                return null;
            }
            if ("".equals(sourceObjectName) || "".equals(destBucketName) || "".equals(destObjectName)) {
                response.setStatus(777);
                return null;
            }
            CopyObjectRequest request = new CopyObjectRequest(bucketName, sourceObjectName, destBucketName, destObjectName);
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
