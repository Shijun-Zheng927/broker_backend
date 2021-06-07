package com.sdu.broker.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountMapper {
    @Select("select account from account where user_id = #{id}")
    Double getAccount(Integer id);

    @Update("insert into account(user_id, account) values (#{id}, 0)")
    void register(Integer id);

    @Update("update account set account = #{account} where user_id = #{id} ")
    Integer recharge(Integer id, Double account);
}
