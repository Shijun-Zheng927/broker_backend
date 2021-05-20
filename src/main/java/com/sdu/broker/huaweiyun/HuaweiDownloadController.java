package com.sdu.broker.huaweiyun;

import com.obs.services.ObsClient;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.ObsObject;

import java.io.*;

public class HuaweiDownloadController {
    /* 初始化OBS客户端所需的参数 */
    private static final String endPoint     = "https://obs.cn-north-4.myhuaweicloud.com";
    private static final String ak           = "XR4PD1I3LLF52K1KDRRG";
    private static final String sk           = "BD5DfWx2w3Od8XGCiuqsJPXfYJiKucNofuQUuZD4";
    public static ObsClient obsClient = new ObsClient(ak,sk,endPoint);

    public String streamDownload(String bucketName,String objectKey){
        ObsObject obsObject = obsClient.getObject(bucketName, objectKey);
        String s = new String();
        try{
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
        }
        return s;
    }

    public String rangeDownload(String bucketName,String objectKey,String localFile,int begin,int end){

        GetObjectRequest request = new GetObjectRequest(bucketName, objectKey);
        // 指定开始和结束范围
        request.setRangeStart(0l);
        request.setRangeEnd(1000l);
        ObsObject obsObject = obsClient.getObject(request);

        try{
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

        }
        return localFile;
    }

}
