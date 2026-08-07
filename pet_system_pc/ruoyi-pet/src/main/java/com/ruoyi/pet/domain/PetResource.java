package com.ruoyi.pet.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品对象 tb_pet_resource
 *
 * @author ruoyi
 * @date 2025-12-08
 */
public class PetResource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long resourceId;

    @Excel(name = "商品名称")
    private String resourceName;

    @Excel(name = "图片URL")
    private String images;

    @Excel(name = "分类id")
    private Long categoryId;

    @Excel(name = "分类名称")
    private String categoryName;

    @Excel(name = "状态")
    private Long status;

    @Excel(name = "规格")
    private String specification;

    @Excel(name = "库存")
    private Long stock;

    @Excel(name = "销量")
    private Long sales;

    private Long isDeleted;

    @Excel(name = "商品价格", readConverterExp = "单=位为分，减少进制误差")
    private Long price;

    @Excel(name = "店铺id")
    private Long shopId;

    private String description;

    private Long originalPrice;

    private String unit;

    private Long weight;

    private Long sort;

    private Long isHot;

    private Long isNew;

    private Long isRecommend;

    private String shopName;

    public void setResourceId(Long resourceId)
    {
        this.resourceId = resourceId;
    }

    public Long getResourceId()
    {
        return resourceId;
    }

    public void setResourceName(String resourceName)
    {
        this.resourceName = resourceName;
    }

    public String getResourceName()
    {
        return resourceName;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getImages()
    {
        return images;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    public void setSpecification(String specification)
    {
        this.specification = specification;
    }

    public String getSpecification()
    {
        return specification;
    }

    public void setStock(Long stock)
    {
        this.stock = stock;
    }

    public Long getStock()
    {
        return stock;
    }

    public void setSales(Long sales)
    {
        this.sales = sales;
    }

    public Long getSales()
    {
        return sales;
    }

    public void setIsDeleted(Long isDeleted)
    {
        this.isDeleted = isDeleted;
    }

    public Long getIsDeleted()
    {
        return isDeleted;
    }

    public void setPrice(Long price)
    {
        this.price = price;
    }

    public Long getPrice()
    {
        return price;
    }

    public void setShopId(Long shopId)
    {
        this.shopId = shopId;
    }

    public Long getShopId()
    {
        return shopId;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setOriginalPrice(Long originalPrice)
    {
        this.originalPrice = originalPrice;
    }

    public Long getOriginalPrice()
    {
        return originalPrice;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setWeight(Long weight)
    {
        this.weight = weight;
    }

    public Long getWeight()
    {
        return weight;
    }

    public void setSort(Long sort)
    {
        this.sort = sort;
    }

    public Long getSort()
    {
        return sort;
    }

    public void setIsHot(Long isHot)
    {
        this.isHot = isHot;
    }

    public Long getIsHot()
    {
        return isHot;
    }

    public void setIsNew(Long isNew)
    {
        this.isNew = isNew;
    }

    public Long getIsNew()
    {
        return isNew;
    }

    public void setIsRecommend(Long isRecommend)
    {
        this.isRecommend = isRecommend;
    }

    public Long getIsRecommend()
    {
        return isRecommend;
    }

    public void setShopName(String shopName)
    {
        this.shopName = shopName;
    }

    public String getShopName()
    {
        return shopName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("resourceId", getResourceId())
            .append("resourceName", getResourceName())
            .append("images", getImages())
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("status", getStatus())
            .append("specification", getSpecification())
            .append("stock", getStock())
            .append("sales", getSales())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .append("isDeleted", getIsDeleted())
            .append("price", getPrice())
            .append("shopId", getShopId())
            .append("description", getDescription())
            .append("originalPrice", getOriginalPrice())
            .append("unit", getUnit())
            .append("weight", getWeight())
            .append("sort", getSort())
            .append("isHot", getIsHot())
            .append("isNew", getIsNew())
            .append("isRecommend", getIsRecommend())
            .append("shopName", getShopName())
            .toString();
    }
}
