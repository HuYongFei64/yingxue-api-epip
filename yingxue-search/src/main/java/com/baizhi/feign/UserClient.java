package com.baizhi.feign;

import com.baizhi.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("API-USERS")
public interface UserClient {

    @GetMapping("/users/{id}")
    User queryById(@PathVariable("id") Integer id, @RequestParam("token") String token);
}
