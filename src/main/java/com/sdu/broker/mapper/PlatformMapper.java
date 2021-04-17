package com.sdu.broker.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PlatformMapper {
    @Select("select platform from platform where user_id = #{id}")
    String getPlatform(Integer id);
}
