package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.PetResource;
import com.ruoyi.pet.service.IPetResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商品Controller
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
@RestController
@RequestMapping("/pet/resource")
public class PetResourceController extends BaseController
{
    @Autowired
    private IPetResourceService petResourceService;

    /**
     * 查询商品列表
     */
    @PreAuthorize("@ss.hasPermi('pet:resource:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetResource petResource)
    {
        startPage();
        List<PetResource> list = petResourceService.selectPetResourceList(petResource);
        return getDataTable(list);
    }

    /**
     * 导出商品列表
     */
    @PreAuthorize("@ss.hasPermi('pet:resource:export')")
    @Log(title = "商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetResource petResource)
    {
        List<PetResource> list = petResourceService.selectPetResourceList(petResource);
        ExcelUtil<PetResource> util = new ExcelUtil<PetResource>(PetResource.class);
        util.exportExcel(response, list, "商品数据");
    }

    /**
     * 获取商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:resource:query')")
    @GetMapping(value = "/{resourceId}")
    public AjaxResult getInfo(@PathVariable("resourceId") Long resourceId)
    {
        return success(petResourceService.selectPetResourceByResourceId(resourceId));
    }

    /**
     * 新增商品
     */
    @PreAuthorize("@ss.hasPermi('pet:resource:add')")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetResource petResource)
    {
        return toAjax(petResourceService.insertPetResource(petResource));
    }

    /**
     * 修改商品
     */
    @PreAuthorize("@ss.hasPermi('pet:resource:edit')")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetResource petResource)
    {
        return toAjax(petResourceService.updatePetResource(petResource));
    }

    /**
     * 删除商品
     */
    @PreAuthorize("@ss.hasPermi('pet:resource:remove')")
    @Log(title = "商品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{resourceIds}")
    public AjaxResult remove(@PathVariable Long[] resourceIds)
    {
        return toAjax(petResourceService.deletePetResourceByResourceIds(resourceIds));
    }
}
