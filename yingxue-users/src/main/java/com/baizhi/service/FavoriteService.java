package com.baizhi.service;

import com.baizhi.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 收藏(Favorite)表服务接口
 *
 * @author makejava
 * @since 2023-07-20 17:00:42
 */
public interface FavoriteService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Favorite queryById(Integer id);

    /**
     * 分页查询
     *
     * @param favorite 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<Favorite> queryByPage(Favorite favorite, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param favorite 实例对象
     * @return 实例对象
     */
    Favorite insert(Favorite favorite);

    /**
     * 修改数据
     *
     * @param favorite 实例对象
     * @return 实例对象
     */
    Favorite update(Favorite favorite);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    Favorite queryByUidAndVid(Integer uid, Integer vid);

    void deleteByUidAndVid(Integer uid, Integer vid);

    List<Favorite> queryByUid(Integer id, Integer page, Integer rows);
}
