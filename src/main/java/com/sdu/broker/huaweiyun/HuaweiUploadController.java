package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
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
    private static final String default_bucketLoc  = "cn-north-4";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    //生成url
    public String getUrl(String bucketName, String objectKey) {
        TemporarySignatureRequest request = new TemporarySignatureRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setRequestDate(new Date());
        // 90天失效
        request.setExpires(60 * 60 * 24 * 90);
//        ObsClient obsClient = obsBiz.getObsClient();
        // 通过临时授权,直接访问链接下载
        TemporarySignatureResponse signature = obsClient.createTemporarySignature(request);
        String url = signature.getSignedUrl();
        System.out.println("url:" + url);
        return url;
    }

    /* 流式上传 */
    public String putString(String s,String bucketName,String objname){
        String content = s;
        obsClient.putObject(bucketName, objname, new ByteArrayInputStream(content.getBytes()));

        return getUrl(bucketName, objname);
    }

    public String putStream(String s,String bucketName,String objname) {
        InputStream inputStream = null;
        try {
            inputStream = new URL(s).openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
        obsClient.putObject(bucketName, objname, inputStream);

        return getUrl(bucketName, objname);
    }

    public String putFile(String s,String bucketName,String objname) {
        FileInputStream fis = null;  // 待上传的本地文件路径，需要指定到具体的文件名
        try {
            fis = new FileInputStream(new File(s));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "fail";
        }
        obsClient.putObject(bucketName, objname, fis);
        return getUrl(bucketName, objname);
    }

    /* 文件上传 */
    public String uploadFile(String pathname,String BucketName,String objectKey)
    {
        File newfile = new File(pathname);
        obsClient.putObject(BucketName, objectKey, newfile);
        return getUrl(BucketName, objectKey);
    }

    /* 获取上传进度 */
    public Map<String,String> status(String pathname,String bucketName,String objectKey){
        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey);
        request.setFile(new File(pathname));
        Map<String,String> map = new HashMap<>();
        request.setProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressStatus status) {
                // 获取上传平均速率
                System.out.println("AverageSpeed:" + status.getAverageSpeed());
                // 获取上传进度百分比
                System.out.println("TransferPercentage:" + status.getTransferPercentage());
                map.put(String.format("%.3f",status.getAverageSpeed()),String.format("%.3f",status.getTransferPercentage()));
            }
        });
// 每上传1MB数据反馈上传进度
        request.setProgressInterval(1024 * 1024L);
        obsClient.putObject(request);
        return map;
    }

    /*创建文件夹*/
    public String createFolder(String pathname,String bucketName){
        final String keySuffixWithSlash = pathname;
        obsClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
        return "success";
    }

    /* 初始化分段上传 */
    public String InitiateMultipartUpload(String bucketName, String objectKey, String contentType){
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectKey);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("property", "property-value");
        metadata.setContentType(contentType);
        request.setMetadata(metadata);
        InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);

        String uploadId = result.getUploadId();
        System.out.println("\t"+ "uploadid:"+ uploadId);
        return uploadId;
    }

    /* 上传第一段 */
    public String uploadPartFirst(String pathname,String bucketName,String objectKey,String uploadId){

//        List<PartEtag> partEtags = new ArrayList<PartEtag>();
        PartEtag partEtag = new PartEtag();

        // 上传第一段
        UploadPartRequest request = new UploadPartRequest(bucketName,objectKey );
        // 设置Upload ID
        request.setUploadId(uploadId);
        // 设置分段号，范围是1~10000，
        request.setPartNumber(1);
        // 设置将要上传的大文件
        request.setFile(new File(pathname));

        // 设置分段大小
        request.setPartSize(10 * 1024 * 1024L);
        UploadPartResult result = obsClient.uploadPart(request);
        partEtag = new PartEtag(result.getEtag(), result.getPartNumber());

        return partEtag.toString();
    }

    /*上传后续段*/
    public String uploadParts(int partNum,String pathname,String bucketName,String objectKey,String uploadId){

        UploadPartRequest request = new UploadPartRequest(bucketName, objectKey);
        PartEtag partEtag = new PartEtag();

        // 设置Upload ID
        request.setUploadId(uploadId);
        // 设置分段号
        request.setPartNumber(partNum);
        // 设置将要上传的大文件
        File file = new File(pathname);
        request.setFile(file);
        // 设置第二段的段偏移量
        request.setOffset((partNum-1)* 10 * 1024 * 1024L);
        // 设置分段大小
        long size = file.length();
        long restsize = size-(partNum-1)*(10 * 1024 * 1024L);
        if (restsize < 10 * 1024 * 1024L){
            request.setPartSize(restsize);
        } else{
            request.setPartSize(10 * 1024 * 1024L);
        }

        UploadPartResult result = obsClient.uploadPart(request);
        partEtag = new PartEtag(result.getEtag(), result.getPartNumber());
        return partEtag.toString();
    }

    /* 合并分段 */
    public String CompleteMultipartUpload(List<PartEtag> partEtags,String bucketName,String objectKey,String uploadId){
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(bucketName,objectKey,uploadId,partEtags);
        obsClient.completeMultipartUpload(request);
        return "success";
    }

    /* 并发式分段上传 */
    public String concurrentMultipartUpload(String pathname,String bucketName,String objectKey){
        // 初始化线程池
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        final File largeFile = new File(pathname);

        // 初始化分段上传任务
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectKey);
        InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);

        final String uploadId = result.getUploadId();
        System.out.println("\t"+ uploadId + "\n");

        // 每段上传10MB
        long partSize = 10 * 1024 * 1024L;
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

        return uploadId;
    }

    /* 取消分段上传 */
    public String AbortMultipartUpload(String bucketName,String objectKey,String uploadId){
        AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, objectKey, uploadId);

        obsClient.abortMultipartUpload(request);

        return "success";
    }

    /* 简单列举已经上传的段 */
    public List<Map<String,String>>  simpleListPart(String bucketName,String objectKey,String uploadId){
        ListPartsRequest request = new ListPartsRequest(bucketName, objectKey);
        request.setUploadId(uploadId);
        ListPartsResult result = obsClient.listParts(request);

        List<Map<String, String>> list = new ArrayList<>();

        for(Multipart part : result.getMultipartList()){
            // 分段号，上传时候指定
            System.out.println("\t"+part.getPartNumber());
            // 段数据大小
            System.out.println("\t"+part.getSize());
            // 分段的ETag值
            System.out.println("\t"+part.getEtag());
            // 段的最后上传时间
            System.out.println("\t"+part.getLastModified());

            Map<String, String> map = new HashMap<>();
            map.put("partName", part.getPartNumber().toString());
            map.put("size", part.getSize().toString());
            map.put("Etag", part.getEtag());
            map.put("lastModified", part.getLastModified().toString());
            list.add(map);
        }
        return list;
    }

    /* 列举所有段 */// 当分段数大于1000时使用
    public List<Map<String,String>> listPartsAll(String bucketName,String objectKey,String uploadId){
        // 列举所有已上传的段
        ListPartsRequest request = new ListPartsRequest(bucketName, objectKey);
        request.setUploadId(uploadId);
        ListPartsResult result;

        List<Map<String, String>> list = new ArrayList<>();
        do{
            result = obsClient.listParts(request);
            for(Multipart part : result.getMultipartList()){
                // 分段号，上传时候指定
                System.out.println("\t"+part.getPartNumber());
                // 段数据大小
                System.out.println("\t"+part.getSize());
                // 分段的ETag值
                System.out.println("\t"+part.getEtag());
                // 段的最后上传时间
                System.out.println("\t"+part.getLastModified());

                Map<String, String> map = new HashMap<>();
                map.put("partName", part.getPartNumber().toString());
                map.put("size", part.getSize().toString());
                map.put("Etag", part.getEtag());
                map.put("lastModified", part.getLastModified().toString());
                list.add(map);
            }
            request.setPartNumberMarker(Integer.parseInt(result.getNextPartNumberMarker()));
        }while(result.isTruncated());

        return list;
    }

    /* 简单列举分段上传任务 */
    public List<Map<String,String>>  simpleListMultipartUploads(String bucketName){
        ListMultipartUploadsRequest request = new ListMultipartUploadsRequest(bucketName);

        MultipartUploadListing result = obsClient.listMultipartUploads(request);

        List<Map<String, String>> list = new ArrayList<>();
        for(MultipartUpload upload : result.getMultipartTaskList()){
            System.out.println("\t" + upload.getUploadId());
            System.out.println("\t" + upload.getObjectKey());
            System.out.println("\t" + upload.getInitiatedDate());

            Map<String, String> map = new HashMap<>();
            map.put("UploadId", upload.getUploadId());
            map.put("ObjectKey", upload.getObjectKey().toString());

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(upload.getInitiatedDate());
            map.put("InitiatedDate", dateString);

            list.add(map);
        }
        return list;
    }

    /* 分页列举分段上传任务 */
    public List<Map<String,String>> listMultipartUploadsByPapper(String bucketName){
        ListMultipartUploadsRequest request = new ListMultipartUploadsRequest(bucketName);
        MultipartUploadListing result;

        List<Map<String, String>> list = new ArrayList<>();
        do{
            result = obsClient.listMultipartUploads(request);
            for(MultipartUpload upload : result.getMultipartTaskList()){
                System.out.println("\t" + upload.getUploadId());
                System.out.println("\t" + upload.getObjectKey());
                System.out.println("\t" + upload.getInitiatedDate());

                Map<String, String> map = new HashMap<>();
                map.put("UploadId", upload.getUploadId());
                map.put("ObjectKey", upload.getObjectKey().toString());

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = formatter.format(upload.getInitiatedDate());
                map.put("InitiatedDate", dateString);

                list.add(map);
            }
            request.setKeyMarker(result.getNextKeyMarker());
            request.setUploadIdMarker(result.getNextUploadIdMarker());
        }while(result.isTruncated());
        return list;
    }

    /* 第一次追加上传 */
    public String appendObjectStreamFirst(String bucketName,String objectKey,String add1){
        AppendObjectRequest request = new AppendObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setPosition(0);
        request.setInput(new ByteArrayInputStream(add1.getBytes()));
        AppendObjectResult result = obsClient.appendObject(request);

        System.out.println("NextPosition:" + result.getNextPosition());
        System.out.println("Etag:" + result.getEtag());

        Map<String, String> map = new HashMap<>();
        map.put("NextPosition" , String.valueOf(result.getNextPosition()));
        map.put("Etag" , result.getEtag());
        String str = "NextPosition:" + result.getNextPosition() + "Etag" + result.getEtag();
        return str;
    }

    /* 非第一次追加上传 */
    public String appendObjectStream(int position,String bucketName,String objectKey,String add1){
        AppendObjectRequest request = new AppendObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setPosition(position);
        request.setInput(new ByteArrayInputStream(add1.getBytes()));
        AppendObjectResult result = obsClient.appendObject(request);

        System.out.println("NextPosition:" + result.getNextPosition());
        System.out.println("Etag:" + result.getEtag());

        Map<String, String> map = new HashMap<>();
        map.put("NextPosition" , String.valueOf(result.getNextPosition()));
        map.put("Etag" , result.getEtag());
        String str = "NextPosition:" + result.getNextPosition() + "Etag" + result.getEtag();
        return str;
    }

    /*断点续传*/
    public String CheckpointUpload(String pathname,String bucketName,String objectKey){
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
        return "success";
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
