package com.sdu.broker.mapper;

import com.sdu.broker.pojo.RechargeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RechargeRecordMapper {
    @Update("insert into recharge_record(user_id, amount, time, result, orderNum) " +
            "values (#{userId}, #{amount}, #{time}, #{result}, #{orderNum})")
    Integer addRecord(RechargeRecord rechargeRecord);

    @Update("select * from recharge_record where user_id = #{id}")
    List<RechargeRecord> getRecord(String id);
}
