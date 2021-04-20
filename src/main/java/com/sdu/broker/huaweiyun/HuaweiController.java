package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HuaweiController {

    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    private static final String default_bucketLoc  = "cn-north-1";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    /* 创建桶 */
    public int createBucket(String bucketName,int rwPolicy,int storageClass)
    {
        ObsBucket obsBucket = new ObsBucket();
        //设置桶名字
        obsBucket.setBucketName(bucketName);
        // 设置桶访问权限为公共读，默认是私有读写
        switch (rwPolicy) {
            case 0:{
                //私有读写
                obsBucket.setAcl(AccessControlList.REST_CANNED_PRIVATE);
            }
            case 1:{
                //公共读私有写
                obsBucket.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
            }
            case 2:{
                //桶公共读，桶内对象公共读
                obsBucket.setAcl(AccessControlList.	REST_CANNED_PUBLIC_READ_DELIVERED);
            }
            case 3:{
                //公共读写
                obsBucket.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE);
            }
            case 4:{
                //桶公共读写，桶内对象公共读写
                obsBucket.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED);
            }
        }
        // 设置桶的存储类型为归档存储
        switch (storageClass) {
            case 0:{
                //标准
                obsBucket.setBucketStorageClass(StorageClassEnum.STANDARD);
            }
            case 1:{
                //低频访问
                obsBucket.setBucketStorageClass(StorageClassEnum.WARM);
            }
            case 2:{
                //归档
                obsBucket.setBucketStorageClass(StorageClassEnum.COLD);
            }
        }

        try {
            // 设置桶区域位
            obsBucket.setLocation(default_bucketLoc);
            obsClient.createBucket(obsBucket);
            System.out.println("create bucket:" + bucketName + " success！");
            return 1;
        }
        catch (ObsException e){
            return 0;
        }

    }

    /* 列举桶 */
    public List<ObsBucket> listBucket()
    {
        System.out.println("start listing all bucket");
        ListBucketsRequest request = new ListBucketsRequest();
        request.setQueryLocation(true);
        List<ObsBucket> buckets = obsClient.listBuckets(request);
        for (ObsBucket bucket : buckets) {
            System.out.println("Bucket Name:" + bucket.getBucketName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("Create Date:" + bucket.getCreationDate());
            System.out.println("Location:" + bucket.getLocation());
            System.out.println();
        }
        return buckets;
    }

    /* 删除桶 */
    public int removeBucket(String bucketName)
    {
        boolean exist = existBucket(bucketName);
        if (exist) {
            obsClient.deleteBucket(bucketName);
            System.out.println("delete bucket : " + bucketName + "success");
            try {
                obsClient.close();
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        } else {
            System.out.println("Not exist:" + bucketName);
            return 0;
        }

    }

    /* 判断桶是否存在 */
    public boolean existBucket(String bucketName)
    {
        boolean exist = obsClient.headBucket(bucketName);
        return exist;
    }

    /* 获取桶元数据 */
    public BucketMetadataInfoResult getresult(String bucketName)
    {
        BucketMetadataInfoRequest request = new BucketMetadataInfoRequest(bucketName);
        request.setOrigin("http://www.a.com");
        BucketMetadataInfoResult result = obsClient.getBucketMetadata(request);
        System.out.println("\t:" + result.getDefaultStorageClass());
        System.out.println("\t:" + result.getAllowOrigin());
        System.out.println("\t:" + result.getMaxAge());
        System.out.println("\t:" + result.getAllowHeaders());
        System.out.println("\t:" + result.getAllowMethods());
        System.out.println("\t:" + result.getExposeHeaders());
        return result;
    }

    /* 为桶设置预定义访问策略 */
    public void setBucketAcl(String bucketName,int rwPolicy)
    {
        switch (rwPolicy) {
            case 0: {
                //私有读写
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PRIVATE);
            }
            case 1: {
                //公共读私有写
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PUBLIC_READ);
            }
            case 2: {
                //桶公共读，桶内对象公共读
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PUBLIC_READ_DELIVERED);
            }
            case 3: {
                //公共读写
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PUBLIC_READ_WRITE);
            }
            case 4: {
                //桶公共读写，桶内对象公共读写
                obsClient.setBucketAcl(bucketName, AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED);
            }
        }
    }

    /* 获取桶访问权限 */
    public String getBucketAcl(String bucketName){
        AccessControlList acl = obsClient.getBucketAcl(bucketName);
        String result = acl.toString();
        return result;
    }

    /* 设置桶策略 */
    public void setBucketPolicy(String bucketName,String policy)
    {
        ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        obsClient.setBucketPolicy(bucketName, policy);
    }

    /* 获取桶策略 */
    public String getBucketPolicy(String bucketName)
    {
        try{
            String policy = obsClient.getBucketPolicy(bucketName);
            System.out.println("\t" + policy);
            return policy;
        }catch (ObsException e){
            return "no policy";
        }

    }

    /* 删除桶策略 */
    public void deleteBucketPolicy(String bucketName){
        obsClient.deleteBucketPolicy(bucketName);
    }

    /* 获取桶的区域位置 */
    public String getlocation(String bucketName){
        String location = obsClient.getBucketLocation(bucketName);
        System.out.println("\t:" + location);
        return location;
    }

    /* 获取桶存量信息 */
    public Map<String, String> getBucketStorageInfo(String bucketName){
        BucketStorageInfo storageInfo = obsClient.getBucketStorageInfo(bucketName);
        Map<String, String> result = new HashMap<>();
        result.put("objectNmuber", Long.toString(storageInfo.getObjectNumber()));
        result.put("size", Long.toString(storageInfo.getSize()));
        System.out.println("\t" + storageInfo.getObjectNumber());
        System.out.println("\t" + storageInfo.getSize());
        return result;
    }

    /* 设置桶配额 */
    public void setBucketQuota(String bucketName,long size){
        BucketQuota quota = new BucketQuota(size);
        obsClient.setBucketQuota(bucketName, quota);
        System.out.println("set quota success");
    }

    /* 获取桶配额 */
    public long getBucketQuota(String bucketName){
        BucketQuota quota = obsClient.getBucketQuota(bucketName);
//        System.out.println("\t" + quota.getBucketQuota());

        return quota.getBucketQuota();
    }

    /* 设置桶存储类型 */
    public void setBucketStoragePolicy(String bucketName,int policy){
        BucketStoragePolicyConfiguration storgePolicy = new BucketStoragePolicyConfiguration();
        switch (policy){
            case 0:{
                storgePolicy.setBucketStorageClass(StorageClassEnum.STANDARD);
            }
            case 1:{
                storgePolicy.setBucketStorageClass(StorageClassEnum.WARM);
            }
            case 2:{
                storgePolicy.setBucketStorageClass(StorageClassEnum.COLD);
            }
        }
        obsClient.setBucketStoragePolicy(bucketName, storgePolicy);
    }

    /* 获取桶存储类型 */
    public String getBucketStorageClass(String bucketName){
        BucketStoragePolicyConfiguration storagePolicy = obsClient.getBucketStoragePolicy(bucketName);
//        System.out.println("\t" + storagePolicy.getBucketStorageClass());
        return storagePolicy.getBucketStorageClass().toString();
    }



    //----------------------------------------------------------------------

   /* 流式上传 */
    public void putString(String s,String bucketName,String objname){
        String content = s;
        obsClient.putObject(bucketName, objname, new ByteArrayInputStream(content.getBytes()));
    }

    public void putStream(String s,String bucketName,String objname) throws IOException {
        InputStream inputStream = new URL(s).openStream();
        obsClient.putObject(bucketName, objname, inputStream);
    }

    public void putFile(String s,String bucketName,String objname) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File(s));  // 待上传的本地文件路径，需要指定到具体的文件名
        obsClient.putObject(bucketName, objname, fis);
    }


    /* 文件上传 */
    public static void uploadFile(String pathname,String BucketName,String objectKey) throws ObsException
    {
        File newfile = new File(pathname);
        obsClient.putObject(BucketName, objectKey, newfile);
    }

    /* 获取上传进度 */
    public void status(String pathname,String bucketName,String objectKey){
        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey);
        request.setFile(new File(pathname));
        request.setProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressStatus status) {
                // 获取上传平均速率
                System.out.println("AverageSpeed:" + status.getAverageSpeed());
                // 获取上传进度百分比
                System.out.println("TransferPercentage:" + status.getTransferPercentage());
            }
        });
// 每上传1MB数据反馈上传进度
        request.setProgressInterval(1024 * 1024L);
        obsClient.putObject(request);
    }

    /*创建文件夹*/
    public void createFolder(String pathname,String bucketName){
        final String keySuffixWithSlash = pathname;
        obsClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));

    }

    /* 分段上传 */
    public void InitiateMultipartUpload(String bucketName,String objectKey){
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectKey);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("property", "property-value");
        metadata.setContentType("text/plain");
        request.setMetadata(metadata);
        InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);

        String uploadId = result.getUploadId();
        System.out.println("\t" + uploadId);
    }
    public void uploadPart(String pathname,String bucketName,String objectKey,String uploadId){

        List<PartEtag> partEtags = new ArrayList<PartEtag>();
// 上传第一段
        UploadPartRequest request = new UploadPartRequest(bucketName, objectKey);
// 设置Upload ID
        request.setUploadId(uploadId);
// 设置分段号，范围是1~10000，
        request.setPartNumber(1);
// 设置将要上传的大文件
        request.setFile(new File(pathname));

// 设置分段大小
        request.setPartSize(5 * 1024 * 1024L);
        UploadPartResult result = obsClient.uploadPart(request);
        partEtags.add(new PartEtag(result.getEtag(), result.getPartNumber()));

// 上传第二段
        request = new UploadPartRequest(bucketName, objectKey);
// 设置Upload ID
        request.setUploadId(uploadId);
// 设置分段号
        request.setPartNumber(2);
// 设置将要上传的大文件
        request.setFile(new File(pathname));
// 设置第二段的段偏移量
        request.setOffset(5 * 1024 * 1024L);
// 设置分段大小
        request.setPartSize(5 * 1024 * 1024L);
        result = obsClient.uploadPart(request);
        partEtags.add(new PartEtag(result.getEtag(), result.getPartNumber()));
    }

    public void CompleteMultipartUpload(String bucketName,String objectKey,String uploadId){

        List<PartEtag> partEtags = new ArrayList<PartEtag>();
// 第一段
        PartEtag part1 = new PartEtag();
        part1.setPartNumber(1);
        part1.seteTag("etag1");
        partEtags.add(part1);

// 第二段
        PartEtag part2 = new PartEtag();
        part2.setPartNumber(2);
        part2.setEtag("etag2");
        partEtags.add(part2);

        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(bucketName, objectKey, uploadId, partEtags);

        obsClient.completeMultipartUpload(request);
    }

    public void concurrentMultipartUpload(String pathname,String bucketName,String objectKey){
        // 初始化线程池
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        final File largeFile = new File(pathname);

// 初始化分段上传任务
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectKey);
        InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);

        final String uploadId = result.getUploadId();
        System.out.println("\t"+ uploadId + "\n");

// 每段上传100MB
        long partSize = 100 * 1024 * 1024L;
        long fileSize = largeFile.length();

// 计算需要上传的段数
        long partCount = fileSize % partSize == 0 ? fileSize / partSize : fileSize / partSize + 1;

        final List<PartEtag> partEtags = Collections.synchronizedList(new ArrayList<PartEtag>());

// 执行并发上传段
        for (int i = 0; i < partCount; i++)
        {
            // 分段在文件中的起始位置
            final long offset = i * partSize;
            // 分段大小
            final long currPartSize = (i + 1 == partCount) ? fileSize - offset : partSize;
            // 分段号
            final int partNumber = i + 1;
            executorService.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    UploadPartRequest uploadPartRequest = new UploadPartRequest();
                    uploadPartRequest.setBucketName(bucketName);
                    uploadPartRequest.setObjectKey(objectKey);
                    uploadPartRequest.setUploadId(uploadId);
                    uploadPartRequest.setFile(largeFile);
                    uploadPartRequest.setPartSize(currPartSize);
                    uploadPartRequest.setOffset(offset);
                    uploadPartRequest.setPartNumber(partNumber);

                    UploadPartResult uploadPartResult;
                    try
                    {
                        uploadPartResult = obsClient.uploadPart(uploadPartRequest);
                        System.out.println("Part#" + partNumber + " done\n");
                        partEtags.add(new PartEtag(uploadPartResult.getEtag(), uploadPartResult.getPartNumber()));
                    }
                    catch (ObsException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }

// 等待上传完成
        executorService.shutdown();
        while (!executorService.isTerminated())
        {
            try
            {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
// 合并段
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, objectKey, uploadId, partEtags);
        obsClient.completeMultipartUpload(completeMultipartUploadRequest);
    }

    /* 取消分段上传 */
    public void AbortMultipartUpload(String bucketName,String objectKey,String uploadId){
        AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, objectKey, uploadId);

        obsClient.abortMultipartUpload(request);
    }

    /*列举已经上传的段*/
    public void ListParts(String bucketName,String objectKey,String uploadId){
        ListPartsRequest request = new ListPartsRequest(bucketName, objectKey);
        request.setUploadId(uploadId);
        ListPartsResult result = obsClient.listParts(request);

        for(Multipart part : result.getMultipartList()){
            // 分段号，上传时候指定
            System.out.println("\t"+part.getPartNumber());
            // 段数据大小
            System.out.println("\t"+part.getSize());
            // 分段的ETag值
            System.out.println("\t"+part.getEtag());
            // 段的最后上传时间
            System.out.println("\t"+part.getLastModified());
        }
    }

    /* 追加上传 */
    public void AppendObject(String bucketName,String objectKey,String add1){
        // 第一次追加上传
        AppendObjectRequest request = new AppendObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setPosition(0);
        request.setInput(new ByteArrayInputStream(add1.getBytes()));
        AppendObjectResult result = obsClient.appendObject(request);

// 第二次追加上传
//        request.setPosition(result.getNextPosition());
//        request.setInput(new ByteArrayInputStream("Hello OBS Again".getBytes()));
//        result = obsClient.appendObject(request);

        System.out.println("NextPosition:" + result.getNextPosition());
        System.out.println("Etag:" + result.getEtag());
// 通过获取对象属性接口获取下次追加上传的位置
        ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName, objectKey);
        System.out.println("NextPosition from metadata:" + metadata.getNextPosition());
    }

    /*断点续传*/
    public void CheckpointUpload(String pathname,String bucketName,String objectKey){
        UploadFileRequest request = new UploadFileRequest(bucketName, objectKey);
// 设置待上传的本地文件，localfile为待上传的本地文件路径，需要指定到具体的文件名
        request.setUploadFile(pathname);
// 设置分段上传时的最大并发数
        request.setTaskNum(5);
// 设置分段大小为10MB
        request.setPartSize(10 * 1024 * 1024);
// 开启断点续传模式
        request.setEnableCheckpoint(true);
        try{
            // 进行断点续传上传
            CompleteMultipartUploadResult result = obsClient.uploadFile(request);
        }catch (ObsException e) {
            // 发生异常时可再次调用断点续传上传接口进行重新上传
        }
    }

    /*基于表单上传*/
    public void PostSignature(){
        PostSignatureRequest request = new PostSignatureRequest();
// 设置表单参数
        Map<String, Object> formParams = new HashMap<String, Object>();
// 设置对象访问权限为公共读
        formParams.put("x-obs-acl", "public-read");
// 设置对象MIME类型
        formParams.put("content-type", "text/plain");

        request.setFormParams(formParams);
// 设置表单上传请求有效期，单位：秒
        request.setExpires(3600);
        PostSignatureResponse response = obsClient.createPostSignature(request);

// 获取表单上传请求参数
        System.out.println("\t" + response.getPolicy());
        System.out.println("\t" + response.getSignature());
    }


    /* 列举对象（文件）的信息 */
    public static void listFile(String BucketName) throws ObsException
    {
        System.out.println("start listing objects in bucket");
        ObjectListing objList = obsClient.listObjects(BucketName);
        for (ObsObject obj : objList.getObjects())
        {
            System.out.println("--:"+obj.getObjectKey()+" (size=" + obj.getMetadata().getContentLength()+")");
        }
    }


    /* 删除对象 */
    public static void deleteObject(String BucketName,String objectKey)
    {
        System.out.println("now start deleting");
        boolean exist = obsClient.doesObjectExist(BucketName, objectKey);
        DeleteObjectResult result = null;
        if (exist) {
            result = obsClient.deleteObject(BucketName, objectKey);
            if (result.isDeleteMarker() == false){
                System.out.println("delete "+objectKey+" success!");
            }
        }
        else{
            System.out.println("object : "+ objectKey + "not found");
        }
    }


    /* 下载文件 */
    public static void getFile(String BucketName,String objectKey,String downloadPath) throws IOException {
        System.out.println("now downloading file");
        ObsObject object = null;
        boolean exist = obsClient.doesObjectExist(BucketName, objectKey);
        if (exist) {
            object = obsClient.getObject(BucketName, objectKey);
        }
        if (object != null) {
            InputStream is = object.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(downloadPath));
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            System.out.println("download success!");
            is.close();
            fos.close();
            return;
        }
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
