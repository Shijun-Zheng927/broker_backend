package com.sdu.broker.pojo;

public class StorageType {
    private Integer id;                     // id
    private String platform;                // 云平台 ALI=阿里云 HUAWEI=华为云
    private String type;                    // 存储类型
    private String scene;                   // 适用场景
    private String minMeteringSize;         // 对象最小计量大小
    private String minStorageTime;          // 最少存储时间要求
    private String dataAccessFeatures;      // 数据访问特点
    private String imageProcessing;         // 图片处理
    private String dataRetrievalFee;        // 数据取回费用
    private String ossAcceleration;         // OSS传输加速(上传/下载加速)
    private Double lrsPrice;                // 存储空间价格(本地冗余LRS) (/GB/月)
    private Double zrsPrice;               // 存储空间价格(同城冗余ZRS)
    private String introduction;            // 简介
    private String designPersistenceSingle; // 设计持久性（单AZ）
    private String designAvailabilitySingle;// 设计可用性（单AZ）
    private String designPersistenceMulti;  // 设计持久性（多AZ）
    private String designAvailabilityMulti; // 设计可用性（多AZ）
    private String responseTime;            // 响应时间
    private Double huaweiPrice;             // 华为云价格 (/月/GB)

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getMinMeteringSize() {
        return minMeteringSize;
    }

    public void setMinMeteringSize(String minMeteringSize) {
        this.minMeteringSize = minMeteringSize;
    }

    public String getMinStorageTime() {
        return minStorageTime;
    }

    public void setMinStorageTime(String minStorageTime) {
        this.minStorageTime = minStorageTime;
    }

    public String getDataAccessFeatures() {
        return dataAccessFeatures;
    }

    public void setDataAccessFeatures(String dataAccessFeatures) {
        this.dataAccessFeatures = dataAccessFeatures;
    }

    public String getImageProcessing() {
        return imageProcessing;
    }

    public void setImageProcessing(String imageProcessing) {
        this.imageProcessing = imageProcessing;
    }

    public String getDataRetrievalFee() {
        return dataRetrievalFee;
    }

    public void setDataRetrievalFee(String dataRetrievalFee) {
        this.dataRetrievalFee = dataRetrievalFee;
    }

    public String getOssAcceleration() {
        return ossAcceleration;
    }

    public void setOssAcceleration(String ossAcceleration) {
        this.ossAcceleration = ossAcceleration;
    }

    public Double getLrsPrice() {
        return lrsPrice;
    }

    public void setLrsPrice(Double lrsPrice) {
        this.lrsPrice = lrsPrice;
    }

    public Double getZrsPrice() {
        return zrsPrice;
    }

    public void setZrsPrice(Double zrsPrice) {
        this.zrsPrice = zrsPrice;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getDesignPersistenceSingle() {
        return designPersistenceSingle;
    }

    public void setDesignPersistenceSingle(String designPersistenceSingle) {
        this.designPersistenceSingle = designPersistenceSingle;
    }

    public String getDesignAvailabilitySingle() {
        return designAvailabilitySingle;
    }

    public void setDesignAvailabilitySingle(String designAvailabilitySingle) {
        this.designAvailabilitySingle = designAvailabilitySingle;
    }

    public String getDesignPersistenceMulti() {
        return designPersistenceMulti;
    }

    public void setDesignPersistenceMulti(String designPersistenceMulti) {
        this.designPersistenceMulti = designPersistenceMulti;
    }

    public String getDesignAvailabilityMulti() {
        return designAvailabilityMulti;
    }

    public void setDesignAvailabilityMulti(String designAvailabilityMulti) {
        this.designAvailabilityMulti = designAvailabilityMulti;
    }

    public Double getHuaweiPrice() {
        return huaweiPrice;
    }

    public void setHuaweiPrice(Double huaweiPrice) {
        this.huaweiPrice = huaweiPrice;
    }
}
