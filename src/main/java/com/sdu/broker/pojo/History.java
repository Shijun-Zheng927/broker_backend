package com.sdu.broker.pojo;

public class History {
    private Integer id;
    private String url;
    private Integer user;
    private Double size;
    private Double price;
    private String time;
    private Integer type;
    private String bucketName;
    private String platform;
    private String ud;

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", user=" + user +
                ", size=" + size +
                ", price=" + price +
                ", time='" + time + '\'' +
                ", type=" + type +
                ", bucketName='" + bucketName + '\'' +
                ", platform='" + platform + '\'' +
                ", ud='" + ud + '\'' +
                '}';
    }

    public String getUd() {
        return ud;
    }

    public void setUd(String ud) {
        this.ud = ud;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
