package com.baizhi.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baizhi.constants.RedisPrefix;
import com.baizhi.entity.*;
import com.baizhi.dao.UserDao;
import com.baizhi.feign.VideosFeign;
import com.baizhi.service.CommentService;
import com.baizhi.service.FavoriteService;
import com.baizhi.service.PlayedService;
import com.baizhi.service.UserService;
import com.baizhi.vo.CommentVO;
import com.baizhi.vo.Reviewer;
import com.baizhi.vo.VideoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户(User)表服务实现类
 *
 * @author makejava
 * @since 2023-07-14 14:20:50
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserDao userDao;
    private RedisTemplate redisTemplate;
    private VideosFeign videosFeign;
    private PlayedService playedService;
    private FavoriteService favoriteService;
    private CommentService commentService;

    @Autowired
    public UserServiceImpl(UserDao userDao, RedisTemplate redisTemplate, VideosFeign videosFeign, PlayedService playedService, FavoriteService favoriteService, CommentService commentService) {
        this.userDao = userDao;
        this.redisTemplate = redisTemplate;
        this.videosFeign = videosFeign;
        this.playedService = playedService;
        this.favoriteService = favoriteService;
        this.commentService = commentService;
    }

    @Override
    public void saveCommont(CommentVO commentVO) {
        commentService.insertVo(commentVO);
    }

    @Override
    public Map<String,Object> queryByComments(Integer vid, Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<>();
        //首先查询该视频下的评论
        List<Comment> comments = commentService.queryByVid(vid,page,rows);
        //将评论转换为VO
        List<CommentVO> commentVOS = comments.stream().map(comment -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment,commentVO);
            //查询用户信息
            User user = userDao.queryById(comment.getUid());
            Reviewer reviewer = new Reviewer();
            BeanUtils.copyProperties(user,reviewer);
            commentVO.setReviewer(reviewer);
            //查询回复信息
            List<Comment> commentChildrenList = commentService.queryByParent(comment.getId());
            //将回复信息转换为VO
            List<CommentVO> commentChildrenVOList = commentChildrenList.stream().map(commentChildren -> {
                CommentVO commentChildrenVO = new CommentVO();
                BeanUtils.copyProperties(commentChildren,commentChildrenVO);
                //查询用户信息
                User userChildren = userDao.queryById(commentChildren.getUid());
                Reviewer reviewerChildren = new Reviewer();
                BeanUtils.copyProperties(userChildren,reviewerChildren);
                commentChildrenVO.setReviewer(reviewerChildren);
                return commentChildrenVO;
            }).collect(Collectors.toList());
            commentVO.setSubComments(commentChildrenVOList);
            return commentVO;
        }).collect(Collectors.toList());
        map.put("total_count",commentVOS.size());
        map.put("items",commentVOS);
        return map;
    }

    /**
     * 收藏列表
     * @param token
     * @param page
     * @param rows
     * @return
     */
    @Override
    public List<VideoVO> queryByFavorites(String token, Integer page, Integer rows) {
        //判断是否登录
        if(StringUtils.isEmpty(token)) throw new RuntimeException("尚未登录！");
        //获取用户登录信息
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        //已登录，查询播放历史
        List<Favorite> favorites = favoriteService.queryByUid(user.getId(),page,rows);
        //取出视频id
        List<Long> vids = favorites.stream().map(favorite -> {
            return Long.parseLong(favorite.getVideoId().toString());
        }).collect(Collectors.toList());
        //调用视频服务进行查询视频信息并返回
        return videosFeign.queryByPlayed(token,vids);
    }

    /**
     * 历史列表
     * @param token
     * @param page
     * @param rows
     * @return
     */
    @Override
    public List<VideoVO> queryByPlayed(String token, Integer page, Integer rows) {
        //判断是否登录
        if(StringUtils.isEmpty(token)) throw new RuntimeException("尚未登录！");
        //获取用户登录信息
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        //已登录，查询播放历史
        List<Played> playeds = playedService.queryByUid(user.getId(),page,rows);
        //取出视频id
        List<Long> vids = playeds.stream().map(played -> {
            return Long.parseLong(played.getVideoId().toString());
        }).collect(Collectors.toList());
        //调用视频服务进行查询视频信息并返回
        return videosFeign.queryByPlayed(token,vids);
    }

    /**
     * 用户取消收藏
     * @param vid
     * @param token
     */
    @Override
    public void deleteFavorites(Integer vid, String token) {
        //判断是否登录
        if(StringUtils.isEmpty(token)) throw new RuntimeException("尚未登录！");
        //获取用户登录信息
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        //已登录，判断是否收藏，收藏则取消收藏
        Favorite favorite = favoriteService.queryByUidAndVid(user.getId(),vid);
        //判断是否已收藏
        if(!ObjectUtils.isEmpty(favorite)){
            //已收藏
            favoriteService.deleteByUidAndVid(user.getId(),vid);
        }

    }

    /**
     * 用户收藏
     * @param vid
     * @param token
     */
    @Override
    public void favorites(Integer vid, String token) {
        //判断是否登录
        if(StringUtils.isEmpty(token)) throw new RuntimeException("尚未登录！");
        //获取用户登录信息
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        //已登录，判断是否收藏，收藏则取消收藏
        Favorite favorite = favoriteService.queryByUidAndVid(user.getId(), vid);
        //判断是否已收藏
        if(ObjectUtils.isEmpty(favorite)){
            //未收藏
            favorite = new Favorite();
            favorite.setUid(user.getId());
            favorite.setVideoId(vid);
            Date date = new Date();
            favorite.setCreatedAt(date);
            favorite.setUpdatedAt(date);
            favoriteService.insert(favorite);
        }

    }

    /**
     * 取消不喜欢
     * @param vid
     * @param token
     */
    @Override
    public void deleteDisliked(Integer vid, String token) {
        //判断是否登录
        if(StringUtils.isEmpty(token)) throw new RuntimeException("尚未登录！");
        //获取用户登录信息
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        //已登录，判断是否不喜欢，不喜欢则取消不喜欢
        if(redisTemplate.opsForSet().isMember(RedisPrefix.IS_DISLIKE_KEY+user.getId(),vid)){
            //不喜欢，从set中删除视频id
            redisTemplate.opsForSet().remove(RedisPrefix.IS_DISLIKE_KEY+user.getId(),vid);
        }
    }

    @Override
    public void disliked(Integer vid, String token) {
        //判断是否登录
        if(StringUtils.isEmpty(token)) throw new RuntimeException("尚未登录！");
        //获取用户登录信息
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        //已登录，判断是否喜欢，喜欢则取消喜欢并减少点赞数
        if(redisTemplate.opsForSet().isMember(RedisPrefix.IS_LIKE_KEY+user.getId(),vid)){
            //已喜欢，从set中删除视频id
            redisTemplate.opsForSet().remove(RedisPrefix.IS_LIKE_KEY+user.getId(),vid);
            //为当前视频减少点赞数
            redisTemplate.opsForValue().decrement(RedisPrefix.LIKE_COUNT_KEY+vid);
        }
        //记录不喜欢
        redisTemplate.opsForSet().add(RedisPrefix.IS_DISLIKE_KEY+user.getId(),vid);

    }

    /**
     * 减少点赞数
     * @param vid
     * @param token
     */
    @Override
    public void deleteLiked(Integer vid, String token) {
        //判断是否登录
        if(StringUtils.isEmpty(token)) throw new RuntimeException("尚未登录！");
        //获取用户登录信息
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        //已登录，从set中删除视频id
        redisTemplate.opsForSet().remove(RedisPrefix.IS_LIKE_KEY+user.getId(),vid);
        //为当前视频减少点赞数
        redisTemplate.opsForValue().decrement(RedisPrefix.LIKE_COUNT_KEY+vid);

    }

    /**
     * 实现点赞的业务方法
     * @param vid
     * @param token
     */
    @Override
    public void liked(Integer vid, String token) {
        //判断是否登录
        if(StringUtils.isEmpty(token)) throw new RuntimeException("尚未登录！");
        //获取用户登录信息
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        //已登录，放入set集合中（可以避免重复）
        redisTemplate.opsForSet().add(RedisPrefix.IS_LIKE_KEY+user.getId(),vid);
        //为当前视频增加点赞数
        redisTemplate.opsForValue().increment(RedisPrefix.LIKE_COUNT_KEY+vid);


    }

    /**
     * 实现播放次数的业务方法
     * @param vid
     * @param token
     */
    @Override
    public void played(Integer vid, String token) {
        if(!StringUtils.isEmpty(token)){
            //已登录，查询是否播放过
            User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
            Played played = playedService.queryByVideoIdAndUserId(vid,user.getId());

            //判断是否有记录
            if (ObjectUtils.isEmpty(played)){
                played = new Played();
                //设置视频id
                played.setVideoId(vid);
                //设置用户id
                played.setUid(user.getId());
                //设置创建时间
                played.setCreatedAt(new Date());
                //设置修改时间
                played.setUpdatedAt(new Date());
                //保存到数据库
                playedService.insert(played);
            }else{
                //有记录，修改时间
                played.setUpdatedAt(new Date());
                //更新到数据库
                playedService.update(played);
            }

        }

        //最后无论登录还是未登录都添加一次播放次数
        redisTemplate.opsForValue().increment(RedisPrefix.PLAYED_KEY + vid);

    }

    @Override
    @Transactional
    public List<VideoVO> findById(String token) {
        // 1.获取用户id
        User user = (User) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY + token);
        log.info("user:{}" , user.getId());
        Integer id = user.getId();
        // 2.调用远程接口查询视频信息
        return videosFeign.videosVo(id,token);
    }

    @Override
    @Transactional
    public User updateByUser(User user, HttpServletRequest request) {
        // 1.获取token
        String token = request.getParameter("token");
        // 2.从session中获取user和token
        User userOld = (User) request.getAttribute("user");
        String tokenOld = (String) request.getAttribute("token");
        // 3.判断token是否一致
        if(tokenOld == null || tokenOld == "" || !tokenOld.equals(token)){
            throw new RuntimeException("登录异常！");
        }
        // 4.判断手机号是否需要修改
        if (!StringUtils.isEmpty(user.getPhone())){
            if (!redisTemplate.hasKey(RedisPrefix.CODE_KEY+user.getPhone())) throw new RuntimeException("验证码已过期！");
            //获取验证码
            String code = (String) redisTemplate.opsForValue().get(RedisPrefix.CODE_KEY + user.getPhone());
            //判断验证码是否正确
            if (!StringUtils.equals(code,user.getCaptcha())) throw new RuntimeException("验证码错误！");
            userOld.setPhone(user.getPhone());
        }
        // 5.判断用户名称是否需要修改
        if (!StringUtils.isEmpty(user.getName())) userOld.setName(user.getName());
        // 6.判断简介是否需要修改
        if (!StringUtils.isEmpty(user.getIntro())) userOld.setIntro(user.getIntro());

        // 7.修改用户信息
        userDao.update(userOld);

        // 8.更新redis中的用户信息
        redisTemplate.opsForValue().set(RedisPrefix.TOKEN_KEY+token,userOld,30, TimeUnit.MINUTES);

        // 9.更新request中的用户信息
        request.setAttribute("user",userOld);
        request.setAttribute("token",token);

        return userOld;
    }

    @Override
    public String login(Sms sms, HttpSession session) {
        // 1.获取手机号
        String phone = sms.getPhone();
        // 2.获取验证码
        String code = sms.getCaptcha();
        // 3.从redis中获取验证码
        String redisCode = (String) redisTemplate.opsForValue().get(RedisPrefix.CODE_KEY+ phone);
        // 4.判断验证码是否正确
        if(code == null || code == "" || !code.equals(redisCode)){
            throw new RuntimeException("验证码错误");
        }
        // 5.判断用户是否存在
        User user = userDao.findByPhone(phone);
        //获取token
        String token = session.getId();
        if(user == null){
            //用户不存在
            user = new User();
            user.setPhone(phone);               //手机号
            user.setName(phone);                //昵称
            user.setCreatedAt(new Date());      //创建时间
            user.setUpdatedAt(new Date());      //修改时间
            user.setIntro("");                  //简介
            user.setAvatar("https://img1.imgtp.com/2023/07/14/fgHNPJ2P.jpg");     //头像
            user.setPhoneLinked(1);
            user.setWechatLinked(0);
            user.setFollowersCount(0);
            user.setFollowingCount(0);
            //保存用户
            userDao.insert(user);
            redisTemplate.opsForValue().set(RedisPrefix.TOKEN_KEY+token,user,30, TimeUnit.MINUTES);
        }else{
            //用户存在
            redisTemplate.opsForValue().set(RedisPrefix.TOKEN_KEY+token,user,30, TimeUnit.MINUTES);
        }

        return token;
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public User queryById(Integer id) {
        return this.userDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param user 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<User> queryByPage(User user, PageRequest pageRequest) {
        long total = this.userDao.count(user);
        return new PageImpl<>(this.userDao.queryAllByLimit(user, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User update(User user) {
        this.userDao.update(user);
        return this.queryById(user.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.userDao.deleteById(id) > 0;
    }
}
