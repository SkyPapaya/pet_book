package com.skypapaya.controller;

import com.skypapaya.entity.Pet;
import com.skypapaya.mapper.PetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet")
@CrossOrigin // 允许前端跨域访问
public class PetController {

    @Autowired
    private PetMapper petMapper; // 报错没关系，下一步创建它

    // 添加宠物接口
    @PostMapping("/add")
    public String addPet(@RequestBody Pet pet) {
        // 暂时模拟一个用户ID（比如你登录了，ID是1）
        pet.setUserId(1L);
        pet.setStatus(0); // 默认在家

        petMapper.insert(pet);
        return "添加宠物成功！宠物ID: " + pet.getId();
    }
}