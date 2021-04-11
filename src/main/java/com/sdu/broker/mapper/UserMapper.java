package com.sdu.broker.mapper;

import com.sdu.broker.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select phone,username from user where phone = #{phone} and password = #{password}")
    User login(User user);
}
