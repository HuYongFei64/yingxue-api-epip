package com.baizhi.controller;

import com.alibaba.fastjson.JSONObject;
import com.baizhi.annotations.RequiredToken;
import com.baizhi.constants.RedisPrefix;
import com.baizhi.entity.Sms;
import com.baizhi.entity.User;
import com.baizhi.entity.Video;
import com.baizhi.feign.VideosFeign;
import com.baizhi.service.UserService;
import com.baizhi.utils.OSSUtils;
import com.baizhi.vo.VideoVO;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户(User)表控制层
 *
 * @author Fat
 * @since 2023-07-14 14:20:50
 */
@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    /**
     * 服务对象
     */
    private UserService userService;
    private RedisTemplate redisTemplate;
    @Autowired
    private VideosFeign videosFeign;


    @Autowired
    public UserController(UserService userService, RedisTemplate redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/user/favorites")
    public List<VideoVO> queryByFavorites(
            @RequestParam("token") String token,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "per_page",defaultValue = "5") Integer rows) {

        return userService.queryByFavorites(token,page,rows);
    }

    /**
     * 查询用户播放历史
     * @return
     */
    @GetMapping("/user/played")
    public List<VideoVO> queryByPlayed(
            @RequestParam("token") String token,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "per_page",defaultValue = "5") Integer rows) {

        return userService.queryByPlayed(token,page,rows);
    }

    /**
     * 用户取消收藏
     * @param vid
     * @param token
     */
    @DeleteMapping("/user/favorites/{id}")
    public void deleteFavorites(@PathVariable("id") Integer vid,
                                @RequestParam(value = "token",required = false) String token){
        log.info("进入刷新点赞数的接口");
        userService.deleteFavorites(vid,token);
    }

    /**
     * 用户收藏
     * @param vid
     * @param token
     */
    @PutMapping("/user/favorites/{id}")
    public void favorites(@PathVariable("id") Integer vid,
                          @RequestParam(value = "token",required = false) String token){
        log.info("进入刷新点赞数的接口");
        userService.favorites(vid,token);
    }

    /**
     * 取消不喜欢
     * @param vid
     * @param token
     */
    @DeleteMapping("/user/disliked/{id}")
    public void deleteDisliked(@PathVariable("id") Integer vid,
                               @RequestParam(value = "token",required = false) String token){
        log.info("进入刷新点赞数的接口");
        userService.deleteDisliked(vid,token);
    }

    /**
     * 用户不喜欢
     * @param vid
     * @param token
     */
    @PutMapping("/user/disliked/{vid}")
    public void disliked(@PathVariable("vid") Integer vid,
                         @RequestParam(value = "token",required = false) String token){
        log.info("进入刷新点赞数的接口");
        userService.disliked(vid,token);
    }

    /**
     * 用户取消点赞
     * @param vid   视频id
     * @param token 用户token
     */
    @DeleteMapping("/user/liked/{id}")
    public void deleteLiked(@PathVariable("id") Integer vid,
                            @RequestParam(value = "token",required = false) String token){
        log.info("进入刷新点赞数的接口");
        userService.deleteLiked(vid,token);
    }

    /**
     * 用户点赞
     * @param vid   视频id
     * @param token 用户token
     */
    @PutMapping("/user/liked/{id}")
    public void addLiked(@PathVariable("id") Integer vid,
                      @RequestParam(value = "token",required = false) String token){
        log.info("进入刷新点赞数的接口");
        userService.liked(vid,token);
    }


    /**
     * 播放次数
     * @param id    视频id
     * @param token 用户token
     */
    @PutMapping("/user/played/{id}")
    public void played(@PathVariable("id") Integer id,
                       @RequestParam(value = "token",required = false) String token){
        log.info("进入刷新播放次数的接口");
        userService.played(id,token);
    }


    /**
     * 用户上传
     */
    @PostMapping("/user/videos")
    @RequiredToken
    public Video uploadVideo(MultipartFile file, Video video, Integer category_id, HttpServletRequest request) throws IOException {
        log.info("video:{}",JSONObject.toJSONString(video));
        log.info("file:{}",file.getOriginalFilename());

        //1.获取文件名
        String filename = file.getOriginalFilename();

        //2.获取文件后缀
        String ext = FilenameUtils.getExtension(filename);

        //3.生成uuid
        String uuidFileName = UUID.randomUUID().toString().replace("-", "");

        //4.生成uuid文件名名称
        String newFileName = uuidFileName + "." + ext;

        //5.上传阿里云oss 返回文件在oss地址
        String url = OSSUtils.upload(file.getInputStream(), "videos", newFileName);
        log.info("上传成功返回的地址: {}", url);

        //阿里云oss截取视频中某一帧作为封面
        String cover = url + "?x-oss-process=video/snapshot,t_30000,f_jpg,w_0,h_0,m_fast,ar_auto";
        log.info("阿里云oss根据url截取视频封面: {}", cover);

        //6.设置视频信息
        video.setCover(cover);//设置视频封面
        video.setLink(url);//设置视频地址
        video.setCategoryId(category_id);//设置类别id

        //获取用户信息
        User user = (User) request.getAttribute("user");
        video.setUid(user.getId());//设置发布用户id

        //调用视频服务
        Video videoResult = videosFeign.uploadVideo(video);
//        log.info("视频发布成功之后返回的视频信息: {}", JSONWriter.writeValueAsString(videoResult));
        return videoResult;
    }



    /**
     * 登录
     * @param sms
     * @param session
     * @return
     */
    @PostMapping("/tokens")
    public Map<String,String> login(@RequestBody Sms sms, HttpSession session) {
        log.info("sms.phone:{}",sms.getPhone());
        log.info("sms.captcha:{}",sms.getCaptcha());
        HashMap<String,String> map = new HashMap<>();
        String token = userService.login(sms, session);
        map.put("token",token);
        return map;
    }


    /**
     * 查询已登录用户信息
     * @param request
     * @return
     */
    @GetMapping("/user")
    @RequiredToken
    public User user(HttpServletRequest request){
        User user = (User) request.getAttribute("user");
        log.info("user:{}", JSONObject.toJSONString(user));
        return user;
    }

    /**
     * 登出
     * @param token
     */
    @DeleteMapping("/tokens")
    public void logout(@RequestParam("token") String token){
        log.info("token:{}",token);
        redisTemplate.delete(RedisPrefix.TOKEN_KEY+token);
    }

    /**
     * 修改用户信息
     * @param user
     * @param request
     */
    @PatchMapping("/user")
    @RequiredToken
    public User update(@RequestBody User user,HttpServletRequest request){
        User userDB = userService.updateByUser(user, request);
        return userDB;
    }





}

