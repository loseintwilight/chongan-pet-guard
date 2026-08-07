package com.ruoyi.pet.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 宠物托养对象 tb_pet_boarding
 * 
 * @author ruoyi
 * @date 2025-12-04
 */
public class PetBoarding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long boardingId;

    /** 用户id */
    private Long userId;

    /** 店铺id */
    @Excel(name = "店铺id")
    private Long shopId;

    /** 店铺名称 */
    @Excel(name = "店铺名称")
    private String shopName;

    /** 宠物名称 */
    @Excel(name = "宠物名称")
    private String name;

    /** 物种 */
    @Excel(name = "物种")
    private Long species;

    /** 品种 */
    @Excel(name = "品种")
    private String breed;

    /** 性别 */
    @Excel(name = "性别")
    private Long gender;

    /** 年龄 */
    @Excel(name = "年龄")
    private Long age;

    /** 健康状态 */
    @Excel(name = "健康状态")
    private String healthInfo;

    /** 是否绝育 */
    @Excel(name = "是否绝育")
    private Long neuter;

    /** 是否免疫 */
    @Excel(name = "是否免疫")
    private Long immunity;

    /** 是否驱虫 */
    @Excel(name = "是否驱虫")
    private Long insectRepellent;

    /** 驱虫周期，天 */
    @Excel(name = "驱虫周期，天")
    private Long dewormingInterval;

    /** 上回驱虫日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上回驱虫日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date insectRepellentLastTime;

    /** 宠物描述 */
    @Excel(name = "宠物描述")
    private String description;

    /** 预计开始托养时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预计开始托养时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 预计结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预计结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 总托养天数 */
    private Long totalDays;

    /** 总费用 */
    private Long totalAmount;

    /** 特殊要求 */
    @Excel(name = "特殊要求")
    private String specialRequirement;

    /** 紧急联系人 */
    private String emergencyContact;

    /** 紧急联系人电话 */
    private String emergencyPhone;

    /** 托养状态（0-待审核，1-已确认，待送宠，2-托养中，3-已完成，4-已取消，5-用户已取消） */
    @Excel(name = "托养状态", readConverterExp = "0=-待审核，1-已确认，待送宠，2-托养中，3-已完成，4-已取消，5-用户已取消")
    private Long status;

    /** 审核人id */
    private Long reviewerId;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reviewTime;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String reviewRemark;

    /** 实际送宠时间 */
    private Date checkInTime;

    /** 实际接宠时间 */
    private Date checkOutTime;

    /** 支付状态（0-未支付，1-已支付） */
    @Excel(name = "支付状态", readConverterExp = "0=-未支付，1-已支付")
    private Long paymentStatus;

    /** 逻辑删除（0-未删除，1-已删除） */
    private Long isDeleted;

    /** 宠物图片 */
    @Excel(name = "宠物图片")
    private String images;

    public void setBoardingId(Long boardingId) 
    {
        this.boardingId = boardingId;
    }

    public Long getBoardingId() 
    {
        return boardingId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setShopId(Long shopId) 
    {
        this.shopId = shopId;
    }

    public Long getShopId() 
    {
        return shopId;
    }

    public void setShopName(String shopName) 
    {
        this.shopName = shopName;
    }

    public String getShopName() 
    {
        return shopName;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setSpecies(Long species) 
    {
        this.species = species;
    }

    public Long getSpecies() 
    {
        return species;
    }

    public void setBreed(String breed) 
    {
        this.breed = breed;
    }

    public String getBreed() 
    {
        return breed;
    }

    public void setGender(Long gender) 
    {
        this.gender = gender;
    }

    public Long getGender() 
    {
        return gender;
    }

    public void setAge(Long age) 
    {
        this.age = age;
    }

    public Long getAge() 
    {
        return age;
    }

    public void setHealthInfo(String healthInfo) 
    {
        this.healthInfo = healthInfo;
    }

    public String getHealthInfo() 
    {
        return healthInfo;
    }

    public void setNeuter(Long neuter) 
    {
        this.neuter = neuter;
    }

    public Long getNeuter() 
    {
        return neuter;
    }

    public void setImmunity(Long immunity) 
    {
        this.immunity = immunity;
    }

    public Long getImmunity() 
    {
        return immunity;
    }

    public void setInsectRepellent(Long insectRepellent) 
    {
        this.insectRepellent = insectRepellent;
    }

    public Long getInsectRepellent() 
    {
        return insectRepellent;
    }

    public void setDewormingInterval(Long dewormingInterval) 
    {
        this.dewormingInterval = dewormingInterval;
    }

    public Long getDewormingInterval() 
    {
        return dewormingInterval;
    }

    public void setInsectRepellentLastTime(Date insectRepellentLastTime) 
    {
        this.insectRepellentLastTime = insectRepellentLastTime;
    }

    public Date getInsectRepellentLastTime() 
    {
        return insectRepellentLastTime;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }

    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }

    public void setTotalDays(Long totalDays) 
    {
        this.totalDays = totalDays;
    }

    public Long getTotalDays() 
    {
        return totalDays;
    }

    public void setTotalAmount(Long totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public Long getTotalAmount() 
    {
        return totalAmount;
    }

    public void setSpecialRequirement(String specialRequirement) 
    {
        this.specialRequirement = specialRequirement;
    }

    public String getSpecialRequirement() 
    {
        return specialRequirement;
    }

    public void setEmergencyContact(String emergencyContact) 
    {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyContact() 
    {
        return emergencyContact;
    }

    public void setEmergencyPhone(String emergencyPhone) 
    {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyPhone() 
    {
        return emergencyPhone;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setReviewerId(Long reviewerId) 
    {
        this.reviewerId = reviewerId;
    }

    public Long getReviewerId() 
    {
        return reviewerId;
    }

    public void setReviewTime(Date reviewTime) 
    {
        this.reviewTime = reviewTime;
    }

    public Date getReviewTime() 
    {
        return reviewTime;
    }

    public void setReviewRemark(String reviewRemark) 
    {
        this.reviewRemark = reviewRemark;
    }

    public String getReviewRemark() 
    {
        return reviewRemark;
    }

    public void setCheckInTime(Date checkInTime) 
    {
        this.checkInTime = checkInTime;
    }

    public Date getCheckInTime() 
    {
        return checkInTime;
    }

    public void setCheckOutTime(Date checkOutTime) 
    {
        this.checkOutTime = checkOutTime;
    }

    public Date getCheckOutTime() 
    {
        return checkOutTime;
    }

    public void setPaymentStatus(Long paymentStatus) 
    {
        this.paymentStatus = paymentStatus;
    }

    public Long getPaymentStatus() 
    {
        return paymentStatus;
    }

    public void setIsDeleted(Long isDeleted) 
    {
        this.isDeleted = isDeleted;
    }

    public Long getIsDeleted() 
    {
        return isDeleted;
    }

    public void setImages(String images) 
    {
        this.images = images;
    }

    public String getImages() 
    {
        return images;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("boardingId", getBoardingId())
            .append("userId", getUserId())
            .append("shopId", getShopId())
            .append("shopName", getShopName())
            .append("name", getName())
            .append("species", getSpecies())
            .append("breed", getBreed())
            .append("gender", getGender())
            .append("age", getAge())
            .append("healthInfo", getHealthInfo())
            .append("neuter", getNeuter())
            .append("immunity", getImmunity())
            .append("insectRepellent", getInsectRepellent())
            .append("dewormingInterval", getDewormingInterval())
            .append("insectRepellentLastTime", getInsectRepellentLastTime())
            .append("description", getDescription())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("totalDays", getTotalDays())
            .append("totalAmount", getTotalAmount())
            .append("specialRequirement", getSpecialRequirement())
            .append("emergencyContact", getEmergencyContact())
            .append("emergencyPhone", getEmergencyPhone())
            .append("status", getStatus())
            .append("reviewerId", getReviewerId())
            .append("reviewTime", getReviewTime())
            .append("reviewRemark", getReviewRemark())
            .append("checkInTime", getCheckInTime())
            .append("checkOutTime", getCheckOutTime())
            .append("paymentStatus", getPaymentStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("isDeleted", getIsDeleted())
            .append("images", getImages())
            .toString();
    }
}
