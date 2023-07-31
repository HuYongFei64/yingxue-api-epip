package com.baizhi.dao;

import com.baizhi.entity.Played;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * 播放历史(Played)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-19 16:15:23
 */
@Mapper
public interface PlayedDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Played queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param played 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<Played> queryAllByLimit(Played played, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param played 查询条件
     * @return 总行数
     */
    long count(Played played);

    /**
     * 新增数据
     *
     * @param played 实例对象
     * @return 影响行数
     */
    int insert(Played played);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Played> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Played> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Played> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Played> entities);

    /**
     * 修改数据
     *
     * @param played 实例对象
     * @return 影响行数
     */
    int update(Played played);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    Played queryByVideoIdAndUserId(Integer vid, Integer uid);

    List<Played> queryByUid(Integer id, Integer page, Integer rows);
}

