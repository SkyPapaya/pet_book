package com.skypapaya.mapper;

import com.skypapaya.entity.Pet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
// 1. 删掉 extends BaseMapper<Pet>
public interface PetMapper {

    // 2. 手动写 SQL
    @Insert("INSERT INTO t_pet(user_id, nickname, type, gender, status, create_time) " +
            "VALUES(#{userId}, #{nickname}, #{type}, #{gender}, #{status}, #{createTime})")
    // 3. 关键配置：让数据库自动生成的主键 ID 回填到 Pet 对象中
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Pet pet);

}