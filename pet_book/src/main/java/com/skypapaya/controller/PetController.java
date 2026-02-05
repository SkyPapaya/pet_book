package com.skypapaya.controller;

import com.skypapaya.common.Result;
import com.skypapaya.entity.Pet;
import com.skypapaya.mapper.PetMapper;
import com.skypapaya.security.CurrentUserHolder;
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
    public Result<Long> addPet(@RequestBody Pet pet) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        pet.setUserId(currentUserId);
        pet.setStatus("0");
        petMapper.insertPet(pet);
        return Result.success(pet.getId());
    }

    /**
     * 更新宠物信息 PUT /api/pet/{id}，仅允许本人修改
     */
    @PutMapping("/{id}")
    public Result<Void> updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        Long currentUserId = CurrentUserHolder.get();
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }
        Pet existing = petMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "宠物不存在");
        }
        if (!currentUserId.equals(existing.getUserId())) {
            return Result.error(403, "无权修改该宠物");
        }
        pet.setId(id);
        pet.setUserId(currentUserId);
        petMapper.updatePet(pet);
        return Result.success(null);
    }
}