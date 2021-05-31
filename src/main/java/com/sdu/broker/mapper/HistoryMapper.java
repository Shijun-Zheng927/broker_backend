package com.sdu.broker.mapper;

import com.sdu.broker.pojo.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface HistoryMapper {
    @Update("insert into history (url, user, size, price, time, type, bucketName, platform) " +
            "values(#{url}, #{user}, #{size}, #{price}, #{time}, #{type}, #{bucketName}, #{platform})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    Integer addHistory(History history);
}
