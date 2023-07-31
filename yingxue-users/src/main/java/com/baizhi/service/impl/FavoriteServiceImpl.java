package com.baizhi.service.impl;

import com.baizhi.entity.Favorite;
import com.baizhi.dao.FavoriteDao;
import com.baizhi.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收藏(Favorite)表服务实现类
 *
 * @author makejava
 * @since 2023-07-20 17:00:42
 */
@Service("favoriteService")
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    private FavoriteDao favoriteDao;

    @Override
    public List<Favorite> queryByUid(Integer id, Integer page, Integer rows) {
        int start = (page - 1) * rows;
        return favoriteDao.queryByUid(id, start, rows);
    }

    @Override
    public void deleteByUidAndVid(Integer uid, Integer vid) {
        favoriteDao.deleteByUidAndVid(uid,vid);
    }

    @Override
    public Favorite queryByUidAndVid(Integer uid, Integer vid) {
        return favoriteDao.queryByUidAndVid(uid, vid);
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Favorite queryById(Integer id) {
        return this.favoriteDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param favorite 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Favorite> queryByPage(Favorite favorite, PageRequest pageRequest) {
        long total = this.favoriteDao.count(favorite);
        return new PageImpl<>(this.favoriteDao.queryAllByLimit(favorite, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param favorite 实例对象
     * @return 实例对象
     */
    @Override
    public Favorite insert(Favorite favorite) {
        this.favoriteDao.insert(favorite);
        return favorite;
    }

    /**
     * 修改数据
     *
     * @param favorite 实例对象
     * @return 实例对象
     */
    @Override
    public Favorite update(Favorite favorite) {
        this.favoriteDao.update(favorite);
        return this.queryById(favorite.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.favoriteDao.deleteById(id) > 0;
    }
}
