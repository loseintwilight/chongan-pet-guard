package com.ruoyi.pet.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 宠物丢失对象 tb_lost_pet
 * 
 * @author ruoyi
 * @date 2025-12-06
 */
public class LostPet extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 寻宠id */
    private Long lostId;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 主人姓名 */
    @Excel(name = "主人姓名")
    private String ownerName;

    /** 丢失主人电话 */
    @Excel(name = "丢失主人电话")
    private String ownerPhone;

    /** 宠物姓名 */
    @Excel(name = "宠物姓名")
    private String petName;

    /** 物种(0-猫,1-狗,2-其他) */
    @Excel(name = "物种(0-猫,1-狗,2-其他)")
    private Long species;

    /** 品种 */
    @Excel(name = "品种")
    private String breed;

    /** 显著特征 */
    @Excel(name = "显著特征")
    private String features;

    /** 想说的话 */
    @Excel(name = "想说的话")
    private String message;

    /** 丢失地点 */
    @Excel(name = "丢失地点")
    private String lostLocation;

    /** 丢失时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "丢失时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lostTime;

    /** 酬谢金额 */
    @Excel(name = "酬谢金额")
    private Long reward;

    /** 宠物图片 */
    @Excel(name = "宠物图片")
    private String images;

    /** 状态(0-寻找中，1-已找到) */
    @Excel(name = "状态(0-寻找中，1-已找到)")
    private Long status;

    /** 逻辑删除（0-未删除,1-已删除） */
    private Long isDeleted;

    public void setLostId(Long lostId) 
    {
        this.lostId = lostId;
    }

    public Long getLostId() 
    {
        return lostId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setOwnerName(String ownerName) 
    {
        this.ownerName = ownerName;
    }

    public String getOwnerName() 
    {
        return ownerName;
    }

    public void setOwnerPhone(String ownerPhone) 
    {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerPhone() 
    {
        return ownerPhone;
    }

    public void setPetName(String petName) 
    {
        this.petName = petName;
    }

    public String getPetName() 
    {
        return petName;
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

    public void setFeatures(String features) 
    {
        this.features = features;
    }

    public String getFeatures() 
    {
        return features;
    }

    public void setMessage(String message) 
    {
        this.message = message;
    }

    public String getMessage() 
    {
        return message;
    }

    public void setLostLocation(String lostLocation) 
    {
        this.lostLocation = lostLocation;
    }

    public String getLostLocation() 
    {
        return lostLocation;
    }

    public void setLostTime(Date lostTime) 
    {
        this.lostTime = lostTime;
    }

    public Date getLostTime() 
    {
        return lostTime;
    }

    public void setReward(Long reward) 
    {
        this.reward = reward;
    }

    public Long getReward() 
    {
        return reward;
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
            .append("lostId", getLostId())
            .append("userId", getUserId())
            .append("ownerName", getOwnerName())
            .append("ownerPhone", getOwnerPhone())
            .append("petName", getPetName())
            .append("species", getSpecies())
            .append("breed", getBreed())
            .append("features", getFeatures())
            .append("message", getMessage())
            .append("lostLocation", getLostLocation())
            .append("lostTime", getLostTime())
            .append("reward", getReward())
            .append("images", getImages())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
