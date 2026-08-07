package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.TbActivity;
import com.ruoyi.pet.service.ITbActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 活动Controller
 *
 * @author ruoyi
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/pet/activity")
public class TbActivityController extends BaseController {
    @Autowired
    private ITbActivityService tbActivityService;

    /**
     * 查询活动列表
     */
    @PreAuthorize("@ss.hasPermi('pet:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(TbActivity tbActivity) {
        startPage();
        List<TbActivity> list = tbActivityService.selectTbActivityList(tbActivity);
        return getDataTable(list);
    }

    /**
     * 查询活动列表
     */
    @PreAuthorize("@ss.hasPermi('pet:activity:list')")
    @GetMapping("/wxList")
    public TableDataInfo listByUserId(TbActivity tbActivity) {
        startPage();
        List<TbActivity> list = tbActivityService.selectWxTbActivityList(tbActivity);
        return getDataTable(list);
    }

    /**
     * 导出活动列表
     */
    @PreAuthorize("@ss.hasPermi('pet:activity:export')")
    @Log(title = "活动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TbActivity tbActivity) {
        List<TbActivity> list = tbActivityService.selectTbActivityList(tbActivity);
        ExcelUtil<TbActivity> util = new ExcelUtil<TbActivity>(TbActivity.class);
        util.exportExcel(response, list, "活动数据");
    }

    /**
     * 获取活动详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:activity:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Long activityId) {
        return success(tbActivityService.selectTbActivityByActivityId(activityId));
    }

    /**
     * 新增活动
     */
    @PreAuthorize("@ss.hasPermi('pet:activity:add')")
    @Log(title = "活动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TbActivity tbActivity) {
        return toAjax(tbActivityService.insertTbActivity(tbActivity));
    }

    /**
     * 修改活动
     */
    @PreAuthorize("@ss.hasPermi('pet:activity:edit')")
    @Log(title = "活动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TbActivity tbActivity) {
        return toAjax(tbActivityService.updateTbActivity(tbActivity));
    }

    /**
     * 删除活动
     */
    @PreAuthorize("@ss.hasPermi('pet:activity:remove')")
    @Log(title = "活动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Long[] activityIds) {
        return toAjax(tbActivityService.deleteTbActivityByActivityIds(activityIds));
    }
}
