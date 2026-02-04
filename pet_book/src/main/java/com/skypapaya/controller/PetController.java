package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.entity.Pet;
import com.skypapaya.mapper.PetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet")
@CrossOrigin
public class PetController {

    @Autowired
    private PetMapper petMapper;

    /**
     * 添加宠物（与设计文档对齐：POST /api/pet 或 /api/pet/add）
     */
    @PostMapping("/add")
    public Result<Long> insertPet(@RequestBody Pet pet) {
        pet.setUserId(1L); // TODO: 从 Token 获取
        pet.setStatus("0");
        petMapper.insertPet(pet);
        return Result.success(pet.getId());
    }
}