package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.PetBoarding;
import com.ruoyi.pet.service.IPetBoardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 宠物托养Controller
 * 
 * @author ruoyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/pet/boarding")
public class PetBoardingController extends BaseController
{
    @Autowired
    private IPetBoardingService petBoardingService;

    /**
     * 查询宠物托养列表
     */
    @PreAuthorize("@ss.hasPermi('pet:boarding:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBoarding petBoarding)
    {
        startPage();
        List<PetBoarding> list = petBoardingService.selectPetBoardingList(petBoarding);
        return getDataTable(list);
    }

    /**
     * 导出宠物托养列表
     */
    @PreAuthorize("@ss.hasPermi('pet:boarding:export')")
    @Log(title = "宠物托养", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBoarding petBoarding)
    {
        List<PetBoarding> list = petBoardingService.selectPetBoardingList(petBoarding);
        ExcelUtil<PetBoarding> util = new ExcelUtil<PetBoarding>(PetBoarding.class);
        util.exportExcel(response, list, "宠物托养数据");
    }

    /**
     * 获取宠物托养详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:boarding:query')")
    @GetMapping(value = "/{boardingId}")
    public AjaxResult getInfo(@PathVariable("boardingId") Long boardingId)
    {
        return success(petBoardingService.selectPetBoardingByBoardingId(boardingId));
    }


    /**
     * 新增宠物托养
     */
    @PreAuthorize("@ss.hasPermi('pet:boarding:add')")
    @Log(title = "宠物托养", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBoarding petBoarding)
    {
        return toAjax(petBoardingService.insertPetBoarding(petBoarding));
    }

    /**
     * 修改宠物托养
     */
    @PreAuthorize("@ss.hasPermi('pet:boarding:edit')")
    @Log(title = "宠物托养", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBoarding petBoarding)
    {
        return toAjax(petBoardingService.updatePetBoarding(petBoarding));
    }

    /**
     * 删除宠物托养
     */
    @PreAuthorize("@ss.hasPermi('pet:boarding:remove')")
    @Log(title = "宠物托养", businessType = BusinessType.DELETE)
	@DeleteMapping("/{boardingIds}")
    public AjaxResult remove(@PathVariable Long[] boardingIds)
    {
        return toAjax(petBoardingService.deletePetBoardingByBoardingIds(boardingIds));
    }
}
