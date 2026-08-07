package com.ruoyi.pet.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderWithOrderItemDTO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    private Long orderId;

    /**
     * 订单号
     */
    @Excel(name = "订单号")
    private String orderSn;

    /**
     * 下单用户
     */
    @Excel(name = "下单用户")
    private Long userId;

    /**
     * 店铺id
     */
    @Excel(name = "店铺id")
    private Long shopId;

    /**
     * 订单总金额
     */
    @Excel(name = "订单总金额")
    private Long totalAmount;

    /**
     * 订单状态
     */
    @Excel(name = "订单状态")
    private Long status;

    private List<OrderItem> orderItems;

}
