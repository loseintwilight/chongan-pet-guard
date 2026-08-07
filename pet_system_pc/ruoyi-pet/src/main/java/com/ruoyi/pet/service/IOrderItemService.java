package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.OrderItem;

/**
 * 订单详情（订单项）Service接口
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
public interface IOrderItemService 
{
    /**
     * 查询订单详情（订单项）
     * 
     * @param itemId 订单详情（订单项）主键
     * @return 订单详情（订单项）
     */
    public OrderItem selectOrderItemByItemId(Long itemId);

    /**
     * 查询订单详情（订单项）列表
     * 
     * @param orderItem 订单详情（订单项）
     * @return 订单详情（订单项）集合
     */
    public List<OrderItem> selectOrderItemList(OrderItem orderItem);

    /**
     * 新增订单详情（订单项）
     * 
     * @param orderItem 订单详情（订单项）
     * @return 结果
     */
    public int insertOrderItem(OrderItem orderItem);

    /**
     * 修改订单详情（订单项）
     * 
     * @param orderItem 订单详情（订单项）
     * @return 结果
     */
    public int updateOrderItem(OrderItem orderItem);

    /**
     * 批量删除订单详情（订单项）
     * 
     * @param itemIds 需要删除的订单详情（订单项）主键集合
     * @return 结果
     */
    public int deleteOrderItemByItemIds(Long[] itemIds);

    /**
     * 删除订单详情（订单项）信息
     * 
     * @param itemId 订单详情（订单项）主键
     * @return 结果
     */
    public int deleteOrderItemByItemId(Long itemId);
}
