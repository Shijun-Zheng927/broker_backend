package com.sdu.broker.mapper;

import com.sdu.broker.pojo.History;
import org.apache.ibatis.annotations.*;

@Mapper
public interface HistoryMapper {
    @Update("insert into history (url, user, size, price, time, type, bucketName, platform, ud) " +
            "values(#{url}, #{user}, #{size}, #{price}, #{time}, #{type}, #{bucketName}, #{platform}, #{ud})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    Integer addHistory(History history);

    @Select("select id from history where #{time1} < time and #{time2} > time")
    Integer selectTime(String time1, String time2);
}
