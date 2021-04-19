package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;


import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class UpdateController {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";

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
            ossClient.shutdown();

            System.out.println(content);
        } catch (OSSException ossException) {
            System.out.println("出错了智障");
            return "false";
        }
        
        return "上传字符串成功";
    }
    
    //上传Byte数组
    public static String putBytes(byte[] bytes,String bucketName, String objectPath){
        //输入参数：bytes 需要上传的byte数组
        //        objectPath 存储路径（不包含存储空间名称）
        try {
            //创建ossClient实例
            OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
            //填写输入参数
            ossClient.putObject(bucketName, objectPath, new ByteArrayInputStream(bytes));
            //关闭ossClient
            ossClient.shutdown();
        } catch (OSSException ossException) {
            ossException.printStackTrace();
            return "false";
        } catch (ClientException e) {
            e.printStackTrace();
            return "false";
        }

        return "上传Byte数组成功";
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

        ossClient.shutdown();
        return "上传网络流成功";
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

        ossClient.shutdown();
        return "上传文件流成功！";
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
    //
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





    public static void main(String[] args) {
        /*
                byte[] haha = "Naruto come on".getBytes(StandardCharsets.UTF_8);
                putString("hello onePiece", "xmsx-00o1", "hello.txt");
                putBytes(haha,"xmsx-001", "hello2.txt");
                putStream("https://www.aliyun.com/", "xmsx-001", "testStream.txt");
                putFileStream("F:\\数值计算\\实验一截图1.png","xmsx-001", "picture1.png");
                putFile("F:\\Temp\\hhh.txt","xmsx-001","123.txt");
        */

//        Map<String, String> map = new HashMap<>();
//        map.put("123", "jsa");
//        formUpload("xmsx-001", map, "F:\\Temp\\hhh.txt");
//        appendObjectStreamFirst("xmsx-001", "append1.txt", "text/plain", "i am the first");
//        appendObjectStream("xmsx-001", "append1.txt", "text/plain", "i am the first", "14");
    }


}
