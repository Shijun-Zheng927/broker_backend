package com.sdu.broker.mapper;

import com.sdu.broker.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select phone,username from user where phone = #{phone} and password = #{password}")
    User login(User user);

    @Update("insert into user(phone, password, username, email, company, introduction, occupation) values" +
            "(#{phone}, #{password}, #{username}, #{email}, #{company}, #{introduction}, #{occupation})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    void register(User user);

    @Select("select id from user where phone = #{phone}")
    Integer selectPhone(String str);
}
