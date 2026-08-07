package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.LostPet;
import com.ruoyi.pet.service.ILostPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 宠物丢失Controller
 * 
 * @author ruoyi
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/pet/lostPet")
public class LostPetController extends BaseController
{
    @Autowired
    private ILostPetService lostPetService;

    /**
     * 查询宠物丢失列表
     */
    @PreAuthorize("@ss.hasPermi('pet:lostPet:list')")
    @GetMapping("/list")
    public TableDataInfo list(LostPet lostPet)
    {
        startPage();
        List<LostPet> list = lostPetService.selectLostPetList(lostPet);
        return getDataTable(list);
    }

    /**
     * 导出宠物丢失列表
     */
    @PreAuthorize("@ss.hasPermi('pet:lostPet:export')")
    @Log(title = "宠物丢失", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LostPet lostPet)
    {
        List<LostPet> list = lostPetService.selectLostPetList(lostPet);
        ExcelUtil<LostPet> util = new ExcelUtil<LostPet>(LostPet.class);
        util.exportExcel(response, list, "宠物丢失数据");
    }

    /**
     * 获取宠物丢失详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:lostPet:query')")
    @GetMapping(value = "/{lostId}")
    public AjaxResult getInfo(@PathVariable("lostId") Long lostId)
    {
        return success(lostPetService.selectLostPetByLostId(lostId));
    }

    /**
     * 新增宠物丢失
     */
    @PreAuthorize("@ss.hasPermi('pet:lostPet:add')")
    @Log(title = "宠物丢失", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LostPet lostPet)
    {
        return toAjax(lostPetService.insertLostPet(lostPet));
    }

    /**
     * 修改宠物丢失
     */
    @PreAuthorize("@ss.hasPermi('pet:lostPet:edit')")
    @Log(title = "宠物丢失", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LostPet lostPet)
    {
        return toAjax(lostPetService.updateLostPet(lostPet));
    }

    /**
     * 删除宠物丢失
     */
    @PreAuthorize("@ss.hasPermi('pet:lostPet:remove')")
    @Log(title = "宠物丢失", businessType = BusinessType.DELETE)
	@DeleteMapping("/{lostIds}")
    public AjaxResult remove(@PathVariable Long[] lostIds)
    {
        return toAjax(lostPetService.deleteLostPetByLostIds(lostIds));
    }
}
