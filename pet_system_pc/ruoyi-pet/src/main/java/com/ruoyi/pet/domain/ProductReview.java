package com.ruoyi.pet.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

public class ProductReview extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long reviewId;

    @Excel(name = "订单id")
    private Long orderId;

    @Excel(name = "订单项id")
    private Long orderItemId;

    @Excel(name = "商品id")
    private Long resourceId;

    @Excel(name = "用户id")
    private Long userId;

    @Excel(name = "规格名称")
    private String specName;

    @Excel(name = "评分")
    private Long rating;

    @Excel(name = "评价内容")
    private String content;

    @Excel(name = "评价图片")
    private String images;

    @Excel(name = "是否匿名")
    private Long isAnonymous;

    @Excel(name = "商家回复")
    private String replyContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date replyTime;

    @Excel(name = "点赞数")
    private Long likeCount;

    @Excel(name = "状态")
    private Long status;

    private Long isDeleted;

    private String userName;

    private String userAvatar;

    private List<ProductComment> commentList;

    public void setReviewId(Long reviewId)
    {
        this.reviewId = reviewId;
    }

    public Long getReviewId()
    {
        return reviewId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderItemId(Long orderItemId)
    {
        this.orderItemId = orderItemId;
    }

    public Long getOrderItemId()
    {
        return orderItemId;
    }

    public void setResourceId(Long resourceId)
    {
        this.resourceId = resourceId;
    }

    public Long getResourceId()
    {
        return resourceId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setSpecName(String specName)
    {
        this.specName = specName;
    }

    public String getSpecName()
    {
        return specName;
    }

    public void setRating(Long rating)
    {
        this.rating = rating;
    }

    public Long getRating()
    {
        return rating;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getImages()
    {
        return images;
    }

    public void setIsAnonymous(Long isAnonymous)
    {
        this.isAnonymous = isAnonymous;
    }

    public Long getIsAnonymous()
    {
        return isAnonymous;
    }

    public void setReplyContent(String replyContent)
    {
        this.replyContent = replyContent;
    }

    public String getReplyContent()
    {
        return replyContent;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    public void setLikeCount(Long likeCount)
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount()
    {
        return likeCount;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    public void setIsDeleted(Long isDeleted)
    {
        this.isDeleted = isDeleted;
    }

    public Long getIsDeleted()
    {
        return isDeleted;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserAvatar(String userAvatar)
    {
        this.userAvatar = userAvatar;
    }

    public String getUserAvatar()
    {
        return userAvatar;
    }

    public void setCommentList(List<ProductComment> commentList)
    {
        this.commentList = commentList;
    }

    public List<ProductComment> getCommentList()
    {
        return commentList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reviewId", getReviewId())
            .append("orderId", getOrderId())
            .append("orderItemId", getOrderItemId())
            .append("resourceId", getResourceId())
            .append("userId", getUserId())
            .append("specName", getSpecName())
            .append("rating", getRating())
            .append("content", getContent())
            .append("images", getImages())
            .append("isAnonymous", getIsAnonymous())
            .append("replyContent", getReplyContent())
            .append("replyTime", getReplyTime())
            .append("likeCount", getLikeCount())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("isDeleted", getIsDeleted())
            .append("userName", getUserName())
            .append("userAvatar", getUserAvatar())
            .toString();
    }
}
