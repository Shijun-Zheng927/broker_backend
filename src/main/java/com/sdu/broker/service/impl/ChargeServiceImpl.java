package com.sdu.broker.service.impl;

import com.sdu.broker.mapper.AccountMapper;
import com.sdu.broker.mapper.BucketMapper;
import com.sdu.broker.mapper.HistoryMapper;
import com.sdu.broker.mapper.PriceMapper;
import com.sdu.broker.pojo.History;
import com.sdu.broker.pojo.Price;
import com.sdu.broker.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ChargeServiceImpl implements ChargeService {
    @Autowired
    private BucketMapper bucketMapper;
    @Autowired
    private PriceMapper priceMapper;
    @Autowired
    private HistoryMapper historyMapper;
    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Integer operate(String bucketName, double size, String url, Integer user, String ud) {
        Integer type = bucketMapper.getType(bucketName);
        String platform = bucketMapper.getPlatform(bucketName);
        Price p = new Price();
        p.setType(type);
        p.setPlatform(platform);
        Double price = priceMapper.getPrice(p);
        History h = new History();
        h.setPrice(price * size);
        h.setType(type);
        h.setSize(size);
        h.setUrl(url);
        h.setUser(user);
        h.setBucketName(bucketName);
        h.setPlatform(bucketMapper.getPlatform(bucketName));
        Date today=new Date();
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=f.format(today);
        h.setTime(time);
        h.setUd(ud);
        historyMapper.addHistory(h);

        Double money = accountMapper.getAccount(user);
        System.out.println(money);
        money -= price * size;
        System.out.println(money);
        Integer result = accountMapper.recharge(user, money);
        return null;
    }
}
