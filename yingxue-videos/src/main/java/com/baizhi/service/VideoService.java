package com.baizhi.service;

import com.baizhi.entity.Video;
import com.baizhi.vo.CommentVO;
import com.baizhi.vo.VideoDetailVO;
import com.baizhi.vo.VideoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 视频(Video)表服务接口
 *
 * @author makejava
 * @since 2023-07-14 21:09:04
 */
public interface VideoService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Video queryById(Integer id);

    /**
     * 分页查询
     *
     * @param video 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<Video> queryByPage(Video video, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param video 实例对象
     * @return 实例对象
     */
    Video insert(Video video);

    /**
     * 修改数据
     *
     * @param video 实例对象
     * @return 实例对象
     */
    Video update(Video video);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    List<VideoVO> videoRecommendation(Integer page, Integer rows,String token);

    List<Video> queryByName(String q,Integer page,Integer rows);

    List<VideoVO> queryByUserId(Integer id);

    List<VideoVO> queryByLimit(Integer page, Integer rows, Integer categoryId);

    VideoDetailVO queryByDetail(Integer id, String token);

    List<VideoVO> queryByPlayed(String token, List<Long> ids);

    void saveComment(Integer vid, CommentVO commentVO, String token);
}
