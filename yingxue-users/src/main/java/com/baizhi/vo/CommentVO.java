package com.baizhi.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class CommentVO {

    private Integer id;

    private String content;

    @JsonProperty("video_id")
    private Integer videoId;

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    private Reviewer reviewer;

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("sub_comments")
    private List<CommentVO> subComments;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<CommentVO> getSubComments() {
        return subComments;
    }

    public void setSubComments(List<CommentVO> subComments) {
        this.subComments = subComments;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Reviewer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }
}
