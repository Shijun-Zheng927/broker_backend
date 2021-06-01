package com.sdu.broker.mapper;

import com.sdu.broker.pojo.Bucket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BucketMapper {
    @Select("select id from bucket where platform = #{platform} and name = #{name}")
    Integer isExist(Bucket bucket);

    @Select("select id from bucket where user_id = #{userId} and platform = #{platform} and name = #{name}")
    Integer isLegal(Bucket bucket);

    @Update("insert into bucket (user_id, platform, name, type) values(#{userId}, #{platform}, #{name}, #{type})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    Integer addBucket(Bucket bucket);

    @Delete("delete from bucket where platform = #{platform} and name = #{name}")
    Integer deleteBucket(Bucket bucket);

    @Select("select type from bucket where name = #{name}")
    Integer getType(String name);

    @Select("select platform from bucket where name = #{name}")
    String getPlatform(String name);

    @Select("select id from bucket where name = #{name}")
    Integer haveName(String name);

    @Select("select id from bucket where user_id = #{userId} and name = #{name}")
    Integer verify(String userId, String name);

    @Update("update bucket set type = #{type} where name = #{bucketName} ")
    Integer setStorageClass(String bucketName, Integer type);
}
