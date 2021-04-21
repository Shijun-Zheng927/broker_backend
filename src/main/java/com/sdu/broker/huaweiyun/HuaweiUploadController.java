package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class HuaweiUploadController {
    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    private static final String default_bucketLoc  = "cn-north-1";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    /* 流式上传 */
    public void putString(String s,String bucketName,String objname){
        String content = s;
        obsClient.putObject(bucketName, objname, new ByteArrayInputStream(content.getBytes()));
    }

    public int putStream(String s,String bucketName,String objname) {
        InputStream inputStream = null;
        try {
            inputStream = new URL(s).openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        obsClient.putObject(bucketName, objname, inputStream);
        return 1;
    }

    public int putFile(String s,String bucketName,String objname) {
        FileInputStream fis = null;  // 待上传的本地文件路径，需要指定到具体的文件名
        try {
            fis = new FileInputStream(new File(s));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
        obsClient.putObject(bucketName, objname, fis);
        return 1;
    }


    /* 文件上传 */
//    public static void uploadFile(String pathname,String BucketName,String objectKey) throws ObsException
//    {
//        File newfile = new File(pathname);
//        obsClient.putObject(BucketName, objectKey, newfile);
//    }

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
