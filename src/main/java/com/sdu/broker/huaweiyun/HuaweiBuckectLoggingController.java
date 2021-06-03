package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HuaweiBuckectLoggingController {
    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    /* 开启桶日志 */
    public String setBuckectLogging(String targetBucketName,String targetPrefix,String bucketName){
        BucketLoggingConfiguration config = new BucketLoggingConfiguration();
//        config.setAgency(agency);
        config.setTargetBucketName(targetBucketName);
        config.setLogfilePrefix(targetPrefix);

        HuaweiController hwc = new HuaweiController();
        hwc.setBucketAclForLog(targetBucketName);
        hwc.closeObsClient();

        // 为所有用户设置对日志对象的读权限
        GrantAndPermission grant1 = new GrantAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_FULL_CONTROL);
        config.setTargetGrants(new GrantAndPermission[]{grant1});
        try{
            obsClient.setBucketLogging(bucketName, config);
        }catch (ObsException e){
            return "ObsException";
        }
        return "success";
    }

    /* 为所有用户设置对日志对象权限 */
    public String setGrantAndPermission(BucketLoggingConfiguration config,String bucketName,int permission){
        try{
            GrantAndPermission grant1 = new GrantAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
            switch (permission){
                case 1:{
                    grant1 = new GrantAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ_ACP);
                    break;
                }
                case 2:{
                    grant1 = new GrantAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_WRITE);
                    break;
                }
                case 3:{
                    grant1 = new GrantAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_WRITE_ACP);
                    break;
                }
                case 4:{
                    grant1 = new GrantAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_FULL_CONTROL);
                    break;
                }
            }
            config.setTargetGrants(new GrantAndPermission[]{grant1});
            obsClient.setBucketLogging(bucketName, config);
        }catch (ObsException e){
            return "obsException";
        }
        return "success";
    }

    /* 查看桶日志配置 */
    public Map<String,String> getBucketLogging(String bucketName){
        BucketLoggingConfiguration config = obsClient.getBucketLogging(bucketName);
        Map<String,String> map = new HashMap<>();
        map.put("targetBucketName",config.getTargetBucketName());
        map.put("targetPrefix",config.getLogfilePrefix());
        System.out.println("\t" + config.getTargetBucketName());
        System.out.println("\t" + config.getLogfilePrefix());
        return map;
    }

    /* 关闭桶日志 */
    public String shutdownBucketLogging(String bucketName){
        // 对桶设置空的日志配置
        try{
            Map<String,String> map = getBucketLogging(bucketName);
            String targetBucketName = map.get("targetBucketName");
            String targetPrefix = map.get("targetPrefix");
            System.out.println(targetBucketName);
            System.out.println(targetPrefix);
            HuaweiObjectController hoc = new HuaweiObjectController();
            ListObjectsRequest request = hoc.newListRequest(targetBucketName);
            List<ObsObject> list = hoc.simpleList(request,targetPrefix);
            for (ObsObject o : list){
                hoc.deleteObject(targetBucketName,o.getObjectKey());
            }
            obsClient.setBucketLogging(bucketName, new BucketLoggingConfiguration());
        }catch (ObsException e ){
            return "ObsException";
        }
        return "success";
    }

    /* 关闭客户端 */
    public static void closeObsClient()
    {
        try
        {
            obsClient.close();
            System.out.println("close obs client success");
        }
        catch (IOException e)
        {
            System.out.println("close obs client error.");
        }

    }
}
