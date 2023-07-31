package com.baizhi.controller;

import com.baizhi.annotations.RequiredToken;
import com.baizhi.entity.Category;
import com.baizhi.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类(Category)表控制层
 *
 * @author makejava
 * @since 2023-07-14 17:51:56
 */
@RestController
public class CategoryController {
    /**
     * 服务对象
     */
    private CategoryService categoryService;


    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 分类列表
     * @return
     */
    @GetMapping("/categories")
    public List<Category> queryAll(){
        return categoryService.queryAll();
    }

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
//    @RequiredToken
    @GetMapping("/categories/{id}")
    public Category queryById(@PathVariable Integer id){
        return categoryService.queryById(id);
    }



}

