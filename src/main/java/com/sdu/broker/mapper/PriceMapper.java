package com.sdu.broker.mapper;

import com.sdu.broker.pojo.Price;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PriceMapper {
    @Select("select price from price where platform = #{platform} and type = #{type}")
    Double getPrice(Price price);
}
