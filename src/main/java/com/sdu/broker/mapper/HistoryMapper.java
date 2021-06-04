package com.sdu.broker.mapper;

import com.sdu.broker.pojo.History;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HistoryMapper {
    @Update("insert into history (url, user, size, price, time, type, bucketName, platform, ud) " +
            "values(#{url}, #{user}, #{size}, #{price}, #{time}, #{type}, #{bucketName}, #{platform}, #{ud})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    Integer addHistory(History history);

    @Select("select id from history where #{time1} < time and #{time2} > time")
    Integer selectTime(String time1, String time2);

    @Select("select * from history where user=#{user}")
    List<History> getHistory(Integer user);

    @Select("select sum(size) from history where user=#{user} and ud='upload'")
    Double getUpload(Integer user);

    @Select("select sum(size) from history where user=#{user} and ud='download'")
    Double getDownload(Integer user);

    @Select("select sum(size) from history where bucketName=#{bucketName} and ud='upload'")
    Double getBucketUpFlow(String bucketName);

    @Select("select sum(size) from history where bucketName=#{bucketName} and ud='download'")
    Double getBucketDownFlow(String bucketName);
}
