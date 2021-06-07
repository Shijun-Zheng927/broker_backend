package com.sdu.broker.mapper;

import com.sdu.broker.pojo.History;
import com.sdu.broker.pojo.Introduce;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface IntroduceMapper {
    @Update("insert into introduce (name, file) " +
            "values(#{name}, #{file})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    Integer addIntroduce(Introduce introduce);

    @Select("select file from introduce where #{name} = name")
    String getPath(String name);
}
