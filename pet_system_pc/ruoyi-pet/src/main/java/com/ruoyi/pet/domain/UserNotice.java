package com.ruoyi.pet.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 通知与用户之间的对象 tb_user_notice
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
public class UserNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键")
    private Long unId;

    /** 通知id */
    @Excel(name = "通知id")
    private Long noticeId;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 阅读状态(0-未读，1-已读) */
    @Excel(name = "阅读状态(0-未读，1-已读)")
    private Long readStatus;

    /** 阅读时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "阅读时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date readTime;

    public void setUnId(Long unId) 
    {
        this.unId = unId;
    }

    public Long getUnId() 
    {
        return unId;
    }

    public void setNoticeId(Long noticeId) 
    {
        this.noticeId = noticeId;
    }

    public Long getNoticeId() 
    {
        return noticeId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setReadStatus(Long readStatus) 
    {
        this.readStatus = readStatus;
    }

    public Long getReadStatus() 
    {
        return readStatus;
    }

    public void setReadTime(Date readTime) 
    {
        this.readTime = readTime;
    }

    public Date getReadTime() 
    {
        return readTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("unId", getUnId())
            .append("noticeId", getNoticeId())
            .append("userId", getUserId())
            .append("readStatus", getReadStatus())
            .append("readTime", getReadTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
