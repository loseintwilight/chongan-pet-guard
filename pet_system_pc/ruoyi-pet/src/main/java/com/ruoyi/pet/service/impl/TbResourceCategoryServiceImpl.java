package com.ruoyi.pet.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.TbResourceCategoryMapper;
import com.ruoyi.pet.domain.TbResourceCategory;
import com.ruoyi.pet.service.ITbResourceCategoryService;

/**
 * 商品种类Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-06
 */
@Service
public class TbResourceCategoryServiceImpl implements ITbResourceCategoryService 
{
    @Autowired
    private TbResourceCategoryMapper tbResourceCategoryMapper;

    /**
     * 查询商品种类
     * 
     * @param categoryId 商品种类主键
     * @return 商品种类
     */
    @Override
    public TbResourceCategory selectTbResourceCategoryByCategoryId(Long categoryId)
    {
        return tbResourceCategoryMapper.selectTbResourceCategoryByCategoryId(categoryId);
    }

    /**
     * 查询商品种类列表
     * 
     * @param tbResourceCategory 商品种类
     * @return 商品种类
     */
    @Override
    public List<TbResourceCategory> selectTbResourceCategoryList(TbResourceCategory tbResourceCategory)
    {
        return tbResourceCategoryMapper.selectTbResourceCategoryList(tbResourceCategory);
    }

    /**
     * 新增商品种类
     * 
     * @param tbResourceCategory 商品种类
     * @return 结果
     */
    @Override
    public int insertTbResourceCategory(TbResourceCategory tbResourceCategory)
    {
        tbResourceCategory.setCreateTime(DateUtils.getNowDate());
        return tbResourceCategoryMapper.insertTbResourceCategory(tbResourceCategory);
    }

    /**
     * 修改商品种类
     * 
     * @param tbResourceCategory 商品种类
     * @return 结果
     */
    @Override
    public int updateTbResourceCategory(TbResourceCategory tbResourceCategory)
    {
        tbResourceCategory.setUpdateTime(DateUtils.getNowDate());
        return tbResourceCategoryMapper.updateTbResourceCategory(tbResourceCategory);
    }

    /**
     * 批量删除商品种类
     * 
     * @param categoryIds 需要删除的商品种类主键
     * @return 结果
     */
    @Override
    public int deleteTbResourceCategoryByCategoryIds(Long[] categoryIds)
    {
        return tbResourceCategoryMapper.deleteTbResourceCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除商品种类信息
     * 
     * @param categoryId 商品种类主键
     * @return 结果
     */
    @Override
    public int deleteTbResourceCategoryByCategoryId(Long categoryId)
    {
        return tbResourceCategoryMapper.deleteTbResourceCategoryByCategoryId(categoryId);
    }
}
