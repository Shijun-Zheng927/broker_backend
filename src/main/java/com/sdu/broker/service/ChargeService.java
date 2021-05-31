package com.sdu.broker.service;

public interface ChargeService {
    Integer operate(String bucketName, double size, String url, Integer user);
}
