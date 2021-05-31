package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.Permission;

public class HuaweiBuckectLoggingController {
    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    /* 开启桶日志 */
    public BucketLoggingConfiguration setBuckectLogging(String agency,String targetBucketName,String targetPrefix,String bucketName){
        BucketLoggingConfiguration config = new BucketLoggingConfiguration();
        config.setAgency(agency);
        config.setTargetBucketName(targetBucketName);
        config.setLogfilePrefix(targetPrefix);

        obsClient.setBucketLogging(bucketName, config);
        return config;
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
}
