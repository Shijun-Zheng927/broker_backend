package com.sdu.broker.mapper;

import com.sdu.broker.pojo.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper {
    @Select("select account from account where user_id = #{id}")
    Double getAccount(Integer id);
}
