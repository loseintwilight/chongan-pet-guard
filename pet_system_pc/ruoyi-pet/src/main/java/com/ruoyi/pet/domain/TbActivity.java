package com.ruoyi.pet.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 活动对象 tb_activity
 * 
 * @author ruoyi
 * @date 2025-12-04
 */
public class TbActivity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 活动id */
    private Long activityId;

    /** 活动标题 */
    @Excel(name = "活动标题")
    private String title;

    /** 活动详情富文本 */
    @Excel(name = "活动详情富文本")
    private String content;

    /** 图片 */
    @Excel(name = "图片")
    private String images;

    /** 开始报名时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始报名时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startSignTime;

    /** 结束报名时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束报名时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endSignTime;

    /** 活动时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "活动时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 创办组织 */
    @Excel(name = "创办组织")
    private String createOrganize;

    /** 活动分类 */
    @Excel(name = "活动分类")
    private Long activityCategory;

    /** 活动地点 */
    @Excel(name = "活动地点")
    private String address;

    /** 当前人数 */
    @Excel(name = "当前人数")
    private Long count;

    /** 人数限制 */
    @Excel(name = "人数限制")
    private Long personCount;

    /** 活动状态（0-报名中，1-报名结束，2-已结束，3-未开始） */
    @Excel(name = "活动状态", readConverterExp = "0=-报名中，1-报名结束，2-已结束，3-未开始")
    private Long status;

    /** 是否顶置（0-顶置，1-未顶置） */
    @Excel(name = "是否顶置", readConverterExp = "0=-顶置，1-未顶置")
    private Long isTop;

    /** 逻辑删除（0-未删除，1-已删除） */
    private Long isDeleted;

    private Long isSignedUp;

    public Long getIsSignedUp() {
        return isSignedUp;
    }

    public void setIsSignedUp(Long isSignedUp) {
        this.isSignedUp = isSignedUp;
    }

    public void setActivityId(Long activityId)
    {
        this.activityId = activityId;
    }

    public Long getActivityId() 
    {
        return activityId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
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

    public void setStartSignTime(Date startSignTime) 
    {
        this.startSignTime = startSignTime;
    }

    public Date getStartSignTime() 
    {
        return startSignTime;
    }

    public void setEndSignTime(Date endSignTime) 
    {
        this.endSignTime = endSignTime;
    }

    public Date getEndSignTime() 
    {
        return endSignTime;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setCreateOrganize(String createOrganize) 
    {
        this.createOrganize = createOrganize;
    }

    public String getCreateOrganize() 
    {
        return createOrganize;
    }

    public void setActivityCategory(Long activityCategory) 
    {
        this.activityCategory = activityCategory;
    }

    public Long getActivityCategory() 
    {
        return activityCategory;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setCount(Long count) 
    {
        this.count = count;
    }

    public Long getCount() 
    {
        return count;
    }

    public void setPersonCount(Long personCount) 
    {
        this.personCount = personCount;
    }

    public Long getPersonCount() 
    {
        return personCount;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setIsTop(Long isTop) 
    {
        this.isTop = isTop;
    }

    public Long getIsTop() 
    {
        return isTop;
    }

    public void setIsDeleted(Long isDeleted) 
    {
        this.isDeleted = isDeleted;
    }

    public Long getIsDeleted() 
    {
        return isDeleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("activityId", getActivityId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("images", getImages())
            .append("startSignTime", getStartSignTime())
            .append("endSignTime", getEndSignTime())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("createOrganize", getCreateOrganize())
            .append("activityCategory", getActivityCategory())
            .append("address", getAddress())
            .append("count", getCount())
            .append("personCount", getPersonCount())
            .append("status", getStatus())
            .append("isTop", getIsTop())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
