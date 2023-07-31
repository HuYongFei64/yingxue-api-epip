package com.baizhi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baizhi.constants.RedisPrefix;
import com.baizhi.entity.Favorite;
import com.baizhi.entity.User;
import com.baizhi.entity.Video;
import com.baizhi.dao.VideoDao;
import com.baizhi.feign.CategoriesClient;
import com.baizhi.feign.UserClient;
import com.baizhi.service.VideoService;
import com.baizhi.vo.CommentVO;
import com.baizhi.vo.Reviewer;
import com.baizhi.vo.VideoDetailVO;
import com.baizhi.vo.VideoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 视频(Video)表服务实现类
 *
 * @author makejava
 * @since 2023-07-14 21:09:04
 */
@Service("videoService")
public class VideoServiceImpl implements VideoService {

    private static final Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);
    private VideoDao videoDao;
    private RabbitTemplate rabbitTemplate;
    private RedisTemplate redisTemplate;
    private StringRedisTemplate stringRedisTemplate;

    private CategoriesClient categoriesClient;
    private UserClient userClient;

    @Autowired
    public VideoServiceImpl(VideoDao videoDao, RabbitTemplate rabbitTemplate, RedisTemplate redisTemplate, StringRedisTemplate stringRedisTemplate, CategoriesClient categoriesClient, UserClient userClient) {
        this.videoDao = videoDao;
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.categoriesClient = categoriesClient;
        this.userClient = userClient;
    }

    @Override
    public void saveComment(Integer vid, CommentVO commentVO, String token) {
        //根据token获取用户
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        Reviewer reviewer = new Reviewer();
        BeanUtils.copyProperties(user, reviewer);
        //封装评论对象
        commentVO.setReviewer(reviewer);
        commentVO.setVideoId(vid);
        commentVO.setCreatedAt(new Date());
        commentVO.setUpdatedAt(new Date());
        //发送消息
        userClient.saveCommont(commentVO);
    }

    @Override
    public List<VideoVO> queryByPlayed(String token, List<Long> ids) {
        //根据ids查询
        log.info("ids:{}", ids);
        if (ids.size() == 0) {
            return null;
        }
        List<Video> videos = videoDao.queryByIds(ids);
        //使用stream流，为每一个视频同时查询分类名称和用户信息
        List<VideoVO> videoVOS = videos.stream().map(video -> {
            VideoVO videoVO = new VideoVO();
            //复制属性
            BeanUtils.copyProperties(video, videoVO);
            //获取分类名称
            String categoryName = categoriesClient.queryById(video.getCategoryId()).getName();
            videoVO.setCategory(categoryName);
            //获取用户信息
            User user = userClient.queryById(video.getUid());
            videoVO.setUploader(user.getName());
            //获取点赞数
            int likeCount = (int) redisTemplate.opsForValue().get(RedisPrefix.LIKE_COUNT_KEY + video.getId());
            videoVO.setLikes(likeCount);
            return videoVO;
        }).collect(Collectors.toList());
        return videoVOS;
    }

    /**
     * 查询视频详情业务方法
     *
     * @param id
     * @param token
     * @return
     */
    @Override
    public VideoDetailVO queryByDetail(Integer id, String token) {

        //获取视频信息对象
        Video video = videoDao.queryById(id);

        //获取视频详情对象
        VideoDetailVO videoDetailVO = new VideoDetailVO();

        //复制属性
        BeanUtils.copyProperties(video, videoDetailVO);

        //获取分类名称
        String categoryName = categoriesClient.queryById(video.getCategoryId()).getName();
        videoDetailVO.setCategory(categoryName);

        //获取视频用户信息
        User user = userClient.queryById(video.getUid());
        videoDetailVO.setUploader(user);

        //设置播放次数
        videoDetailVO.setPlaysCount(0);
        Integer count = (Integer) redisTemplate.opsForValue().get(RedisPrefix.PLAYED_KEY + video.getId());
        if(count != null){
            videoDetailVO.setPlaysCount(count);
        }

        //获取当前用户
        User currentUser = null;
        if(!StringUtils.isEmpty(token)){
            currentUser = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
            log.info("currentUser:{}",currentUser);
        }
        //判断用户是否登录
        if(!ObjectUtils.isEmpty(currentUser)){
            //判断是否点赞，如果点赞则不喜欢为false
            if(redisTemplate.opsForSet().isMember(RedisPrefix.IS_LIKE_KEY+currentUser.getId(),video.getId())){
                //设置已点赞
                videoDetailVO.setLiked(true);
                //设置是否不喜欢
                videoDetailVO.setDisliked(false);
            }else if(redisTemplate.opsForSet().isMember(RedisPrefix.IS_DISLIKE_KEY+currentUser.getId(),video.getId())){
                //设置已不喜欢
                videoDetailVO.setDisliked(true);
                //设置是否点赞
                videoDetailVO.setLiked(false);
            }
            Favorite favorite = userClient.queryByFavorite(currentUser.getId(), video.getId());
            //设置是否收藏
            videoDetailVO.setFavorite(!ObjectUtils.isEmpty(favorite));
        }
        //获取点赞数
        Long size = Long.parseLong(redisTemplate.opsForValue().get(RedisPrefix.LIKE_COUNT_KEY + videoDetailVO.getId()).toString());
        //打印点赞数
        log.info("点赞数：{}",size);
        //设置点赞数
        if(size>0){
            videoDetailVO.setLikesCount(size.intValue());
        }else {
            videoDetailVO.setLikesCount(0);
        }

        return videoDetailVO;
    }

    @Override
    public List<VideoVO> queryByLimit(Integer page, Integer rows, Integer categoryId) {
        int start = (page - 1) * rows;
        List<Video> videoList = videoDao.queryByCategoryId(start,rows,categoryId);

        List<VideoVO> videoVOList = videoList.stream().map(video -> {
            VideoVO videoVO = new VideoVO();

            BeanUtils.copyProperties(video, videoVO);

            //获取分类名称
            String categoryName = categoriesClient.queryById(video.getCategoryId()).getName();
            videoVO.setCategory(categoryName);

            //获取点赞数
            if(!redisTemplate.hasKey(RedisPrefix.LIKE_KEY+video.getId())) {
                videoVO.setLikes(0);
            }else {
                videoVO.setLikes((Integer) redisTemplate.opsForValue().get(RedisPrefix.LIKE_KEY+video.getId()));
            }

            return videoVO;

        }).collect(Collectors.toList());

        return videoVOList;
    }

    @Override
    public List<VideoVO> queryByUserId(Integer id) {
        List<Video> videoVOList = videoDao.queryByUserId(id);
        log.info("videoVOList:{}",videoVOList);

        List<VideoVO> videoVOS = videoVOList.stream().map(video -> {
            VideoVO videoVO = new VideoVO();
            BeanUtils.copyProperties(video, videoVO);

            log.info("categoryId:{}",video.getCategoryId());
            //获取分类名称
            String categoryName = categoriesClient.queryById(video.getCategoryId()).getName();
            videoVO.setCategory(categoryName);

            //获取点赞数
            if(!redisTemplate.hasKey(RedisPrefix.LIKE_KEY+video.getId())){
                videoVO.setLikes(0);
            }else{
                videoVO.setLikes((Integer) redisTemplate.opsForValue().get(RedisPrefix.LIKE_KEY+video.getId()));
            }

            return videoVO;
        }).collect(Collectors.toList());
        return videoVOS;
    }

    @Override
    public List<Video> queryByName(String q,Integer page,Integer rows) {
        int start = (page - 1) * rows;
        return videoDao.queryByName(q,start,rows);
    }

    @Override
    public List<VideoVO> videoRecommendation(Integer page, Integer rows,String token) {
        int start = (page - 1) * rows;
        List<Video> videoList = videoDao.queryVideoByPage(start, rows);

        List<VideoVO> videoVOList = videoList.stream().map(video->{
            VideoVO videoVO = new VideoVO();
            BeanUtils.copyProperties(video, videoVO);

            //获取分类名称
            String categoryName = categoriesClient.queryById(video.getCategoryId()).getName();
            videoVO.setCategory(categoryName);

            //获取用户名称
            String userName = userClient.queryById(video.getUid()).getName();
            videoVO.setUploader(userName);

            //获取点赞数
            if(!redisTemplate.hasKey(RedisPrefix.LIKE_KEY+video.getId())) {
                videoVO.setLikes(0);
            }else{
                videoVO.setLikes((Integer) redisTemplate.opsForValue().get(RedisPrefix.LIKE_KEY+video.getId()));
            }

            return videoVO;
        }).collect(Collectors.toList());

        return videoVOList;

    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Video queryById(Integer id) {
        return this.videoDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param video 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Video> queryByPage(Video video, PageRequest pageRequest) {
        long total = this.videoDao.count(video);
        return new PageImpl<>(this.videoDao.queryAllByLimit(video, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param video 实例对象
     * @return 实例对象
     */
    @Override
    public Video insert(Video video) {
        video.setCreatedAt(new Date());
        video.setUpdatedAt(new Date());

        this.videoDao.insert(video);

        // 发送消息
        this.rabbitTemplate.convertAndSend("video", "", JSONObject.toJSONString(video));

        return video;
    }

    /**
     * 修改数据
     *
     * @param video 实例对象
     * @return 实例对象
     */
    @Override
    public Video update(Video video) {
        this.videoDao.update(video);
        return this.queryById(video.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.videoDao.deleteById(id) > 0;
    }
}
