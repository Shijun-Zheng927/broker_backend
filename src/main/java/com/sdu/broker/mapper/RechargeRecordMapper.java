package com.sdu.broker.mapper;

import com.sdu.broker.pojo.RechargeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RechargeRecordMapper {
    @Update("insert into recharge_record(user_id, amount, time, result) values (#{userId}, #{amount}, #{time}, #{result})")
    Integer addRecord(RechargeRecord rechargeRecord);
}
