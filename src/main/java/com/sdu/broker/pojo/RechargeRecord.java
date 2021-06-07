package com.sdu.broker.pojo;

public class RechargeRecord {
    private Integer id;
    private Integer userId;
    private Double amount;
    private String time;
    private String result;
    private String orderNum;

    public RechargeRecord(Integer userId, Double amount, String time, String result, String orderNum) {
        this.userId = userId;
        this.amount = amount;
        this.time = time;
        this.result = result;
        this.orderNum = orderNum;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
}
