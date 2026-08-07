package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.pet.domain.PetResource;
import com.ruoyi.pet.mapper.PetResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Anonymous
@RestController
@RequestMapping("/mall/product")
public class MallProductController extends BaseController
{
    @Autowired
    private PetResourceMapper petResourceMapper;

    @GetMapping("/list")
    public TableDataInfo list(PetResource petResource)
    {
        startPage();
        petResource.setIsDeleted(0L);
        petResource.setStatus(0L);
        List<PetResource> list = petResourceMapper.selectPetResourceList(petResource);
        return getDataTable(list);
    }

    @GetMapping(value = "/{resourceId}")
    public AjaxResult getInfo(@PathVariable("resourceId") Long resourceId)
    {
        return success(petResourceMapper.selectPetResourceByResourceId(resourceId));
    }

    @GetMapping("/listByShop")
    public TableDataInfo listByShop(@RequestParam("shopId") Long shopId, PetResource query)
    {
        startPage();
        query.setShopId(shopId);
        query.setIsDeleted(0L);
        query.setStatus(0L);
        List<PetResource> list = petResourceMapper.selectPetResourceList(query);
        return getDataTable(list);
    }

    @GetMapping("/listByShopHot")
    public AjaxResult listByShopHot(@RequestParam("shopId") Long shopId, @RequestParam(value = "limit", defaultValue = "4") int limit)
    {
        List<PetResource> list = petResourceMapper.selectPetResourceListByShopHot(shopId, limit);
        return success(list);
    }
}
