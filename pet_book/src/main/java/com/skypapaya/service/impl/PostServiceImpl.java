package com.skypapaya.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypapaya.mapper.PostMapper;
import com.skypapaya.service.PostService;
import com.skypapaya.vo.PostCardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    // JSON 工具类，用于解析数据库里的图片数组字符串
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<PostCardVO> getFeedList(Integer category, Integer page, Integer pageSize) {
        // 1. 计算数据库的分页偏移量 (Offset)
        // 第1页: (1-1)*10 = 0
        // 第2页: (2-1)*10 = 10
        int offset = (page - 1) * pageSize;

        // 2. 查询数据库,用一个list来暂存数据
        List<PostCardVO> list = postMapper.selectFeedList(category, offset, pageSize);

        // 3. 处理数据：把 JSON 字符串转成单张图片 URL
        for (PostCardVO vo : list) {
            String imagesJson = vo.getCoverUrl(); // 此时这里面还是 ["url1", "url2"]
            String firstImage = extractFirstImage(imagesJson);
            vo.setCoverUrl(firstImage); // 替换成真正的图片链接
        }

        return list;
    }

    /**
     * 辅助方法：从 JSON 数组字符串中提取第一张图片
     */
    private String extractFirstImage(String json) {
        if (json == null || json.isEmpty()) {
            return null; // 或者返回一个默认的占位图 URL
        }
        try {
            // 将 JSON 字符串解析为 List<String>
            List<String> images = objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
            if (images != null && !images.isEmpty()) {
                return images.get(0); // 取第一张
            }
        } catch (Exception e) {
            // 解析失败（比如数据脏了），打印日志或忽略
            e.printStackTrace();
        }
        return null;
    }
}