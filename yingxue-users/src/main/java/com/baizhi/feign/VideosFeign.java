package com.baizhi.feign;

import com.baizhi.entity.Video;
import com.baizhi.vo.VideoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("API-VIDEOS")
public interface VideosFeign {

    /**
     * 调用videos模块
     * 实现上传视频的功能
     * @param video
     * @return
     */
    @PostMapping("/publish")
    Video uploadVideo(@RequestBody Video video);

    @GetMapping("/videos/search/test")
    List<VideoVO> videosVo(@RequestParam("id") Integer id,@RequestParam("token") String token);

    @GetMapping("/videos/played")
    List<VideoVO> queryByPlayed(@RequestParam("token") String token, @RequestParam("ids") List<Long> ids);

}
