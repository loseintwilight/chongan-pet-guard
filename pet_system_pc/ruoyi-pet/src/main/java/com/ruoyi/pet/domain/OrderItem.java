package com.ruoyi.pet.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单详情（订单项）对象 tb_order_item
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
public class OrderItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单项id */
    private Long itemId;

    /** 订单id */
    @Excel(name = "订单id")
    private Long orderId;

    /** 商品id */
    @Excel(name = "商品id")
    private Long resourceId;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String resourceName;

    /** 购买时单价 */
    @Excel(name = "购买时单价")
    private Long price;

    /** 购买数量 */
    @Excel(name = "购买数量")
    private Long quantity;

    /** 小计金额 */
    @Excel(name = "小计金额")
    private Long totalPrice;

    /** 逻辑删除（0-未删除，1-已删除） */
    private Long isDeleted;

    public void setItemId(Long itemId) 
    {
        this.itemId = itemId;
    }

    public Long getItemId() 
    {
        return itemId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

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

    public void setPrice(Long price) 
    {
        this.price = price;
    }

    public Long getPrice() 
    {
        return price;
    }

    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }

    public void setTotalPrice(Long totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public Long getTotalPrice() 
    {
        return totalPrice;
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
            .append("itemId", getItemId())
            .append("orderId", getOrderId())
            .append("resourceId", getResourceId())
            .append("resourceName", getResourceName())
            .append("price", getPrice())
            .append("quantity", getQuantity())
            .append("totalPrice", getTotalPrice())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
