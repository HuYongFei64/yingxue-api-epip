package com.baizhi.controller;

import com.baizhi.feign.UserClient;
import com.baizhi.service.VideoService;
import com.baizhi.vo.CommentVO;
import com.baizhi.vo.VideoDetailVO;
import com.baizhi.vo.VideoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 视频(Video)表控制层
 *
 * @author makejava
 * @since 2023-07-14 21:09:04
 */
@RestController
public class VideoController {
    /**
     * 服务对象
     */
    private VideoService videoService;

    private UserClient userClient;


    @Autowired
    public VideoController(VideoService videoService, UserClient userClient) {
        this.videoService = videoService;
        this.userClient = userClient;
    }

    @PostMapping("/videos/{vid}/comments")
    public void saveComment(@PathVariable("vid") Integer vid,
                            @RequestBody CommentVO commentVO,
                            @RequestParam("token") String token){
        videoService.saveComment(vid,commentVO,token);
    }

    /**
     * 查询评论列表
     * @param vid
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/videos/{vid}/comments")
    public Map<String,Object> queryByComments(@PathVariable("vid") Integer vid,
                                              @RequestParam(value = "page",defaultValue = "1") Integer page,
                                              @RequestParam(value = "per_page",defaultValue = "5")Integer rows){
        return userClient.queryByComments(vid,page,rows);
    }

    /**
     * 首页视频推荐接口
     * @param page
     * @param rows
     * @param token
     * @return
     */
    @GetMapping("/recommends")
    public List<VideoVO> videoRecommendation(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "per_page",defaultValue = "5") Integer rows,
            String token){
        return videoService.videoRecommendation(page,rows,token);
    }


    /**
     * 分类列表查询
     * @param page
     * @param rows
     * @param categoryId
     * @param token
     * @return
     */
    @GetMapping("/videos")
    public List<VideoVO> queryByLimit(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                      @RequestParam(value = "rows",defaultValue = "5")Integer rows,
                                      @RequestParam("category")Integer categoryId,
                                      @RequestParam("token") String token){
        return videoService.queryByLimit(page, rows,categoryId);
    }



    /**
     * 视频详情
     * @param id
     * @return
     */
    @GetMapping("/videos/{id}")
    public VideoDetailVO queryById(@PathVariable("id") Integer id,
                                   @RequestParam(value = "token",required = false) String token){
        return videoService.queryByDetail(id,token);
    }




}

