package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.Rescue;
import com.ruoyi.pet.service.IRescueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 救助登记Controller
 * 
 * @author ruoyi
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/pet/rescue")
public class RescueController extends BaseController
{
    @Autowired
    private IRescueService rescueService;

    /**
     * 查询救助登记列表
     */
    //@PreAuthorize("@ss.hasPermi('pet:rescue:list')")
    @GetMapping("/list")
    public TableDataInfo list(Rescue rescue)
    {
        startPage();
        List<Rescue> list = rescueService.selectRescueList(rescue);
        return getDataTable(list);
    }

    /**
     * 导出救助登记列表
     */
    @PreAuthorize("@ss.hasPermi('pet:rescue:export')")
    @Log(title = "救助登记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Rescue rescue)
    {
        List<Rescue> list = rescueService.selectRescueList(rescue);
        ExcelUtil<Rescue> util = new ExcelUtil<Rescue>(Rescue.class);
        util.exportExcel(response, list, "救助登记数据");
    }

    /**
     * 获取救助登记详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:rescue:query')")
    @GetMapping(value = "/{rescueId}")
    public AjaxResult getInfo(@PathVariable("rescueId") Long rescueId)
    {
        return success(rescueService.selectRescueByRescueId(rescueId));
    }

    /**
     * 新增救助登记
     */
    @PreAuthorize("@ss.hasPermi('pet:rescue:add')")
    @Log(title = "救助登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Rescue rescue)
    {
        return toAjax(rescueService.insertRescue(rescue));
    }

    /**
     * 修改救助登记
     */
    @PreAuthorize("@ss.hasPermi('pet:rescue:edit')")
    @Log(title = "救助登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Rescue rescue)
    {
        return toAjax(rescueService.updateRescue(rescue));
    }

    /**
     * 删除救助登记
     */
    @PreAuthorize("@ss.hasPermi('pet:rescue:remove')")
    @Log(title = "救助登记", businessType = BusinessType.DELETE)
	@DeleteMapping("/{rescueIds}")
    public AjaxResult remove(@PathVariable Long[] rescueIds)
    {
        return toAjax(rescueService.deleteRescueByRescueIds(rescueIds));
    }
}
