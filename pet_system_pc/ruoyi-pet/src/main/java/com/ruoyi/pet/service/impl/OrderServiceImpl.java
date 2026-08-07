package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.pet.domain.OrderItem;
import com.ruoyi.pet.domain.OrderWithOrderItemDTO;
import com.ruoyi.pet.domain.PetResource;
import com.ruoyi.pet.mapper.OrderItemMapper;
import com.ruoyi.pet.mapper.PetResourceMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.OrderMapper;
import com.ruoyi.pet.domain.Order;
import com.ruoyi.pet.service.IOrderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-08
 */
@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PetResourceMapper petResourceMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 查询订单
     *
     * @param orderId 订单主键
     * @return 订单
     */
    @Override
    public OrderWithOrderItemDTO selectOrderByOrderId(Long orderId) {
        OrderWithOrderItemDTO orderWithOrderItemDTO = new OrderWithOrderItemDTO();
        Order order = orderMapper.selectOrderByOrderId(orderId);
        //订单信息
        BeanUtils.copyProperties(order, orderWithOrderItemDTO);
        //查询订单项信息
        List<OrderItem> orderItems = orderItemMapper.selectOrderItemByOrderId(orderId);
        orderWithOrderItemDTO.setOrderItems(orderItems);
        return orderWithOrderItemDTO;
    }

    /**
     * 查询订单列表
     *
     * @param order 订单
     * @return 订单
     */
    @Override
    public List<Order> selectOrderList(Order order) {
        return orderMapper.selectOrderList(order);
    }

    /**
     * 新增订单
     *
     * @param order 订单
     * @return 结果
     */
    @Override
    public int insertOrder(Order order) {
        order.setCreateTime(DateUtils.getNowDate());
        return orderMapper.insertOrder(order);
    }

    /**
     * 修改订单
     *
     * @param order 订单
     * @return 结果
     */
    @Override
    public int updateOrder(Order order) {
        order.setUpdateTime(DateUtils.getNowDate());
        return orderMapper.updateOrder(order);
    }

    /**
     * 批量删除订单
     *
     * @param orderIds 需要删除的订单主键
     * @return 结果
     */
//    @Override
//    public int deleteOrderByOrderIds(Long[] orderIds) {
//        return orderMapper.deleteOrderByOrderIds(orderIds);
//    }


    /**
     * 批量删除订单
     *
     * @param orderIds 需要删除的订单主键
     * @return 结果
     */
    @Override
    public int deleteOrderByOrderIds(Long[] orderIds) {
        return orderMapper.updateDeleteOrderByOrderIds(orderIds);
    }


    /**
     * 删除订单信息
     *
     * @param orderId 订单主键
     * @return 结果
     */
    @Override
    public int deleteOrderByOrderId(Long orderId) {
        return orderMapper.deleteOrderByOrderId(orderId);
    }

    /**
     * 新增订单
     *
     * @param orderWithOrderItemDTO 接受到的订单信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertOrderWithOrderItem(OrderWithOrderItemDTO orderWithOrderItemDTO) {
        //获取订单数据
        Order order = new Order();
        BeanUtils.copyProperties(orderWithOrderItemDTO, order);
        //设置创建的时间
        order.setCreateTime(DateUtils.getNowDate());
        //设置创建人
        order.setCreateBy(SecurityUtils.getUsername());
        orderMapper.insertOrder(order);
        //新增后，通过主键返回策略，获取对应的订单id
        Long orderId = order.getOrderId();
        //获取订单项数据
        List<OrderItem> orderItems = orderWithOrderItemDTO.getOrderItems();
        if (null != orderItems) {
            orderItems.forEach(orderItem -> {
                //设置订单id
                orderItem.setOrderId(orderId);
                //获取商品id
                Long resourceId = orderItem.getResourceId();
                //根据商品id查询商品信息
                PetResource petResource = petResourceMapper.selectPetResourceByResourceId(resourceId);
                //获取商品名称
                String resourceName = petResource.getResourceName();
                orderItem.setResourceName(resourceName);
                //计算小计
                //1.单价
                Long price = orderItem.getPrice();
                //2.数量
                Long quantity = orderItem.getQuantity();
                orderItem.setTotalPrice(price * quantity);
                //更新商品数据信息
                petResource.setSales(petResource.getSales() + quantity); //售量
                petResource.setStock(petResource.getStock() - quantity); //库存
                petResourceMapper.updatePetResource(petResource);
                //插入数据
                orderItemMapper.insertOrderItem(orderItem);
            });
        }
        return 1;
    }

    @Override
    @Transactional
    public int updateOrderWithOrderItem(OrderWithOrderItemDTO orderWithOrderItemDTO) {
        //获取订单号
        Long orderId = orderWithOrderItemDTO.getOrderId();
        //删除订单项，重新新建
        orderItemMapper.deleteOrderItemByOrderId(orderId);
        //修改订单信息
        Order order = new Order();
        BeanUtils.copyProperties(orderWithOrderItemDTO, order);
        //设置修改人和修改时间
        order.setUpdateTime(DateUtils.getNowDate());
        order.setUpdateBy(SecurityUtils.getUsername());
        orderMapper.updateOrder(order);
        //新增订单项信息
        //获取订单项数据
        List<OrderItem> orderItems = orderWithOrderItemDTO.getOrderItems();
        if (null != orderItems) {
            orderItems.forEach(orderItem -> {
                //设置订单id
                orderItem.setOrderId(orderId);
                //获取商品id
                Long resourceId = orderItem.getResourceId();
                //根据商品id查询商品信息
                PetResource petResource = petResourceMapper.selectPetResourceByResourceId(resourceId);
                //获取商品名称
                String resourceName = petResource.getResourceName();
                orderItem.setResourceName(resourceName);
                //计算小计
                //1.单价
                Long price = orderItem.getPrice();
                //2.数量
                Long quantity = orderItem.getQuantity();
                orderItem.setTotalPrice(price * quantity);
                //插入数据
                orderItemMapper.insertOrderItem(orderItem);
            });
        }
        return 1;
    }

    @Override
    public List<Order> wxSelectOrderList(Order order) {
        order.setUserId(SecurityUtils.getUserId());
        return orderMapper.selectOrderList(order);
    }
}
