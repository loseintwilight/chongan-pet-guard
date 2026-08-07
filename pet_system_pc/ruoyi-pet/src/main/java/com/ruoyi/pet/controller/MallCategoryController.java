package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pet.domain.ResourceCategory;
import com.ruoyi.pet.mapper.ResourceCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Anonymous
@RestController
@RequestMapping("/mall/category")
public class MallCategoryController extends BaseController
{
    @Autowired
    private ResourceCategoryMapper resourceCategoryMapper;

    @GetMapping("/list")
    public AjaxResult list()
    {
        ResourceCategory query = new ResourceCategory();
        query.setStatus(0L);
        List<ResourceCategory> list = resourceCategoryMapper.selectResourceCategoryList(query);
        return success(list);
    }

    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(resourceCategoryMapper.selectResourceCategoryByCategoryId(categoryId));
    }
}
