package com.sdu.broker.mapper;

import com.sdu.broker.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from user where phone = #{phone} and password = #{password}")
    User login(User user);

    @Update("insert into user(phone, password, username, email, company, introduction, occupation, head) values" +
            "(#{phone}, #{password}, #{username}, #{email}, #{company}, #{introduction}, #{occupation}, #{head})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    void register(User user);

    @Select("select id from user where phone = #{phone}")
    Integer selectPhone(String str);

    @Update("update user set head = #{head} where id = #{id}")
    Integer setHead(String head, Integer id);

    @Update("update user set phone = #{phone} where id = #{id}")
    Integer setPhone(String phone, Integer id);

    @Update("update user set password = #{password} where id = #{id}")
    Integer setPassword(String password, Integer id);
}
