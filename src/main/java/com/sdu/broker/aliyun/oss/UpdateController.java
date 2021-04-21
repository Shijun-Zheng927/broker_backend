package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.springframework.stereotype.Component;


import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

@Component
public class UpdateController {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";
    //    Date expiration = new Date(System.currentTimeMillis() + 24 * 1000 * 90);
    //流式上传：上传字符串、上传数组、上传

    //上传字符串
    public static String putString(String content, String bucketName, String objectPath){
        //objectPath:字符串的保存路径，例如： test.txt(不要带存储空间名称）
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //创建PutObjectRequest对象
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectPath, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            /*
             如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
             ObjectMetadata metadata = new ObjectMetadata();
             metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
             metadata.setObjectAcl(CannedAccessControlList.Private);
             putObjectRequest.setMetadata(metadata);
            */

            //上传字符串
            ossClient.putObject(putObjectRequest);

            System.out.println(content);
        } catch (OSSException ossException) {
            System.out.println("出错了智障");
            return "false";
        }
        Date expiration = new Date(System.currentTimeMillis() + 24 * 1000 * 90);
        String  url = ossClient.generatePresignedUrl(bucketName, objectPath, expiration).toString();
        ossClient.shutdown();
        //返回上传地址
        return url;
    }
    
    //上传Byte数组
    public static String putBytes(byte[] bytes,String bucketName, String objectPath){
        //输入参数：bytes 需要上传的byte数组
        //        objectPath 存储路径（不包含存储空间名称）
        // 创建ossClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        try {

            //填写输入参数
            ossClient.putObject(bucketName, objectPath, new ByteArrayInputStream(bytes));

        } catch (OSSException ossException) {
            ossException.printStackTrace();
            return "false";
        }

        Date expiration = new Date(System.currentTimeMillis() + 24 * 1000 * 90);
        String  url = ossClient.generatePresignedUrl(bucketName, objectPath, expiration).toString();
            //关闭ossClient
        ossClient.shutdown();

        return url;
    }

    //上传网络流
    public static String putStream(String inputUrl, String bucketName, String objectPath){
        //输入参数：inputUrl  网络流对应的URL地址
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        try {
            //填写网络流地址
            URL url = new URL(inputUrl);
            InputStream inputStream = url.openStream();
            //填写输入参数
            ossClient.putObject(bucketName, objectPath, inputStream);
            //关闭ossClient
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }


        Date expiration = new Date(System.currentTimeMillis() + 24 * 1000 * 90);
        String  url = ossClient.generatePresignedUrl(bucketName, objectPath, expiration).toString();
        ossClient.shutdown();
        return url;
    }

    //上传文件流
    public static String putFileStream(String fileStreamPath, String bucketName, String objectPath){
        //输入参数: fileStreamPath 所上传文件流的本地路径 格式如下：F:\\数值计算\\实验一截图1.png
        //注意是双斜线
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        try {
            InputStream inputStream = new FileInputStream(fileStreamPath);
            ossClient.putObject(bucketName,objectPath,inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "false";
        }


        Date expiration = new Date(System.currentTimeMillis() + 24 * 1000 * 90);
        String  url = ossClient.generatePresignedUrl(bucketName, objectPath, expiration).toString();
        ossClient.shutdown();
        return url;
    }

    //文件上传
    public static String putFile(String filePath, String bucketName, String objectPath){
        //输入参数:filePath:所上传文件在本地的绝对路径
        //格式也是双右斜线\\
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,objectPath,new File(filePath));

        /*
         如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
         ObjectMetadata metadata = new ObjectMetadata();
         metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
         metadata.setObjectAcl(CannedAccessControlList.Private);
         putObjectRequest.setMetadata(metadata);

        */

        //上传文件
        PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
//        String url = putObjectResult.getETag();
        Date expiration = new Date(System.currentTimeMillis() + 24 * 1000 * 90);
        String url = ossClient.generatePresignedUrl(bucketName,objectPath, expiration).toString();
        System.out.println(url);

        //关闭ossClient
        ossClient.shutdown();

        return url;


    }

    /*
表单上传
    public static String formUpload(String bucketName, Map<String, String> formFields, String localFile){
        String res = "";
        HttpURLConnection conn = null;
        String boundary = "9431149156168";
        OSS ossClient  = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        try {
            System.out.println(endpoint);
            String urlStr = endpoint.replace("https://", "http://" + bucketName+ ".");
            System.out.println(urlStr);
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            // 设置MD5值。MD5值由整个body计算得出。
            conn.setRequestProperty("Content-MD5", "<yourContentMD5>");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 遍历读取表单Map中的数据，将数据写入到输出流中。
            if (formFields != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Map.Entry<String, String>> iter = formFields.entrySet().iterator();
                int i = 0;
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    if (i == 0) {
                        strBuf.append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    } else {
                        strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    }
                    i++;
                }
                out.write(strBuf.toString().getBytes());
            }
            // 读取文件信息，将要上传的文件写入到输出流中。
            File file = new File(localFile);
            String filename = file.getName();
            String contentType = new MimetypesFileTypeMap().getContentType(file);
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file\"; "
                    + "filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type: " + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();
            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据。
            strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.err.println("Send post request exception: " + e);
            return "false";
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
            ossClient.shutdown();
        }
        return res;
    }
*/
    //追加上传

    //追加上传流
    //第一次追加上传（创建一个追加类型的文件）
    public static  String appendObjectStreamFirst(String bucketName,String objectPath,String contentType,String content){
        //contentType 常用值如下
        //纯文本：Content-Type text/plain
        // JPG:image/jpeg  gif:image/gif png:image/png  word:application/msword
        //jsp:text/html mp3:audio/mp3 mp4:video/mpeg4 ppt:application/vnd.ms-powerpoint
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName,objectPath,new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),metadata);

        // 通过AppendObjectRequest设置单个参数。
        // 设置Bucket名称。
        //appendObjectRequest.setBucketName("<yourBucketName>");
        // 设置Object名称。即不包含Bucket名称在内的Object的完整路径，例如example/test.txt。
        //appendObjectRequest.setKey("<yourObjectName>");
        // 设置待追加的内容。有两种可选类型：InputStream类型和File类型。这里为InputStream类型。
        //appendObjectRequest.setInputStream(new ByteArrayInputStream(content1.getBytes()));
        // 设置待追加的内容。有两种可选类型：InputStream类型和File类型。这里为File类型。
        //appendObjectRequest.setFile(new File("<yourLocalFile>"));
        // 指定文件的元信息，第一次追加时有效。
        //appendObjectRequest.setMetadata(meta);

        // 第一次追加。
        // 设置文件的追加位置。
        appendObjectRequest.setPosition(0L);
        AppendObjectResult appendObjectResult = ossClient.appendObject(appendObjectRequest);
        // 文件的64位CRC值。此值根据ECMA-182标准计算得出。
        System.out.println(appendObjectResult.getObjectCRC());

        String position = appendObjectResult.getNextPosition().toString();

        System.out.println(position);


        // 第二次追加。
        // nextPosition指明下一次请求中应当提供的Position，即文件当前的长度。
//        appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
//        appendObjectRequest.setInputStream(new ByteArrayInputStream(content2.getBytes()));
//        appendObjectResult = ossClient.appendObject(appendObjectRequest);
//        // 第三次追加。
//        appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
//        appendObjectRequest.setInputStream(new ByteArrayInputStream(content3.getBytes()));
//        appendObjectResult = ossClient.appendObject(appendObjectRequest);

        // 关闭OSSClient。
        ossClient.shutdown();

        return position;
    }

    //追加上传流（第二次及以后）
    public static  String appendObjectStream(String bucketName,String objectPath,String contentType,String content,String givenPosition){

        OSS ossClient = new OSSClientBuilder().
                build(endpoint,accessKeyId,accessKeySecret);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName,objectPath,new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),metadata);

        // 通过AppendObjectRequest设置单个参数。
        // 设置Bucket名称。
        //appendObjectRequest.setBucketName("<yourBucketName>");
        // 设置Object名称。即不包含Bucket名称在内的Object的完整路径，例如example/test.txt。
        //appendObjectRequest.setKey("<yourObjectName>");
        // 设置待追加的内容。有两种可选类型：InputStream类型和File类型。这里为InputStream类型。
        //appendObjectRequest.setInputStream(new ByteArrayInputStream(content1.getBytes()));
        // 设置待追加的内容。有两种可选类型：InputStream类型和File类型。这里为File类型。
        //appendObjectRequest.setFile(new File("<yourLocalFile>"));
        // 指定文件的元信息，第一次追加时有效。
        //appendObjectRequest.setMetadata(meta);


        appendObjectRequest.setPosition(Long.parseLong(givenPosition));
        AppendObjectResult appendObjectResult = ossClient.appendObject(appendObjectRequest);
        // 文件的64位CRC值。此值根据ECMA-182标准计算得出。
        System.out.println(appendObjectResult.getObjectCRC());

        String position = appendObjectResult.getNextPosition().toString();

        System.out.println(position);


        // 第二次追加。
        // nextPosition指明下一次请求中应当提供的Position，即文件当前的长度。
//        appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
//        appendObjectRequest.setInputStream(new ByteArrayInputStream(content2.getBytes()));
//        appendObjectResult = ossClient.appendObject(appendObjectRequest);
//        // 第三次追加。
//        appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
//        appendObjectRequest.setInputStream(new ByteArrayInputStream(content3.getBytes()));
//        appendObjectResult = ossClient.appendObject(appendObjectRequest);

        // 关闭OSSClient。
        ossClient.shutdown();

        return position;
    }


    //追加上传（文件）
    //创建
    //追加上传文件（第一次）
    public static  String appendObjectFileFirst(String bucketName,String objectPath,String contentType,String localPath){
        //contentType 常用值如下
        //纯文本：Content-Type text/plain
        // JPG:image/jpeg  gif:image/gif png:image/png  word:application/msword
        //jsp:text/html mp3:audio/mp3 mp4:video/mpeg4 ppt:application/vnd.ms-powerpoint
        //localPath:所选择本地文件的路径
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName,objectPath,new File(localPath),metadata);

        /*
         通过AppendObjectRequest设置单个参数。
         设置Bucket名称。
        appendObjectRequest.setBucketName("<yourBucketName>");
         设置Object名称。即不包含Bucket名称在内的Object的完整路径，例如example/test.txt。
        appendObjectRequest.setKey("<yourObjectName>");
         设置待追加的内容。有两种可选类型：InputStream类型和File类型。这里为InputStream类型。
        appendObjectRequest.setInputStream(new ByteArrayInputStream(content1.getBytes()));
         设置待追加的内容。有两种可选类型：InputStream类型和File类型。这里为File类型。
        appendObjectRequest.setFile(new File("<yourLocalFile>"));
         指定文件的元信息，第一次追加时有效。
        appendObjectRequest.setMetadata(meta);
         第一次追加。
         设置文件的追加位置。
        */

        appendObjectRequest.setPosition(0L);
        AppendObjectResult appendObjectResult = ossClient.appendObject(appendObjectRequest);
        // 文件的64位CRC值。此值根据ECMA-182标准计算得出。
        System.out.println(appendObjectResult.getObjectCRC());

        String position = appendObjectResult.getNextPosition().toString();

        System.out.println(position);


        // 关闭OSSClient。
        ossClient.shutdown();

        return position;
    }


    //追加上传文件
    public static  String appendObjectFile(String bucketName,String objectPath,String contentType,String localPath,String givenPosition){

        OSS ossClient = new OSSClientBuilder().
                build(endpoint,accessKeyId,accessKeySecret);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName,objectPath,new File(localPath),metadata);

        // 通过AppendObjectRequest设置单个参数。
        // 设置Bucket名称。
        //appendObjectRequest.setBucketName("<yourBucketName>");
        // 设置Object名称。即不包含Bucket名称在内的Object的完整路径，例如example/test.txt。
        //appendObjectRequest.setKey("<yourObjectName>");
        // 设置待追加的内容。有两种可选类型：InputStream类型和File类型。这里为InputStream类型。
        //appendObjectRequest.setInputStream(new ByteArrayInputStream(content1.getBytes()));
        // 设置待追加的内容。有两种可选类型：InputStream类型和File类型。这里为File类型。
        //appendObjectRequest.setFile(new File("<yourLocalFile>"));
        // 指定文件的元信息，第一次追加时有效。
        //appendObjectRequest.setMetadata(meta);


        appendObjectRequest.setPosition(Long.parseLong(givenPosition));
        AppendObjectResult appendObjectResult = ossClient.appendObject(appendObjectRequest);
        // 文件的64位CRC值。此值根据ECMA-182标准计算得出。
        System.out.println(appendObjectResult.getObjectCRC());

        String position = appendObjectResult.getNextPosition().toString();

        System.out.println(position);


        // 第二次追加。
        // nextPosition指明下一次请求中应当提供的Position，即文件当前的长度。
//        appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
//        appendObjectRequest.setInputStream(new ByteArrayInputStream(content2.getBytes()));
//        appendObjectResult = ossClient.appendObject(appendObjectRequest);
//        // 第三次追加。
//        appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
//        appendObjectRequest.setInputStream(new ByteArrayInputStream(content3.getBytes()));
//        appendObjectResult = ossClient.appendObject(appendObjectRequest);

        // 关闭OSSClient。
        ossClient.shutdown();

        return position;
    }

    //断点续传上传

    public static String checkPointUpload(String bucketName, String objectPath, String localFilePath, String contentType,int taskNum,int partSize) throws Throwable {
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(contentType);

        // 文件上传时设置访问权限ACL。
        // meta.setObjectAcl(CannedAccessControlList.Private);

        // 通过UploadFileRequest设置多个参数。
        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName,objectPath);

        // 通过UploadFileRequest设置单个参数。
        // 填写Bucket名称。
        //uploadFileRequest.setBucketName("examplebucket");
        // 填写Object完整路径。Object完整路径中不能包含Bucket名称。
        //uploadFileRequest.setKey(objectPath);
        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        uploadFileRequest.setUploadFile(localFilePath);
        // 指定上传并发线程数，默认值为1。
        uploadFileRequest.setTaskNum(taskNum);
        // 指定上传的分片大小。
        uploadFileRequest.setPartSize(partSize);
        // 开启断点续传，默认关闭。
        uploadFileRequest.setEnableCheckpoint(true);
        // 记录本地分片上传结果的文件。上传过程中的进度信息会保存在该文件中。
        uploadFileRequest.setCheckpointFile(objectPath+"CheckpointFile");
        String checkPointFile = uploadFileRequest.getCheckpointFile();
        System.out.println(checkPointFile);
        // 文件的元数据。
        uploadFileRequest.setObjectMetadata(meta);
        // 设置上传成功回调，参数为Callback类型。
        //uploadFileRequest.setCallback("yourCallbackEvent");

        // 断点续传上传。
        ossClient.uploadFile(uploadFileRequest);


        // 关闭OSSClient。
        ossClient.shutdown();

        return checkPointFile;

    }


    //分片上传
    public static String multipartUpload(String bucketName,String objectName,String localFilePath ){

        //objectName: 上传文件到oss时需要制定包含文件后缀在内的完整路径
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName);
        /*
         如果需要在初始化分片时设置文件存储类型，请参考以下示例代码。
         ObjectMetadata metadata = new ObjectMetadata();
         metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
         request.setObjectMetadata(metadata);
        */

        //初始化分片
        InitiateMultipartUploadResult uploadResult = ossClient.initiateMultipartUpload(request);
        //返回uploadId 分片上传的唯一标识
        String uploadId = uploadResult.getUploadId();

        //partEtags是partETag的集合 PartFTag由分片的ETag和分片号组成
        ArrayList<PartETag> partETags = new ArrayList<>();

        //计算文件有多少分片
        final long partSize = 1*1024*1024L;//1MB
        final File sampleFile = new File(localFilePath);
        long fileLength = sampleFile.length();
        int partCount = (int) (fileLength/partSize);
        if(fileLength % partSize != 0){
            partCount++;
        }
        //遍历分片上传
        try {
            for(int i = 0;i<partCount; i++){
                long startPos = i*partSize;
                long curPartSize = (i+1==partCount)?(fileLength -startPos):partSize;
                InputStream inputStream = new FileInputStream(sampleFile);
                inputStream.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(objectName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(inputStream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
                uploadPartRequest.setPartNumber( i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 创建CompleteMultipartUploadRequest对象。
        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);

        // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
        // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);

        // 完成上传。
        CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);

        // 关闭OSSClient。
        ossClient.shutdown();
        
        return uploadId;
    }

    //取消分片上传
    public static String abortMultipartUpload(String bucketName,String objectName,String uploadId){
        try{
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 取消分片上传，其中uploadId源自InitiateMultipartUpload。
            AbortMultipartUploadRequest abortMultipartUploadRequest =
                    new AbortMultipartUploadRequest(bucketName,objectName,uploadId);
            ossClient.abortMultipartUpload(abortMultipartUploadRequest);
            ossClient.shutdown();
        } catch (OSSException ossException) {
            ossException.printStackTrace();
            return "false";
        } catch (ClientException e) {
            e.printStackTrace();
            return "false";
        }


        // 关闭OSSClient。


        return "success";
    }

    //列举已上传分片
    //简单列举已上传的分片
    public static List<Map<String,String>> simpleListParts(String bucketName,String objectName, String uploadId){
        //maxParts:每个分页的分片数量
        //marker: 分片的起始位置
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName,objectName,uploadId);
        // 设置uploadId。
        //listPartsRequest.setUploadId(uploadId);
        //设置分页时每一个分页的分片数量，默认值为1000
        listPartsRequest.setMaxParts(100);
        //指定列举的起始位置，只有分片号大于此参数值得分片会被列举
        listPartsRequest.setPartNumberMarker(0);

        PartListing partListing = ossClient.listParts(listPartsRequest);

        List<Map<String, String>> list = new ArrayList<>();

        for(PartSummary part : partListing.getParts()) {
            //获取分片号
            String partNumber = String.valueOf(part.getPartNumber());
            //获取分片数据大小
            String size = String.valueOf(part.getSize());
            //获取分片的最后修改时间
            String lastModified = String.valueOf(part.getLastModified());

            Map<String, String> map = new HashMap<>();
            map.put("partNumber",partNumber);
            map.put("size",size);
            map.put("lastModified",lastModified);
            list.add(map);
        }

        ossClient.shutdown();
        return list;
    }
    //列举所有已上传分片
    //默认情况下，listParts()方法一次只能列举1000个分片，当分片数大于1000时，需要以下方法
    public static List<Map<String,String>> listPartsAll(String bucketName,String objectName,String uploadId){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 列举所有已上传的分片。
        PartListing partListing;
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName,objectName,uploadId);
        List<Map<String, String>> list = new ArrayList<>();
        do {
            partListing = ossClient.listParts(listPartsRequest);

            for (PartSummary part : partListing.getParts()) {
                //获取分片号
                String partNumber = String.valueOf(part.getPartNumber());
                //获取分片数据大小
                String size = String.valueOf(part.getSize());
                //获取分片的最后修改时间
                String lastModified = String.valueOf(part.getLastModified());
                // 获取分片的ETag。
                String eTag = part.getETag();

                Map<String, String> map = new HashMap<>();
                map.put("partNumber",partNumber);
                map.put("size",size);
                map.put("lastModified",lastModified);
                map.put("eTag",eTag);
                list.add(map);
            }
        // 指定List的起始位置，只有分片号大于此参数值的分片会被列出。
            listPartsRequest.setPartNumberMarker(partListing.getNextPartNumberMarker());
        } while (partListing.isTruncated());

        // 关闭OSSClient。
        ossClient.shutdown();

        return list;
    }

    //分页列举符合要求的已上传分片
    public static List<Map<String,String>> listPartsByPaper(String bucketName,String objectName, String uploadId,int maxParts,int marker){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 列举已上传的分片，其中uploadId来自于InitiateMultipartUpload返回的结果。
        ListPartsRequest listPartsRequest = new ListPartsRequest("<yourBucketName>", "<yourObjectName>", "<uploadId>");
        // 设置uploadId。
        //listPartsRequest.setUploadId(uploadId);
        // 设置分页时每一页中分片数量为100个。默认列举1000个分片。
        listPartsRequest.setMaxParts(maxParts);
        // 指定List的起始位置。只有分片号大于此参数值的分片会被列举。
        listPartsRequest.setPartNumberMarker(marker);
        PartListing partListing = ossClient.listParts(listPartsRequest);

        List<Map<String, String>> list = new ArrayList<>();

        for (PartSummary part : partListing.getParts()) {
            //获取分片号
            String partNumber = String.valueOf(part.getPartNumber());
            //获取分片数据大小
            String size = String.valueOf(part.getSize());
            //获取分片的最后修改时间
            String lastModified = String.valueOf(part.getLastModified());
            // 获取分片的ETag。
            String eTag = part.getETag();

            Map<String, String> map = new HashMap<>();
            map.put("partNumber",partNumber);
            map.put("size",size);
            map.put("lastModified",lastModified);
            map.put("eTag",eTag);
            list.add(map);


        }

        // 关闭OSSClient。
        ossClient.shutdown();
        return list;
    }
    //列举分片上传事件 包括已初始化但尚未完成或已取消的分片上传事件 这是对一个存储空间的分片上传事件进行的操作
    //简单列举分片上传事件
    public static List<Map<String, String>> simpleListMultipartUploads(String bucketName){

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 列举分片上传事件。默认列举1000个分片。
        ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest(bucketName);
        MultipartUploadListing multipartUploadListing = ossClient.listMultipartUploads(listMultipartUploadsRequest);

        List<Map<String, String>> list = new ArrayList<>();
        for (MultipartUpload multipartUpload : multipartUploadListing.getMultipartUploads()) {
            // 获取uploadId。
            String uploadId = multipartUpload.getUploadId();

            // 获取文件名称。
            String key = multipartUpload.getKey();

            // 获取分片上传的初始化时间。
            String initDate = multipartUpload.getInitiated().toString();

            Map<String, String> map = new HashMap<>();
            map.put("uploadId",uploadId);
            map.put("key",key);
            map.put("initDate",initDate);
            list.add(map);
        }

        // 关闭OSSClient。
        ossClient.shutdown();

        return list;
    }
    //列举全部分片上传事件
    //大于1000时调用此方法
    public static List<Map<String, String>> listMultipartUploads(String bucketName){
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 列举分片上传事件。
        MultipartUploadListing multipartUploadListing;
        ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest(bucketName);

        List<Map<String, String>> list = new ArrayList<>();

        do {
            multipartUploadListing = ossClient.listMultipartUploads(listMultipartUploadsRequest);

            for (MultipartUpload multipartUpload : multipartUploadListing.getMultipartUploads()) {

                // 获取uploadId。
                String uploadId = multipartUpload.getUploadId();

                // 获取文件名称。
                String key = multipartUpload.getKey();

                // 获取分片上传的初始化时间。
                String initDate = multipartUpload.getInitiated().toString();

                Map<String, String> map = new HashMap<>();
                map.put("uploadId",uploadId);
                map.put("key",key);
                map.put("initDate",initDate);
                list.add(map);
            }

            listMultipartUploadsRequest.setKeyMarker(multipartUploadListing.getNextKeyMarker());

            listMultipartUploadsRequest.setUploadIdMarker(multipartUploadListing.getNextUploadIdMarker());
        } while (multipartUploadListing.isTruncated());

        // 关闭OSSClient。
//        System.out.println("111");
        ossClient.shutdown();
        return list;
    }


    //分页列举所有上传事件
    //可以规定每页列举的分片上传事件数目

    public static List<Map<String,String>> listMultipartUploadsByPapper(String bucketName,int maxUploads){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 列举分片上传事件。
        MultipartUploadListing multipartUploadListing;
        ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest(bucketName);
        // 设置每页列举的分片上传事件数目。
        listMultipartUploadsRequest.setMaxUploads(maxUploads);

        List<Map<String, String>> list = new ArrayList<>();

        do {
            multipartUploadListing = ossClient.listMultipartUploads(listMultipartUploadsRequest);

            for (MultipartUpload multipartUpload : multipartUploadListing.getMultipartUploads()) {
                // 获取uploadId。
                String uploadId = multipartUpload.getUploadId();

                // 获取文件名称。
                String key = multipartUpload.getKey();

                // 获取分片上传的初始化时间。
                String initDate = multipartUpload.getInitiated().toString();

                Map<String, String> map = new HashMap<>();
                map.put("uploadId",uploadId);
                map.put("key",key);
                map.put("initDate",initDate);
                list.add(map);
            }

            listMultipartUploadsRequest.setKeyMarker(multipartUploadListing.getNextKeyMarker());
            listMultipartUploadsRequest.setUploadIdMarker(multipartUploadListing.getNextUploadIdMarker());

        } while (multipartUploadListing.isTruncated());

        // 关闭OSSClient。
        ossClient.shutdown();

        return list;
    }

    public static void main(String[] args) throws Throwable {
        /*
                byte[] haha = "Naruto come on".getBytes(StandardCharsets.UTF_8);
                putString("hello onePiece", "xmsx-00o1", "hello.txt");
                putBytes(haha,"xmsx-001", "hello2.txt");
                putStream("https://www.aliyun.com/", "xmsx-001", "testStream.txt");
                putFileStream("F:\\数值计算\\实验一截图1.png","xmsx-001", "picture1.png");
                putFile("F:\\Temp\\hhh.txt","xmsx-001","123.txt");
        */

        /*
                Map<String, String> map = new HashMap<>();
                map.put("123", "jsa");
                formUpload("xmsx-001", map, "F:\\Temp\\hhh.txt");
                appendObjectStreamFirst("xmsx-001", "append1.txt", "text/plain", "i am the first");
                appendObjectStream("xmsx-001", "append1.txt", "text/plain", "i am the first", "14");
                appendObjectFileFirst("xmsx-001", "append2.txt", "text/plain", "F:\\Download\\testStream.txt");
                appendObjectFile("xmsx-001", "append2.txt", "text/plain", "F:\\Download\\testStream.txt","124223");
        */
        //    checkPointUpload("xmsx-001","car.jpg", "C:\\Users\\DELL\\Pictures\\runningcar.jpg","image.jpeg" );
        //        multipartUpload("xmsx-001","操作系统.pdf","F:\\Download\\[操作系统概念(第7版)].(Operating.System.Concepts).((美)西尔伯查茨).扫描版(ED2000.COM).pdf");
        listMultipartUploads("xmsx-001");
    }
}
