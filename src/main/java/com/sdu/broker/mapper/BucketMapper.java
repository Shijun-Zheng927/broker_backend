package com.sdu.broker.mapper;

import com.sdu.broker.pojo.Bucket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BucketMapper {
    @Select("select id from bucket where platform = #{platform} and name = #{name}")
    Integer isExist(Bucket bucket);

    @Select("select id from bucket where user_id = #{userId} and platform = #{platform} and name = #{name}")
    Integer isLegal(Bucket bucket);

    @Update("insert into bucket (user_id, platform, name) values(#{userId}, #{platform}, #{name})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    Integer addBucket(Bucket bucket);

    @Delete("delete from bucket where platform = #{platform} and name = #{name}")
    Integer deleteBucket(Bucket bucket);
}
