package com.baizhi.controller;

import com.baizhi.entity.Favorite;
import com.baizhi.entity.User;
import com.baizhi.service.FavoriteService;
import com.baizhi.service.UserService;
import com.baizhi.vo.CommentVO;
import com.baizhi.vo.VideoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserServiceController {

    private UserService userService;
    private FavoriteService favoriteService;

    @Autowired
    public UserServiceController(UserService userService, FavoriteService favoriteService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    /**
     * 对外提供的接口
     */
    @GetMapping("/users/{id}")
    public User queryById(@PathVariable("id") Integer id){
        User user = userService.queryById(id);
        return user;
    }

    /**
     * 对外提供的接口
     */
    @GetMapping("/user/videos")
    public List<VideoVO> queryByVideosVo(String token){
        return userService.findById(token);
    }

    /**
     * 对外提供的接口
     */
    @GetMapping("/user/favorite/{uid}/{vid}")
    public Favorite queryByFavorite(@PathVariable("uid") Integer uid,@PathVariable("vid") Integer vid){
        return favoriteService.queryByUidAndVid(uid,vid);
    }

    @GetMapping("/user/video/commons")
    Map<String,Object> queryByComments(@RequestParam("vid") Integer vid,
                                       @RequestParam("page") Integer page,
                                       @RequestParam("rows") Integer rows){
        return userService.queryByComments(vid,page,rows);
    }

    @PostMapping("/user/videos/comments")
    void saveCommont(@RequestBody CommentVO commentVO){
        userService.saveCommont(commentVO);
    }

}
