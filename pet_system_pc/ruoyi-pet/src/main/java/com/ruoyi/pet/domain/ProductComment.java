package com.ruoyi.pet.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ProductComment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long commentId;
    private Long reviewId;
    private Long parentId;
    private Long replyToId;
    private Long userId;
    private Long shopId;
    private Long userType;
    private Long commentType;
    private String content;
    private String images;
    private Integer likeCount;
    private Long status;
    private Long isDeleted;
    private String userName;
    private String shopName;
    private List<ProductComment> replyList;

    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public Long getCommentId() { return commentId; }

    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public Long getReviewId() { return reviewId; }

    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getParentId() { return parentId; }

    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public Long getReplyToId() { return replyToId; }

    public void setUserId(Long userId) { this.userId = userId; }
    public Long getUserId() { return userId; }

    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getShopId() { return shopId; }

    public void setUserType(Long userType) { this.userType = userType; }
    public Long getUserType() { return userType; }

    public void setCommentType(Long commentType) { this.commentType = commentType; }
    public Long getCommentType() { return commentType; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    public void setImages(String images) { this.images = images; }
    public String getImages() { return images; }

    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getLikeCount() { return likeCount; }

    public void setStatus(Long status) { this.status = status; }
    public Long getStatus() { return status; }

    public void setIsDeleted(Long isDeleted) { this.isDeleted = isDeleted; }
    public Long getIsDeleted() { return isDeleted; }

    public void setUserName(String userName) { this.userName = userName; }
    public String getUserName() { return userName; }

    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getShopName() { return shopName; }

    public void setReplyList(List<ProductComment> replyList) { this.replyList = replyList; }
    public List<ProductComment> getReplyList() { return replyList; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("commentId", getCommentId())
            .append("reviewId", getReviewId())
            .append("parentId", getParentId())
            .append("replyToId", getReplyToId())
            .append("userId", getUserId())
            .append("shopId", getShopId())
            .append("userType", getUserType())
            .append("commentType", getCommentType())
            .append("content", getContent())
            .append("images", getImages())
            .append("likeCount", getLikeCount())
            .append("status", getStatus())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}