package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.OrderItem;
import com.ruoyi.pet.service.IOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 订单详情（订单项）Controller
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
@RestController
@RequestMapping("/pet/resourceItem")
public class OrderItemController extends BaseController
{
    @Autowired
    private IOrderItemService orderItemService;

    /**
     * 查询订单详情（订单项）列表
     */
    @PreAuthorize("@ss.hasPermi('pet:resourceItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(OrderItem orderItem)
    {
        startPage();
        List<OrderItem> list = orderItemService.selectOrderItemList(orderItem);
        return getDataTable(list);
    }

    /**
     * 导出订单详情（订单项）列表
     */
    @PreAuthorize("@ss.hasPermi('pet:resourceItem:export')")
    @Log(title = "订单详情（订单项）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderItem orderItem)
    {
        List<OrderItem> list = orderItemService.selectOrderItemList(orderItem);
        ExcelUtil<OrderItem> util = new ExcelUtil<OrderItem>(OrderItem.class);
        util.exportExcel(response, list, "订单详情（订单项）数据");
    }

    /**
     * 获取订单详情（订单项）详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:resourceItem:query')")
    @GetMapping(value = "/{itemId}")
    public AjaxResult getInfo(@PathVariable("itemId") Long itemId)
    {
        return success(orderItemService.selectOrderItemByItemId(itemId));
    }

    /**
     * 新增订单详情（订单项）
     */
    @PreAuthorize("@ss.hasPermi('pet:resourceItem:add')")
    @Log(title = "订单详情（订单项）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OrderItem orderItem)
    {
        return toAjax(orderItemService.insertOrderItem(orderItem));
    }

    /**
     * 修改订单详情（订单项）
     */
    @PreAuthorize("@ss.hasPermi('pet:resourceItem:edit')")
    @Log(title = "订单详情（订单项）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OrderItem orderItem)
    {
        return toAjax(orderItemService.updateOrderItem(orderItem));
    }

    /**
     * 删除订单详情（订单项）
     */
    @PreAuthorize("@ss.hasPermi('pet:resourceItem:remove')")
    @Log(title = "订单详情（订单项）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{itemIds}")
    public AjaxResult remove(@PathVariable Long[] itemIds)
    {
        return toAjax(orderItemService.deleteOrderItemByItemIds(itemIds));
    }
}
