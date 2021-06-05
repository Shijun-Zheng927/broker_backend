package com.sdu.broker.aliyun.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;
@Component
public class AliDownloadController {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5tE3U2xuvubTk8qocyd2";
    private static String accessKeySecret = "Q0cqcMmjKGBmyRM6s0G51QYCMSn6aO";


    //流式下载
    public  String  streamDownload(String bucketName,String objectName){
        String content="";
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        try {
            OSSObject ossObject = ossClient.getObject(bucketName,objectName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
            while (true){
                String  line = bufferedReader.readLine();
                if(line==null) break;
                System.out.println("\n"+line);
                content = content + line;
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
        ossClient.shutdown();
        System.out.println(content);
        return content;
    }

    //下载到本地文件
    public static  String homeDownload(String bucketName, String objectName, String localPath){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        //下载object到本地文件，并保存到指定的本地路径。若存在则覆盖，不存在则新建
        try {
            ossClient.getObject(new GetObjectRequest(bucketName,objectName), new File(localPath));
        } catch (OSSException ossException) {
            ossException.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        ossClient.shutdown();
        //返回本地文件路径
        return localPath;

    }

    //范围下载
    //指定正常的下载范围
    /*
    指定异常的下载范围
    假设现有大小为1000 Bytes的Object，则指定的正常下载范围应为0~999。如果指定范围不在有效区间，会导致Range不生效，响应返回值为200，并传送整个Object的内容。
    请求不合法的示例及返回说明如下：
    若指定了Range: bytes=500~2000，此时范围末端取值不在有效区间，返回整个文件的内容，且HTTP Code为200。
    若指定了Range: bytes=1000~2000，此时范围首端取值不在有效区间，返回整个文件的内容，且HTTP Code为200。
     */
    public  String rangeDownload(String bucketName,String objectName, String localFile, int begin,int end){
        /*
        输入参数：bucketName:存储空间名称
                objectName：存储文件名称
                localPath：下载到本地文件路径。若不指定完整路径则下载到当前项目目录
                begin：读取文件的起始点(Bytes)
                end：读取文件的终止点(Bytes)
         */
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
        getObjectRequest.setRange(begin,end);
        OSSObject object = ossClient.getObject(getObjectRequest);

        //读取数据
        byte[] buf = new byte[1024];
        InputStream in = object.getObjectContent();

        try {
            OutputStream out = new FileOutputStream(localFile);
            //读取数据 写入到文件
            for (int n = 0;n!=-1;) {
                n = in.read(buf, 0, buf.length);
                out.write(buf);
            }
            //关闭输出流
            out.close();
            //关闭输入流
            in.close();
        }catch (IOException e) {
            e.printStackTrace();
            return "false!";
        }
        ossClient.shutdown();
        return localFile;
    }

    //兼容行为范围下载
    /*
    对于上述方法，如果给定的首尾不在文件的有效区间内，通过增加请求头来改变下载行为
    若范围末端取值不在有效区间，返回能读取的字节范围内容，HTTP Code 为206
    若范围首端取值不在有效区间，返回HTTP Code 为416，错误码为InvalidRange
     */
    public static String rangeDownloadCompatible(String bucketName, String objectName, String localFile, int begin,int end){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
        getObjectRequest.setRange(begin,end);
        getObjectRequest.addHeader("x-oss-range-behavior", "standard");
        OSSObject ossObject = ossClient.getObject(getObjectRequest);
        try {
            ossObject.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("standard get " + "statusCode:"  + ossObject.getResponse().getStatusCode());
        System.out.println("standard get " + "contentLength:" + ossObject.getResponse().getContentLength());
        try {
            // 范围首端取值不在有效区间，以下代码会抛出异常。返回HTTP Code为416，错误码为InvalidRange。
            getObjectRequest = new GetObjectRequest(bucketName, objectName);
            getObjectRequest.setRange(1000, 2000);
            getObjectRequest.addHeader("x-oss-range-behavior", "standard");
            ossClient.getObject(getObjectRequest);
        } catch (OSSException e) {
            System.out.println("standard get "  + "error code:" + e.getErrorCode());
        }

        // 关闭OSSClient。
        ossClient.shutdown();
        return localFile;
    }

    //断点续传下载
    public  String checkPointDownload(String bucketName, String objectName, String localFile,
                                            int partSize,int taskNum){
        /*
        localPath: 文件本地保存路径
        checkPointFile: 如果分片上传未完成，再次下载时会根据该文件中的记录的点继续下载。下载完成后该文件被删除,与下载文件同路径，名称后缀为dcp
        partSize: 分片大小，取值为1B - 5GB
        taskNum: 分片下载的并发数
         */
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, objectName);
        downloadFileRequest.setDownloadFile(localFile);
        downloadFileRequest.setTaskNum(taskNum);
        downloadFileRequest.setPartSize(partSize);
        downloadFileRequest.setEnableCheckpoint(true);
        //下载文件
        DownloadFileResult downloadFileResult = null;
        try {
            downloadFileResult = ossClient.downloadFile(downloadFileRequest);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //下载成功时返回文件元信息
        downloadFileResult.getObjectMetadata();

        ossClient.shutdown();

        return localFile;

    }

    //限定条件下载

    public static String delimitDownload(String bucketName, String objectName, String localFile,Date date){
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        GetObjectRequest request = new GetObjectRequest(bucketName, objectName);
        request.setModifiedSinceConstraint(date);
            ossClient.getObject(request, new File(localFile));
        ossClient.shutdown();
        return localFile;
    }
    public static void main(String[] args) {

/*
        streamDownload("xmsx-001","hello2.txt");
        rangeDownload("xmsx-001", "append2.txt","text.txt",0,500);
        checkPointDownload("xmsx-001","操作系统.pdf","F:\\Temp\\操作系统.pdf",1*1024*1024,10);
*/

    }



}
