package com.baizhi.dao;

import com.baizhi.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 收藏(Favorite)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-20 17:00:42
 */
@Mapper
public interface FavoriteDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Favorite queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param favorite 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<Favorite> queryAllByLimit(Favorite favorite, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param favorite 查询条件
     * @return 总行数
     */
    long count(Favorite favorite);

    /**
     * 新增数据
     *
     * @param favorite 实例对象
     * @return 影响行数
     */
    int insert(Favorite favorite);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Favorite> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Favorite> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Favorite> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Favorite> entities);

    /**
     * 修改数据
     *
     * @param favorite 实例对象
     * @return 影响行数
     */
    int update(Favorite favorite);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    Favorite queryByUidAndVid(Integer uid, Integer vid);

    void deleteByUidAndVid(Integer uid, Integer vid);

    List<Favorite> queryByUid(Integer id, int start, Integer rows);
}

