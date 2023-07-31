package com.baizhi.service;

import com.baizhi.entity.Sms;
import com.baizhi.entity.User;
import com.baizhi.vo.CommentVO;
import com.baizhi.vo.VideoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 用户(User)表服务接口
 *
 * @author makejava
 * @since 2023-07-14 14:20:50
 */
public interface UserService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User queryById(Integer id);

    /**
     * 分页查询
     *
     * @param user 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<User> queryByPage(User user, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User insert(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User update(User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    String login(Sms sms, HttpSession session);

    User updateByUser(User user, HttpServletRequest request);

    List<VideoVO> findById(String token);

    void played(Integer id, String token);

    void liked(Integer vid, String token);

    void deleteLiked(Integer vid, String token);

    void disliked(Integer vid, String token);

    void deleteDisliked(Integer vid, String token);

    void favorites(Integer vid, String token);

    void deleteFavorites(Integer vid, String token);

    List<VideoVO> queryByPlayed(String token, Integer page, Integer rows);

    List<VideoVO> queryByFavorites(String token, Integer page, Integer rows);

    Map<String,Object> queryByComments(Integer vid, Integer page, Integer rows);

    void saveCommont(CommentVO commentVO);
}
