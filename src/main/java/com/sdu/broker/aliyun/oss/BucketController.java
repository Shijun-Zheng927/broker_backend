package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
import org.checkerframework.checker.units.qual.K;
import org.springframework.stereotype.Component;

import java.util.*;
//import org.springframework.boot.context.properties.ConfigurationProperties;

//@ConfigurationProperties(prefix = 'ali')
@Component
public class BucketController {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";

    //创建一个Bucket
    public int  createBucket(String bucketName, int storageClass, int dataRedundancyType, int cannedACL){
//        System.out.println(bucketName + " " + storageClass + " " + dataRedundancyType + " " + cannedACL);
        //1,参数列表：bucketName:桶名称   storageClass:存储类型
        //          dataRedundancyType:数据容灾类型
        //          cannedACL:数据读写权限
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try{
            if(ossClient.doesBucketExist(bucketName)){
                System.out.println("您已创建Bucket:" + bucketName + "。");
                return 0;
            }else {
                System.out.println("您的Bucket不存在，创建Bucket:" + bucketName + "。");

                CreateBucketRequest bucketRequest = new CreateBucketRequest(bucketName);

                switch (storageClass){
                    case 1:
                        bucketRequest.setStorageClass(StorageClass.Standard);
                        break;
                    case 2:
                        bucketRequest.setStorageClass(StorageClass.IA);
                        break;
                    case 3:
                        bucketRequest.setStorageClass(StorageClass.Archive);
                    case 4:
                        bucketRequest.setStorageClass(StorageClass.ColdArchive);
                }

                switch (dataRedundancyType){
                    case 0:
                        bucketRequest.setDataRedundancyType(DataRedundancyType.LRS);
                        break;
                    case 1:
                        bucketRequest.setDataRedundancyType(DataRedundancyType.ZRS);
                        break;
                }

                switch (cannedACL){
                    case 1:
                        bucketRequest.setCannedACL(CannedAccessControlList.Private);
                        break;
                    case 2:
                        bucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                    case 3:
                        bucketRequest.setCannedACL(CannedAccessControlList.PublicReadWrite);
                }
                ossClient.createBucket(bucketRequest);
            }

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            System.out.println("Bucket" + bucketName + "的信息如下： ");
            System.out.println("\t数据中心：" + info.getBucket().getLocation());
            System.out.println("\t创建时间: " + info.getBucket().getCreationDate());
            System.out.println("\t用户标志：" + info.getBucket().getOwner());
        } catch (OSSException oe){
            oe.printStackTrace();
            return 0;
        }  catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally{
            ossClient.shutdown();
        }

        return 1;

    }

    //列举所有Bucket
    public List<Bucket> listAllBuckets(){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        List<Bucket> buckets = ossClient.listBuckets();
        for (Bucket bucket : buckets){
            System.out.println(" - " + bucket.getName());
        }
        ossClient.shutdown();
        return buckets;
    }


    //列举有参数的Bucket
    public List<Bucket> listRequestBuckets(String Prefix, String Marker, int maxKeys) {
        //调用该方法需要三个参数中至少有一个不为空
        //Prefix代表列举Bucket的前缀(如果没有，前端传空字符串)
        //Marker代表列举的起始位置(如果没有，前端传空字符串)
        //maxKeys表示列举空间的指定个数，默认值为100,如果传过来0转换成100
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
        try {
            if (!Prefix.isEmpty()) {
                listBucketsRequest.setPrefix(Prefix);
            }
            if (!Marker.isEmpty()) {
                listBucketsRequest.setMarker(Marker);
            }
            if (maxKeys == 0) {
                listBucketsRequest.setMaxKeys(100);
            } else if (maxKeys != 0) {
                listBucketsRequest.setMaxKeys(maxKeys);
            }

        } catch (OSSException oe) {
            return null;
        } catch (Exception e){
            return null;
        }
        BucketList bucketList = ossClient.listBuckets(listBucketsRequest);
        for(Bucket bucket : bucketList.getBucketList()){
            System.out.println(" - " + bucket.getName());
        }
        ossClient.shutdown();
        return bucketList.getBucketList();
    }

    //判断bucket是否存在，输入参数：bucketName
    public static boolean doesBucketExist(String bucketName){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        boolean exists = ossClient.doesBucketExist(bucketName);
        System.out.println(exists);
        ossClient.shutdown();
        return exists;
    }

    //获取bucket存储空间地域
    public static String getBucketLocation(String bucketName){
        //输入参数：bucektName
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        String location = ossClient.getBucketLocation(bucketName);
        System.out.println(location);
        return location;
    }

    //获取存储空间的信息
    public Map<String,String> getBucketInfo(String bucketName){
        //输入参数：bucketName
        //返回值：包含地域、创建日期、拥有者信息、权限信息、容灾类型的一个map集合
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        BucketInfo info = ossClient.getBucketInfo(bucketName);
        String location = info.getBucket().getLocation();
        String creationDate = info.getBucket().getCreationDate().toString();
        String owner = info.getBucket().getOwner().toString();
        String grants = info.getGrants().toString();
        String dataRedundancyType = info.getDataRedundancyType().toString();


        Map<String, String> map = new HashMap<>();
        map.put("Location", location);
        map.put("CreationDate", creationDate);
        map.put("Owner", owner);
        map.put("Grants", grants);
        map.put("DataRedundancyType", dataRedundancyType);

        ossClient.shutdown();

        return map;
    }

    //获取存储空间访问权限
    public static String getBucketAcl(String bucketName){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        AccessControlList bucketAcl = ossClient.getBucketAcl(bucketName);
        System.out.println(bucketAcl.toString());
        ossClient.shutdown();
        return bucketName.toString();
    }

    //设置存储空间访问权限
    public static String setBucketAcl(String bucketName, int acl){
        //输入的acl只能是1 || 2 || 3
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        try{
            switch (acl){
                case 1:
                    ossClient.setBucketAcl(bucketName,CannedAccessControlList.Private);
                    break;
                case 2:
                    ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
                    break;
                case 3:
                    ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
                    break;
            }
        } catch (OSSException e) {
            e.printStackTrace();
            return "false";
        } catch (ClientException e) {
            e.printStackTrace();
            return "false";
        }
        ossClient.shutdown();
        return "设置存储空间访问权限成功";
    }

    //删除存储空间
    public static String deleteBucket(String bucketName){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        try {
            ossClient.deleteBucket(bucketName);
        } catch (OSSException e) {
            e.printStackTrace();
            return "false";
        } catch (ClientException e) {
            e.printStackTrace();
            return "false";
        }
        ossClient.shutdown();
        return "删除存储空间成功";
    }

    //设置存储标签
    //同一个Bucket
    public static String setBucketTagging(String bucketName, String tagKey, String tagValue){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        try {
//            SetBucketTaggingRequest request = new SetBucketTaggingRequest();
//            request.setTag(tagKey,tagValue);
//            ossClient.setBucketTagging(request);
        } catch (OSSException e) {
            e.printStackTrace();
            return "false";
        } catch (ClientException e) {
            e.printStackTrace();
            return "false";
        }

        ossClient.shutdown();
        return "设置标签成功";
    }

    //获取存储标签
    public static Map<String,String> getBucketTagging(String bucketName){
        //输入参数：bucketName
        //返回结果：一个包含所有tag的Map集合
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        TagSet tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
        Map<String,String> tags = tagSet.getAllTags();
        ossClient.shutdown();
        return tags;
    }

    //列举带指定标签的bucket
    public static List<Bucket> listBucketByTag(String tagKey, String tagValue){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
        listBucketsRequest.setTag(tagKey,tagValue);
        BucketList bucketList = ossClient.listBuckets(listBucketsRequest);
        for (Bucket bucket : bucketList.getBucketList()){
            System.out.println("list result bucket: " + bucket.getName());
        }
        ossClient.shutdown();
        return bucketList.getBucketList();
    }

    //删除bucket标签
    public static String deleteBucketTagging(String bucketName){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteBucketTagging(new GenericRequest(bucketName));
        ossClient.shutdown();
        return "成功删除标签";
    }

    //添加存储空间清单配置
    public static String setBucketInventoryConfiguration(String bucketName,String inventoryId, int inventoryFrequency,
                                                         int InventoryIncludedObjectVersions,int isEnabled,
                                                         String objPrefix, String destinationPrefix,
                                                         int bucketFormat, String accountId,
                                                         String roleArn, String destBucketName
                                                         ){
        //输入参数：bucketName 当前bucket名称
        //        inventoryId 存储空间清单Id
        //        inventoryFrequency 清单生成频率 1：Weekly 2:Daily
        //        InventoryIncludedObjectVersions 清单包含对象版本：1当前版本 2历史版本
        //        isEnabled 是否启用存储清单： 0：关闭 1：启动
        //        objPrefix   搜索包含该前缀的对象
        //        destinationPrefix   清单存储路径前缀
        //        bucketFormat 清单格式 1：CSV(只有这一种格式）
        //        accountId 目的地bucket的用户accountId
        //        roleArn 目的地bucket的roleArn
        //        destBucketName 目的地bucket的名称

        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        //创建清单配置
        InventoryConfiguration inventoryConfiguration = new InventoryConfiguration();
        //设置清单配置Id
        inventoryConfiguration.setInventoryId(inventoryId);
        //设置清单包含object的属性
        List<String> fields = new ArrayList<String>();
        fields.add(InventoryOptionalFields.Size);
        fields.add(InventoryOptionalFields.LastModifiedDate);
        fields.add(InventoryOptionalFields.IsMultipartUploaded);
        fields.add(InventoryOptionalFields.StorageClass);
        fields.add(InventoryOptionalFields.ETag);
        fields.add(InventoryOptionalFields.EncryptionStatus);
        inventoryConfiguration.setOptionalFields(fields);

        //设置清单生成频率
        switch (inventoryFrequency){
            case 1:
                inventoryConfiguration.setSchedule(new InventorySchedule().withFrequency(InventoryFrequency.Weekly));
                break;
            case 2:
                inventoryConfiguration.setSchedule(new InventorySchedule().withFrequency(InventoryFrequency.Daily));
                break;
        }

        //设置清单配置是否启用
        switch (isEnabled){
            case 0:
                inventoryConfiguration.setEnabled(false);
                break;
            case 1:
                inventoryConfiguration.setEnabled(true);
                break;
        }

        //设置清单筛选规则指定筛选object前缀
        InventoryFilter inventoryFilter = new InventoryFilter();
        inventoryFilter.withPrefix(objPrefix);
        inventoryConfiguration.setInventoryFilter(inventoryFilter);

        //创建清单的bucket目的地配置
        InventoryOSSBucketDestination ossInvDest = new InventoryOSSBucketDestination();
        // 设置产生清单结果的存储路径前缀。
        ossInvDest.setPrefix(destinationPrefix);
        // 设置清单格式。
        if(bucketFormat==1)
            ossInvDest.setFormat(InventoryFormat.CSV);
        // 目的地bucket的用户accountId。
        ossInvDest.setAccountId(accountId);
        // 目的地bucket的roleArn。
        ossInvDest.setRoleArn(roleArn);
        // 目的地bucket的名称。
        ossInvDest.setBucket(destBucketName);
        // 如果需要使用KMS加密清单，请参考如下设置。
        // InventoryEncryption inventoryEncryption = new InventoryEncryption();
        // InventoryServerSideEncryptionKMS serverSideKmsEncryption = new InventoryServerSideEncryptionKMS().withKeyId("test-kms-id");
        // inventoryEncryption.setServerSideKmsEncryption(serverSideKmsEncryption);
        // ossInvDest.setEncryption(inventoryEncryption);

        // 如果需要使用OSS服务端加密清单，请参考如下设置。
        // InventoryEncryption inventoryEncryption = new InventoryEncryption();
        // inventoryEncryption.setServerSideOssEncryption(new InventoryServerSideEncryptionOSS());
        // ossInvDest.setEncryption(inventoryEncryption);

        // 设置清单的目的地。
        InventoryDestination destination = new InventoryDestination();
        destination.setOssBucketDestination(ossInvDest);
        inventoryConfiguration.setDestination(destination);

        // 上传清单配置。
        ossClient.setBucketInventoryConfiguration(bucketName, inventoryConfiguration);

        // 关闭ossClient。
        ossClient.shutdown();
        return "创建清单配置成功";
    }





    public static void main(String[] args) {
//        listAllBuckets();
//        listRequestBuckets("xmsx","",2);
//        Map<String, String> result = getBucketInfo("xmsx-001");
//        System.out.println(result);
    }

}

