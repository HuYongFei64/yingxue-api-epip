package com.baizhi.dao;

import com.baizhi.entity.Comment;
import com.baizhi.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 评论(Comment)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-20 21:35:54
 */
@Mapper
public interface CommentDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param comment 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<Comment> queryAllByLimit(Comment comment, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param comment 查询条件
     * @return 总行数
     */
    long count(Comment comment);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 影响行数
     */
    int insert(Comment comment);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Comment> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Comment> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Comment> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Comment> entities);

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 影响行数
     */
    int update(Comment comment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    List<Comment> queryByVidAndLimit(Integer vid, Integer page, Integer rows);

    List<Comment> queryByParent(Integer id);

    void insertVo(CommentVO commentVO);
}

