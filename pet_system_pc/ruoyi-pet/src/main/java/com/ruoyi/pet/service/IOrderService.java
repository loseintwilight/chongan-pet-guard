package com.ruoyi.pet.service;

import java.util.List;

import com.ruoyi.pet.domain.Order;
import com.ruoyi.pet.domain.OrderWithOrderItemDTO;

/**
 * 订单Service接口
 *
 * @author ruoyi
 * @date 2025-12-08
 */
public interface IOrderService {
    /**
     * 查询订单
     *
     * @param orderId 订单主键
     * @return 订单
     */
    public OrderWithOrderItemDTO selectOrderByOrderId(Long orderId);

    /**
     * 查询订单列表
     *
     * @param order 订单
     * @return 订单集合
     */
    public List<Order> selectOrderList(Order order);

    /**
     * 新增订单
     *
     * @param order 订单
     * @return 结果
     */
    public int insertOrder(Order order);

    /**
     * 修改订单
     *
     * @param order 订单
     * @return 结果
     */
    public int updateOrder(Order order);

    /**
     * 批量删除订单
     *
     * @param orderIds 需要删除的订单主键集合
     * @return 结果
     */
    public int deleteOrderByOrderIds(Long[] orderIds);

    /**
     * 删除订单信息
     *
     * @param orderId 订单主键
     * @return 结果
     */
    public int deleteOrderByOrderId(Long orderId);

    /**
     * 新增订单
     *
     * @param orderWithOrderItemDTO 接受到的订单信息
     * @return 结果
     */
    public int insertOrderWithOrderItem(OrderWithOrderItemDTO orderWithOrderItemDTO);

    int updateOrderWithOrderItem(OrderWithOrderItemDTO orderWithOrderItemDTO);

    List<Order> wxSelectOrderList(Order order);

}
