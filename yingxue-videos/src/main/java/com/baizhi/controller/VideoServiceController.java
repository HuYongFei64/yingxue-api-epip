package com.baizhi.controller;

import com.baizhi.entity.Video;
import com.baizhi.service.VideoService;
import com.baizhi.vo.VideoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;

/**
 * 视频(Video)表控制层
 *
 * @author makejava
 * @since 2023-07-14 21:09:04
 */
@RestController
public class VideoServiceController {
    /**
     * 服务对象
     */
    private VideoService videoService;


    @Autowired
    public VideoServiceController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * 对其他服务暴露的接口
     * @param video
     * @return
     */
    @PostMapping("/publish")
    public Video uploadVideo(@RequestBody Video video){
        videoService.insert(video);
        return videoService.queryById(video.getId());

    }

    /**
     * 对外暴露的接口，根据名称模糊查询
     */
    @GetMapping("/search")
    public List<Video> search(@RequestParam("q") String q,
                              @RequestParam(value = "page",defaultValue = "1") Integer page,
                              @RequestParam(value = "per_page",defaultValue = "5") Integer rows){
        return videoService.queryByName(q,page,rows);
    }

    @GetMapping("/videos/search/test")
    public List<VideoVO> videosVo(@RequestParam("id") Integer id,@RequestParam("token") String token){
        return videoService.queryByUserId(id);
    }

    /**
     * 对其他服务暴露的接口
     * @param video
     * @return
     */
    @PostMapping("/user/upload")
    public Video uploadVideoByFat(@RequestBody Video video){
        return videoService.insert(video);
    }

    @GetMapping("/videos/played")
    public List<VideoVO> queryByPlayed(
            @RequestParam("token") String token,
            @RequestParam("ids") List<Long> ids) {

        return videoService.queryByPlayed(token,ids);
    }

}

