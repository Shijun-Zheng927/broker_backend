package com.sdu.broker.service;

public interface AccountService {
    Double getAccount(String id);

    Integer recharge(String id, Double amount);
}
