package com.ruoyi.pet.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 救助登记对象 tb_rescue
 * 
 * @author ruoyi
 * @date 2025-12-06
 */
public class Rescue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键  救助id */
    private Long rescueId;

    /** 登记用户id */
    @Excel(name = "登记用户id")
    private Long userId;

    /** 上报人姓名 */
    @Excel(name = "上报人姓名")
    private String reporterName;

    /** 上报人电话 */
    @Excel(name = "上报人电话")
    private String reporterPhone;

    /** 宠物描述（如：种类，外貌，伤情） */
    private String petDesc;

    /** 发现地点 */
    @Excel(name = "发现地点")
    private String location;

    /** 现场图片 */
    @Excel(name = "现场图片")
    private String images;

    /** 处理状态（0-待处理，1-处理中，2-已完成,3-无效信息） */
    @Excel(name = "处理状态", readConverterExp = "0=-待处理，1-处理中，2-已完成,3-无效信息")
    private Long status;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /** 处理人id */
    private Long handleId;

    /** 处理备注 */
    private String handleRemark;

    /** 逻辑删除字段（0-未删除 1-已删除） */
    private Long isDeleted;

    public void setRescueId(Long rescueId) 
    {
        this.rescueId = rescueId;
    }

    public Long getRescueId() 
    {
        return rescueId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setReporterName(String reporterName) 
    {
        this.reporterName = reporterName;
    }

    public String getReporterName() 
    {
        return reporterName;
    }

    public void setReporterPhone(String reporterPhone) 
    {
        this.reporterPhone = reporterPhone;
    }

    public String getReporterPhone() 
    {
        return reporterPhone;
    }

    public void setPetDesc(String petDesc) 
    {
        this.petDesc = petDesc;
    }

    public String getPetDesc() 
    {
        return petDesc;
    }

    public void setLocation(String location) 
    {
        this.location = location;
    }

    public String getLocation() 
    {
        return location;
    }

    public void setImages(String images) 
    {
        this.images = images;
    }

    public String getImages() 
    {
        return images;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setHandleTime(Date handleTime) 
    {
        this.handleTime = handleTime;
    }

    public Date getHandleTime() 
    {
        return handleTime;
    }

    public void setHandleId(Long handleId) 
    {
        this.handleId = handleId;
    }

    public Long getHandleId() 
    {
        return handleId;
    }

    public void setHandleRemark(String handleRemark) 
    {
        this.handleRemark = handleRemark;
    }

    public String getHandleRemark() 
    {
        return handleRemark;
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
            .append("rescueId", getRescueId())
            .append("userId", getUserId())
            .append("reporterName", getReporterName())
            .append("reporterPhone", getReporterPhone())
            .append("petDesc", getPetDesc())
            .append("location", getLocation())
            .append("images", getImages())
            .append("status", getStatus())
            .append("handleTime", getHandleTime())
            .append("handleId", getHandleId())
            .append("handleRemark", getHandleRemark())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
