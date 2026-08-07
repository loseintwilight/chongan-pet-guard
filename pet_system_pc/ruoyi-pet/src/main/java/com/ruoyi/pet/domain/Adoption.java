package com.ruoyi.pet.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 宠物领养对象 tb_adoption
 *
 * @author ruoyi
 * @date 2025-12-07
 */
public class Adoption extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private NoticeTemplate noticeTemplate;
    /**
     * 领养申请id
     */
    private Long adoptionId;

    /**
     * 申请宠物id
     */
    private Long petId;

    /**
     * 申请人id
     */
    private Long userId;

    private Pet pet;

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    /**
     * 申请人姓名
     */
    @Excel(name = "申请人姓名")
    private String applicationName;

    /**
     * 申请人电话
     */
    @Excel(name = "申请人电话")
    private String applicationPhone;

    /**
     * 养宠经验
     */
    @Excel(name = "养宠经验")
    private String experience;

    /**
     * 领养理由
     */
    @Excel(name = "领养理由")
    private String reason;

    /**
     * 申请状态（0-待审核，1-审核通过，2-审核拒绝,3-已领养）
     */
    @Excel(name = "申请状态", readConverterExp = "0=-待审核，1-审核通过，2-审核拒绝,3-已领养")
    private Long status;

    /**
     * 审核人id
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reviewTime;

    /**
     * 审核意见
     */
    private String reviewRemark;

    /**
     * 实际领养时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date adoptTime;

    /**
     * 逻辑删除（0-未删除，1-已删除）默认0
     */
    private Long isDeleted;

    public void setAdoptionId(Long adoptionId) {
        this.adoptionId = adoptionId;
    }

    public Long getAdoptionId() {
        return adoptionId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationPhone(String applicationPhone) {
        this.applicationPhone = applicationPhone;
    }

    public String getApplicationPhone() {
        return applicationPhone;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getExperience() {
        return experience;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setAdoptTime(Date adoptTime) {
        this.adoptTime = adoptTime;
    }

    public Date getAdoptTime() {
        return adoptTime;
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
                .append("adoptionId", getAdoptionId())
                .append("petId", getPetId())
                .append("userId", getUserId())
                .append("applicationName", getApplicationName())
                .append("applicationPhone", getApplicationPhone())
                .append("experience", getExperience())
                .append("reason", getReason())
                .append("status", getStatus())
                .append("reviewerId", getReviewerId())
                .append("reviewTime", getReviewTime())
                .append("reviewRemark", getReviewRemark())
                .append("adoptTime", getAdoptTime())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("isDeleted", getIsDeleted())
                .toString();
    }
}
