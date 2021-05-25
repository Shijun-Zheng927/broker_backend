package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HuaweiDownloadController {
    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-1.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    //生成下载请求
    public GetObjectRequest request(String bucketName,String objectKey){
        GetObjectRequest request = new GetObjectRequest(bucketName, objectKey);
        return request;
    }

    //流式下载
    public String streamDownload(GetObjectRequest request){
        String s = new String();
        try{
            ObsObject obsObject = obsClient.getObject(request);
            // 读取对象内容
            System.out.println("Object content:");
            InputStream input = obsObject.getObjectContent();
            byte[] b = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            while ((len=input.read(b)) != -1){
                bos.write(b, 0, len);
            }
            s = new String(bos.toByteArray());
            System.out.println(s);
            bos.close();
            input.close();
        }catch(IOException e){
            return "IO exception!";
        }catch (ObsException e){
            return "OBS exception!";
        }
        return s;
    }

    //范围下载
    public String rangeDownload(GetObjectRequest request,String localFile,long begin,long end){

        // 指定开始和结束范围
        request.setRangeStart(begin);
        request.setRangeEnd(end);
        try{
            ObsObject obsObject = obsClient.getObject(request);
            OutputStream out = new FileOutputStream(localFile);

            // 读取数据
            byte[] buf = new byte[1024];
            InputStream in = obsObject.getObjectContent();
            for (int n = 0; n != -1; ) {
                n = in.read(buf, 0, buf.length);
                out.write(buf);
            }
            out.close();
            in.close();
        }catch(IOException e){
            return "IO exception!";
        }catch (ObsException e){
            return "OBS exception!";
        }
        return localFile;
    }

    //获取下载进度
    /*
    public Map<String,String> progressListener(String bucketName,String objectKey){
        GetObjectRequest request = new GetObjectRequest(bucketName, objectKey);
        Map<String,String> map = new HashMap<>();
        request.setProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressStatus status) {
                // 获取下载平均速率
                System.out.println("AverageSpeed:" + status.getAverageSpeed());

                // 获取下载进度百分比
                System.out.println("TransferPercentage:" + status.getTransferPercentage());
                map.put(String.format("%.3f",status.getAverageSpeed()),String.format("%.3f",status.getTransferPercentage()));
            }
        });
// 每下载1MB数据反馈下载进度
        request.setProgressInterval(1024 * 1024L);
        ObsObject obsObject = obsClient.getObject(request);
        return map;

    }
    */

    //限定条件下载
    public String setIfModifiedSince(GetObjectRequest request, Date date){
        request.setIfModifiedSince(date);
        return "success";
    }
    public String setIfUnModifiedSince(GetObjectRequest request, Date date){
        request.setIfUnmodifiedSince(date);
        return "success";
    }
    public String setIfMatchTag(GetObjectRequest request, PartEtag etag){
        request.setIfMatchTag(etag.getEtag());
        return "success";
    }
    public String setIfNoneMatchTag(GetObjectRequest request, PartEtag etag){
        request.setIfNoneMatchTag(etag.getEtag());
        return "success";
    }

    //重写HTTP/HTTPS响应头信息
    public String setContentType(GetObjectRequest request,String c){
        ObjectRepleaceMetadata replaceMetadata = new ObjectRepleaceMetadata();
        replaceMetadata.setContentType(c);
        request.setReplaceMetadata(replaceMetadata);
        return "success";
    }
    public String setContentLanguage(GetObjectRequest request,String c){
        ObjectRepleaceMetadata replaceMetadata = new ObjectRepleaceMetadata();
        replaceMetadata.setContentLanguage(c);
        request.setReplaceMetadata(replaceMetadata);
        return "success";
    }
    public String setExpires(GetObjectRequest request,String c){
        ObjectRepleaceMetadata replaceMetadata = new ObjectRepleaceMetadata();
        replaceMetadata.setExpires(c);
        request.setReplaceMetadata(replaceMetadata);
        return "success";
    }
    public String setCacheControl(GetObjectRequest request,String c){
        ObjectRepleaceMetadata replaceMetadata = new ObjectRepleaceMetadata();
        replaceMetadata.setCacheControl(c);
        request.setReplaceMetadata(replaceMetadata);
        return "success";
    }
    public String setContentDisposition(GetObjectRequest request,String c){
        ObjectRepleaceMetadata replaceMetadata = new ObjectRepleaceMetadata();
        replaceMetadata.setContentDisposition(c);
        request.setReplaceMetadata(replaceMetadata);
        return "success";
    }
    public String setContentEncoding(GetObjectRequest request,String c){
        ObjectRepleaceMetadata replaceMetadata = new ObjectRepleaceMetadata();
        replaceMetadata.setContentEncoding(c);
        request.setReplaceMetadata(replaceMetadata);
        return "success";
    }
}
