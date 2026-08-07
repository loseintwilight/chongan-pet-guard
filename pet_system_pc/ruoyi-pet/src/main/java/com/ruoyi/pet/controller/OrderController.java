package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.Order;
import com.ruoyi.pet.domain.OrderWithOrderItemDTO;
import com.ruoyi.pet.service.IOrderService;
import com.ruoyi.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 订单Controller
 *
 * @author ruoyi
 * @date 2025-12-08
 */
@Slf4j
@RestController
@RequestMapping("/pet/order")
public class OrderController extends BaseController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private ISysUserService userService;

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('pet:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(Order order) {
        startPage();
        List<Order> list = orderService.selectOrderList(order);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('pet:order:list')")
    @GetMapping("/getPurchaseRecords")
    public TableDataInfo getPurchaseRecords(Order order) {
        startPage();
        List<Order> list = orderService.wxSelectOrderList(order);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('pet:order:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Order order) {
        List<Order> list = orderService.selectOrderList(order);
        ExcelUtil<Order> util = new ExcelUtil<Order>(Order.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId) {
        return success(orderService.selectOrderByOrderId(orderId));
    }

    /**
     * 新增订单
     */
//    @PreAuthorize("@ss.hasPermi('pet:order:add')")
//    @Log(title = "订单", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody Order order)
//    {
//        return toAjax(orderService.insertOrder(order));
//    }

    /**
     * 新增订单
     */
    @PreAuthorize("@ss.hasPermi('pet:order:add')")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OrderWithOrderItemDTO orderWithOrderItemDTO) {
        log.info("订单详情信息：{}", orderWithOrderItemDTO.toString());
        return toAjax(orderService.insertOrderWithOrderItem(orderWithOrderItemDTO));
    }


    /**
     * 修改订单
     */
//    @PreAuthorize("@ss.hasPermi('pet:order:edit')")
//    @Log(title = "订单", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@RequestBody Order order) {
//        return toAjax(orderService.updateOrder(order));
//    }

    /**
     * 修改订单
     */
    @PreAuthorize("@ss.hasPermi('pet:order:edit')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OrderWithOrderItemDTO OrderWithOrderItemDTO) {
        return toAjax(orderService.updateOrderWithOrderItem(OrderWithOrderItemDTO));
    }

    /**
     * 删除订单
     */
    @PreAuthorize("@ss.hasPermi('pet:order:remove')")
    @Log(title = "订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds) {
        return toAjax(orderService.deleteOrderByOrderIds(orderIds));
    }
}
