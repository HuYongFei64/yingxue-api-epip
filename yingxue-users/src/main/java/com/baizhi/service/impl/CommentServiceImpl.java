package com.baizhi.service.impl;

import com.baizhi.entity.Comment;
import com.baizhi.dao.CommentDao;
import com.baizhi.service.CommentService;
import com.baizhi.vo.CommentVO;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-07-20 21:36:01
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentDao commentDao;

    @Override
    public void insertVo(CommentVO commentVO) {
        commentDao.insertVo(commentVO);
    }

    @Override
    public List<Comment> queryByParent(Integer id) {
        return commentDao.queryByParent(id);
    }

    @Override
    public List<Comment> queryByVid(Integer vid,Integer page,Integer rows) {
        page = (page-1)*rows;
        return commentDao.queryByVidAndLimit(vid,page,rows);
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Comment queryById(Integer id) {
        return this.commentDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param comment 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Comment> queryByPage(Comment comment, PageRequest pageRequest) {
        long total = this.commentDao.count(comment);
        return new PageImpl<>(this.commentDao.queryAllByLimit(comment, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public Comment insert(Comment comment) {
        this.commentDao.insert(comment);
        return comment;
    }

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public Comment update(Comment comment) {
        this.commentDao.update(comment);
        return this.queryById(comment.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.commentDao.deleteById(id) > 0;
    }
}
