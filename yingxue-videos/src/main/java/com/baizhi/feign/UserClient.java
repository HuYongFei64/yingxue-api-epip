package com.baizhi.feign;

import com.baizhi.entity.Favorite;
import com.baizhi.entity.User;
import com.baizhi.vo.CommentVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("API-USERS")
public interface UserClient {

    @GetMapping("/users/{id}")
    User queryById(@PathVariable("id") Integer id);

    @GetMapping("/user/favorite/{uid}/{vid}")
    Favorite queryByFavorite(@PathVariable("uid") Integer uid, @PathVariable("vid") Integer vid);

    @GetMapping("/user/video/commons")
    Map<String,Object> queryByComments(@RequestParam("vid") Integer vid,
                                       @RequestParam("page") Integer page,
                                       @RequestParam("rows") Integer rows);

    @PostMapping("/user/videos/comments")
    void saveCommont(@RequestBody CommentVO commentVO);
}
