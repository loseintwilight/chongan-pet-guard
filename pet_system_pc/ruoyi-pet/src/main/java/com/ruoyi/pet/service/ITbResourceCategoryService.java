package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.TbResourceCategory;

/**
 * 商品种类Service接口
 * 
 * @author ruoyi
 * @date 2025-12-06
 */
public interface ITbResourceCategoryService 
{
    /**
     * 查询商品种类
     * 
     * @param categoryId 商品种类主键
     * @return 商品种类
     */
    public TbResourceCategory selectTbResourceCategoryByCategoryId(Long categoryId);

    /**
     * 查询商品种类列表
     * 
     * @param tbResourceCategory 商品种类
     * @return 商品种类集合
     */
    public List<TbResourceCategory> selectTbResourceCategoryList(TbResourceCategory tbResourceCategory);

    /**
     * 新增商品种类
     * 
     * @param tbResourceCategory 商品种类
     * @return 结果
     */
    public int insertTbResourceCategory(TbResourceCategory tbResourceCategory);

    /**
     * 修改商品种类
     * 
     * @param tbResourceCategory 商品种类
     * @return 结果
     */
    public int updateTbResourceCategory(TbResourceCategory tbResourceCategory);

    /**
     * 批量删除商品种类
     * 
     * @param categoryIds 需要删除的商品种类主键集合
     * @return 结果
     */
    public int deleteTbResourceCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 删除商品种类信息
     * 
     * @param categoryId 商品种类主键
     * @return 结果
     */
    public int deleteTbResourceCategoryByCategoryId(Long categoryId);
}
