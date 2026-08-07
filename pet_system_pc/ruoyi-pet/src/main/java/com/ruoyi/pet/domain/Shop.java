package com.ruoyi.pet.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 店铺，站点对象 tb_shop
 * 
 * @author ruoyi
 * @date 2025-12-04
 */
public class Shop extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 店铺id */
    private Long shopId;

    /** 店铺名称 */
    @Excel(name = "店铺名称")
    private String shopName;

    /** 店铺主图 */
    @Excel(name = "店铺主图")
    private String mainImage;

    /** 店主id */
    private Long userId;

    /** 负责人手机号 */
    @Excel(name = "负责人手机号")
    private String legalPhone;

    /** 负责人姓名 */
    @Excel(name = "负责人姓名")
    private String legalName;

    /** 负责人身份证号 */
    private String legalNumber;

    /** 营业执照号 */
    private String licenseNumber;

    /** 营业执照图片URL */
    private String licenseImage;

    /** 省份 */
    private String province;

    /** 城市 */
    @Excel(name = "城市")
    private String city;

    /** 区县 */
    private String district;

    /** 详细地址 */
    private String address;

    /** 联系电话 */
    private String ownerPhone;

    /** 营业时间(如：09:00:00-18:00:00) */
    @Excel(name = "营业时间(如：09:00:00-18:00:00)")
    private String bussinessHours;

    /** 店铺描述，特色服务 */
    private String description;

    /** 状态（0-正常营业，1-休息中，2-未审核） */
    @Excel(name = "状态", readConverterExp = "0=-正常营业，1-休息中，2-未审核")
    private Long status;

    /** 创建人 */
    private String creareBy;

    /** 审核人id */
    private Long reviewId;

    /** 审核时间 */
    private Date reviewTime;

    /** 审核意见 */
    private String reviewRemark;

    /** 逻辑删除（0-未删除，1-已删除） */
    private Long isDeleted;

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

    public void setMainImage(String mainImage) 
    {
        this.mainImage = mainImage;
    }

    public String getMainImage() 
    {
        return mainImage;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setLegalPhone(String legalPhone) 
    {
        this.legalPhone = legalPhone;
    }

    public String getLegalPhone() 
    {
        return legalPhone;
    }

    public void setLegalName(String legalName) 
    {
        this.legalName = legalName;
    }

    public String getLegalName() 
    {
        return legalName;
    }

    public void setLegalNumber(String legalNumber) 
    {
        this.legalNumber = legalNumber;
    }

    public String getLegalNumber() 
    {
        return legalNumber;
    }

    public void setLicenseNumber(String licenseNumber) 
    {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() 
    {
        return licenseNumber;
    }

    public void setLicenseImage(String licenseImage) 
    {
        this.licenseImage = licenseImage;
    }

    public String getLicenseImage() 
    {
        return licenseImage;
    }

    public void setProvince(String province) 
    {
        this.province = province;
    }

    public String getProvince() 
    {
        return province;
    }

    public void setCity(String city) 
    {
        this.city = city;
    }

    public String getCity() 
    {
        return city;
    }

    public void setDistrict(String district) 
    {
        this.district = district;
    }

    public String getDistrict() 
    {
        return district;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setOwnerPhone(String ownerPhone) 
    {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerPhone() 
    {
        return ownerPhone;
    }

    public void setBussinessHours(String bussinessHours) 
    {
        this.bussinessHours = bussinessHours;
    }

    public String getBussinessHours() 
    {
        return bussinessHours;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setCreareBy(String creareBy) 
    {
        this.creareBy = creareBy;
    }

    public String getCreareBy() 
    {
        return creareBy;
    }

    public void setReviewId(Long reviewId) 
    {
        this.reviewId = reviewId;
    }

    public Long getReviewId() 
    {
        return reviewId;
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
            .append("shopId", getShopId())
            .append("shopName", getShopName())
            .append("mainImage", getMainImage())
            .append("userId", getUserId())
            .append("legalPhone", getLegalPhone())
            .append("legalName", getLegalName())
            .append("legalNumber", getLegalNumber())
            .append("licenseNumber", getLicenseNumber())
            .append("licenseImage", getLicenseImage())
            .append("province", getProvince())
            .append("city", getCity())
            .append("district", getDistrict())
            .append("address", getAddress())
            .append("ownerPhone", getOwnerPhone())
            .append("bussinessHours", getBussinessHours())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("creareBy", getCreareBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("reviewId", getReviewId())
            .append("reviewTime", getReviewTime())
            .append("reviewRemark", getReviewRemark())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
