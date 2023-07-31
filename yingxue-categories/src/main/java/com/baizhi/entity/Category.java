package com.baizhi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.io.Serializable;
import java.util.List;

/**
 * 分类(Category)实体类
 *
 * @author makejava
 * @since 2023-07-14 17:51:56
 */
public class Category implements Serializable {
    private static final long serialVersionUID = -54428448076462323L;

    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 父级分类id
     */
    private Integer parentId;

    @JsonProperty("create_at")
    private Date createdAt;

    @JsonProperty("update_at")
    private Date updatedAt;

    @JsonProperty("delete_at")
    private Date deletedAt;

    private List<Category> children;

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

}

