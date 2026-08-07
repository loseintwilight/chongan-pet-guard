package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.ResourceCategory;
import com.ruoyi.pet.service.IResourceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商品种类Controller
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
@RestController
@RequestMapping("/pet/category")
public class ResourceCategoryController extends BaseController
{
    @Autowired
    private IResourceCategoryService resourceCategoryService;

    /**
     * 查询商品种类列表
     */
    @PreAuthorize("@ss.hasPermi('pet:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(ResourceCategory resourceCategory)
    {
        startPage();
        List<ResourceCategory> list = resourceCategoryService.selectResourceCategoryList(resourceCategory);
        return getDataTable(list);
    }

    /**
     * 导出商品种类列表
     */
    @PreAuthorize("@ss.hasPermi('pet:category:export')")
    @Log(title = "商品种类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ResourceCategory resourceCategory)
    {
        List<ResourceCategory> list = resourceCategoryService.selectResourceCategoryList(resourceCategory);
        ExcelUtil<ResourceCategory> util = new ExcelUtil<ResourceCategory>(ResourceCategory.class);
        util.exportExcel(response, list, "商品种类数据");
    }

    /**
     * 获取商品种类详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:category:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(resourceCategoryService.selectResourceCategoryByCategoryId(categoryId));
    }

    /**
     * 新增商品种类
     */
    @PreAuthorize("@ss.hasPermi('pet:category:add')")
    @Log(title = "商品种类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ResourceCategory resourceCategory)
    {
        return toAjax(resourceCategoryService.insertResourceCategory(resourceCategory));
    }

    /**
     * 修改商品种类
     */
    @PreAuthorize("@ss.hasPermi('pet:category:edit')")
    @Log(title = "商品种类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ResourceCategory resourceCategory)
    {
        return toAjax(resourceCategoryService.updateResourceCategory(resourceCategory));
    }

    /**
     * 删除商品种类
     */
    @PreAuthorize("@ss.hasPermi('pet:category:remove')")
    @Log(title = "商品种类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(resourceCategoryService.deleteResourceCategoryByCategoryIds(categoryIds));
    }
}
