package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.ResourceCategory;

/**
 * 商品种类Service接口
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
public interface IResourceCategoryService 
{
    /**
     * 查询商品种类
     * 
     * @param categoryId 商品种类主键
     * @return 商品种类
     */
    public ResourceCategory selectResourceCategoryByCategoryId(Long categoryId);

    /**
     * 查询商品种类列表
     * 
     * @param resourceCategory 商品种类
     * @return 商品种类集合
     */
    public List<ResourceCategory> selectResourceCategoryList(ResourceCategory resourceCategory);

    /**
     * 新增商品种类
     * 
     * @param resourceCategory 商品种类
     * @return 结果
     */
    public int insertResourceCategory(ResourceCategory resourceCategory);

    /**
     * 修改商品种类
     * 
     * @param resourceCategory 商品种类
     * @return 结果
     */
    public int updateResourceCategory(ResourceCategory resourceCategory);

    /**
     * 批量删除商品种类
     * 
     * @param categoryIds 需要删除的商品种类主键集合
     * @return 结果
     */
    public int deleteResourceCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 删除商品种类信息
     * 
     * @param categoryId 商品种类主键
     * @return 结果
     */
    public int deleteResourceCategoryByCategoryId(Long categoryId);
}
