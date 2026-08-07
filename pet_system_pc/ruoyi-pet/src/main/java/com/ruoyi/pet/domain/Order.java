package com.ruoyi.pet.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单对象 tb_order
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
public class Order extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 id */
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderSn;

    /** 下单用户 */
    @Excel(name = "下单用户")
    private Long userId;

    /** 店铺id */
    @Excel(name = "店铺id")
    private Long shopId;

    /** 订单总金额 */
    @Excel(name = "订单总金额")
    private Long totalAmount;

    /** 订单状态 */
    @Excel(name = "订单状态")
    private Long status;

    /** 逻辑删除（0-未删除，1-已删除） */
    private Long isDeleted;

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setOrderSn(String orderSn) 
    {
        this.orderSn = orderSn;
    }

    public String getOrderSn() 
    {
        return orderSn;
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

    public void setTotalAmount(Long totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public Long getTotalAmount() 
    {
        return totalAmount;
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
            .append("orderId", getOrderId())
            .append("orderSn", getOrderSn())
            .append("userId", getUserId())
            .append("shopId", getShopId())
            .append("totalAmount", getTotalAmount())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
