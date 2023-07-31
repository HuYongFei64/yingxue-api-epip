package com.baizhi.feign;

import com.baizhi.entity.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("API-CATEGORIES")
public interface CategoriesClient {

    @GetMapping("/categories/{id}")
    Category queryById(@PathVariable Integer id,@RequestParam("token") String token);

}
