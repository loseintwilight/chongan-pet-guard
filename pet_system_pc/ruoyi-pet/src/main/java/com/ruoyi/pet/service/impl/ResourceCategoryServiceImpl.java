package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.pet.domain.Shop;
import com.ruoyi.pet.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.ResourceCategoryMapper;
import com.ruoyi.pet.domain.ResourceCategory;
import com.ruoyi.pet.service.IResourceCategoryService;

/**
 * 商品种类Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-09
 */
@Service
public class ResourceCategoryServiceImpl implements IResourceCategoryService {
    @Autowired
    private ResourceCategoryMapper resourceCategoryMapper;

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 查询商品种类
     *
     * @param categoryId 商品种类主键
     * @return 商品种类
     */
    @Override
    public ResourceCategory selectResourceCategoryByCategoryId(Long categoryId) {
        return resourceCategoryMapper.selectResourceCategoryByCategoryId(categoryId);
    }

    /**
     * 查询商品种类列表
     *
     * @param resourceCategory 商品种类
     * @return 商品种类
     */
    @Override
    public List<ResourceCategory> selectResourceCategoryList(ResourceCategory resourceCategory) {
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (openId == null) {
            //PC端用户
            //判断是否为管理员角色
            boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
            if (!isAdmin) {
                //非管理员用户，只能查看自己店铺的商品
                Shop shop = shopMapper.selectShopByUserId(SecurityUtils.getUserId());
                if (null != shop) {
                    resourceCategory.setShopId(shop.getShopId());
                } else {
                    //如果没有店铺，则不返回任何数据
                    resourceCategory.setShopId(-1L); //设置一个不可能存在的店铺ID
                }
            }
            //管理员用户可以看到所有宠物，不需要设置deptId限制
        }
        return resourceCategoryMapper.selectResourceCategoryList(resourceCategory);
    }

    /**
     * 新增商品种类
     *
     * @param resourceCategory 商品种类
     * @return 结果
     */
    @Override
    public int insertResourceCategory(ResourceCategory resourceCategory) {
        resourceCategory.setCreateTime(DateUtils.getNowDate());
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (openId == null) {
            //PC端用户
            //判断是否为管理员角色
            boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
            if (!isAdmin) {
                //非管理员用户，只能查看自己店铺的商品
                Shop shop = shopMapper.selectShopByUserId(SecurityUtils.getUserId());
                if (null != shop) {
                    resourceCategory.setShopId(shop.getShopId());
                } else {
                    //如果没有店铺，则不返回任何数据
                    resourceCategory.setShopId(-1L); //设置一个不可能存在的店铺ID
                }
            }
            //管理员用户可以看到所有宠物，不需要设置deptId限制
        }
        return resourceCategoryMapper.insertResourceCategory(resourceCategory);
    }

    /**
     * 修改商品种类
     *
     * @param resourceCategory 商品种类
     * @return 结果
     */
    @Override
    public int updateResourceCategory(ResourceCategory resourceCategory) {
        resourceCategory.setUpdateTime(DateUtils.getNowDate());
        return resourceCategoryMapper.updateResourceCategory(resourceCategory);
    }

    /**
     * 批量删除商品种类
     *
     * @param categoryIds 需要删除的商品种类主键
     * @return 结果
     */
    @Override
    public int deleteResourceCategoryByCategoryIds(Long[] categoryIds) {
        return resourceCategoryMapper.deleteResourceCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除商品种类信息
     *
     * @param categoryId 商品种类主键
     * @return 结果
     */
    @Override
    public int deleteResourceCategoryByCategoryId(Long categoryId) {
        return resourceCategoryMapper.deleteResourceCategoryByCategoryId(categoryId);
    }
}
