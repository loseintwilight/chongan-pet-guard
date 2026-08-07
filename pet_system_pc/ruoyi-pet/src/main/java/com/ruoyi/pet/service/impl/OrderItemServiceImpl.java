package com.ruoyi.pet.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.OrderItemMapper;
import com.ruoyi.pet.domain.OrderItem;
import com.ruoyi.pet.service.IOrderItemService;

/**
 * 订单详情（订单项）Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
@Service
public class OrderItemServiceImpl implements IOrderItemService 
{
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 查询订单详情（订单项）
     * 
     * @param itemId 订单详情（订单项）主键
     * @return 订单详情（订单项）
     */
    @Override
    public OrderItem selectOrderItemByItemId(Long itemId)
    {
        return orderItemMapper.selectOrderItemByItemId(itemId);
    }

    /**
     * 查询订单详情（订单项）列表
     * 
     * @param orderItem 订单详情（订单项）
     * @return 订单详情（订单项）
     */
    @Override
    public List<OrderItem> selectOrderItemList(OrderItem orderItem)
    {
        return orderItemMapper.selectOrderItemList(orderItem);
    }

    /**
     * 新增订单详情（订单项）
     * 
     * @param orderItem 订单详情（订单项）
     * @return 结果
     */
    @Override
    public int insertOrderItem(OrderItem orderItem)
    {
        return orderItemMapper.insertOrderItem(orderItem);
    }

    /**
     * 修改订单详情（订单项）
     * 
     * @param orderItem 订单详情（订单项）
     * @return 结果
     */
    @Override
    public int updateOrderItem(OrderItem orderItem)
    {
        return orderItemMapper.updateOrderItem(orderItem);
    }

    /**
     * 批量删除订单详情（订单项）
     * 
     * @param itemIds 需要删除的订单详情（订单项）主键
     * @return 结果
     */
    @Override
    public int deleteOrderItemByItemIds(Long[] itemIds)
    {
        return orderItemMapper.deleteOrderItemByItemIds(itemIds);
    }

    /**
     * 删除订单详情（订单项）信息
     * 
     * @param itemId 订单详情（订单项）主键
     * @return 结果
     */
    @Override
    public int deleteOrderItemByItemId(Long itemId)
    {
        return orderItemMapper.deleteOrderItemByItemId(itemId);
    }
}
