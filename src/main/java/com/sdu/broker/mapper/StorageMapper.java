package com.sdu.broker.mapper;

import com.sdu.broker.pojo.StorageType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StorageMapper {
    @Select("select platform, type, scene, min_metering_size, min_storage_time, data_access_features, image_processing, data_retrieval_fee, " +
            "oss_acceleration, lrs_price, zrs_price from storage_type where platform = 'ALI'")
    @Results({
            @Result(property = "minMeteringSize", column = "min_metering_size"),
            @Result(property = "minStorageTime", column = "min_storage_time"),
            @Result(property = "dataAccessFeatures", column = "data_access_features"),
            @Result(property = "imageProcessing", column = "image_processing"),
            @Result(property = "dataRetrievalFee", column = "data_retrieval_fee"),
            @Result(property = "ossAcceleration", column = "oss_acceleration"),
            @Result(property = "lrsPrice", column = "lrs_price"),
            @Result(property = "zrsPrice", column = "zrs_price")
    })
    List<StorageType> getAli();

    @Select("select platform, type, scene, introduction, min_storage_time, design_persistence_single, design_availability_single, " +
            "design_persistence_multi, design_availability_multi, response_time, huawei_price from storage_type where platform = 'HUAWEI'")
    @Results({
            @Result(property = "minStorageTime", column = "min_storage_time"),
            @Result(property = "designPersistenceSingle", column = "design_persistence_single"),
            @Result(property = "designAvailabilitySingle", column = "design_availability_single"),
            @Result(property = "designPersistenceMulti", column = "design_persistence_multi"),
            @Result(property = "designAvailabilityMulti", column = "design_availability_multi"),
            @Result(property = "responseTime", column = "response_time"),
            @Result(property = "huaweiPrice", column = "huawei_price")
    })
    List<StorageType> getHuawei();
}
