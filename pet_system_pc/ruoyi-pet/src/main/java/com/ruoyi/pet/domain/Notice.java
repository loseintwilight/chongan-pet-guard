package com.ruoyi.pet.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 通知对象 tb_notice
 *
 * @author ruoyi
 * @date 2025-12-07
 */
public class Notice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long noticeId;

    /**
     * 使用的模板id
     */
    @Excel(name = "使用的模板id")
    private Long templateId;

    /**
     * 通知标题
     */
    @Excel(name = "通知标题")
    private String title;

    /**
     * 通知内容
     */
    @Excel(name = "通知内容")
    private String noticeMain;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishTime;

    /**
     * 发布人
     */
    @Excel(name = "发布人")
    private String publishName;

    @Excel(name = "收件人")
    private String recipientName;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /**
     * 状态（0-已发布，1-编辑中，2-未发送）
     */
    @Excel(name = "状态", readConverterExp = "0=-已发布，1-编辑中，2-未发送")
    private Long status;

    /**
     * 逻辑删除（0-未删除，1-已删除）
     */
    private Long isDeleted;

    private Long readStatus;

    public Long getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Long readStatus) {
        this.readStatus = readStatus;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setNoticeMain(String noticeMain) {
        this.noticeMain = noticeMain;
    }

    public String getNoticeMain() {
        return noticeMain;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }

    public void setIsDeleted(Long isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getIsDeleted() {
        return isDeleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("noticeId", getNoticeId())
                .append("templateId", getTemplateId())
                .append("title", getTitle())
                .append("noticeMain", getNoticeMain())
                .append("publishTime", getPublishTime())
                .append("publishName", getPublishName())
                .append("status", getStatus())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("remark", getRemark())
                .append("isDeleted", getIsDeleted())
                .append("recipientName", getRecipientName())
                .toString();
    }
}
