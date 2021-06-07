package com.sdu.broker.pojo;

public class Bucket {
    private Integer id;
    private Integer userId;
    private String platform;
    private String name;
    private Integer type;

    public Bucket() {
    }

    public Bucket(String platform, String name) {
        this.platform = platform;
        this.name = name;
    }

    public Bucket(Integer userId, String platform, String name) {
        this.userId = userId;
        this.platform = platform;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
