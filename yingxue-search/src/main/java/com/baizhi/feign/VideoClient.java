package com.baizhi.feign;

import com.baizhi.entity.Video;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("API-VIDEOS")
public interface VideoClient {

    @GetMapping("/search")
    List<Video> search(@RequestParam("q") String q,
                       @RequestParam("page") Integer page,
                       @RequestParam("per_page") Integer rows);

}
