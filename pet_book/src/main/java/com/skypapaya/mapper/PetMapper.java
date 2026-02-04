package com.skypapaya.mapper;

import com.skypapaya.entity.Pet;
import com.skypapaya.vo.PetVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetMapper {
    List<PetVO> selectByUserId(@Param("userId") Long userId);
    Pet insertPet(@Param("pet") Pet pet);

}