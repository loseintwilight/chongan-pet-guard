package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.pet.domain.Shop;
import com.ruoyi.pet.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.PetResourceMapper;
import com.ruoyi.pet.domain.PetResource;
import com.ruoyi.pet.service.IPetResourceService;

/**
 * 商品Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-08
 */
@Service
public class PetResourceServiceImpl implements IPetResourceService {
    @Autowired
    private PetResourceMapper petResourceMapper;

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 查询商品
     *
     * @param resourceId 商品主键
     * @return 商品
     */
    @Override
    public PetResource selectPetResourceByResourceId(Long resourceId) {
        return petResourceMapper.selectPetResourceByResourceId(resourceId);
    }

    /**
     * 查询商品列表
     *
     * @param petResource 商品
     * @return 商品
     */
    @Override
    public List<PetResource> selectPetResourceList(PetResource petResource) {
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (openId == null) {
            //PC端用户
            //判断是否为管理员角色
            boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
            if (!isAdmin) {
                //非管理员用户，只能查看自己店铺的商品
                Shop shop = shopMapper.selectShopByUserId(SecurityUtils.getUserId());
                if (null != shop) {
                    petResource.setShopId(shop.getShopId());
                } else {
                    //如果没有店铺，则不返回任何数据
                    petResource.setShopId(-1L); //设置一个不可能存在的店铺ID
                }
            }
            //管理员用户可以看到所有宠物，不需要设置deptId限制
        }
        return petResourceMapper.selectPetResourceList(petResource);
    }

    /**
     * 新增商品
     *
     * @param petResource 商品
     * @return 结果
     */
    @Override
    public int insertPetResource(PetResource petResource) {
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (openId == null) {
            //PC端用户
            //判断是否为管理员角色
            boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
            if (!isAdmin) {
                //非管理员用户，只能查看自己店铺的商品
                Shop shop = shopMapper.selectShopByUserId(SecurityUtils.getUserId());
                if (null != shop) {
                    petResource.setShopId(shop.getShopId());
                } else {
                    //如果没有店铺，则不返回任何数据
                    petResource.setShopId(-1L); //设置一个不可能存在的店铺ID
                }
            }
            //管理员用户可以看到所有宠物，不需要设置deptId限制
        }
        petResource.setCreateBy(SecurityUtils.getUsername());
        petResource.setCreateTime(DateUtils.getNowDate());
        return petResourceMapper.insertPetResource(petResource);
    }

    /**
     * 修改商品
     *
     * @param petResource 商品
     * @return 结果
     */
    @Override
    public int updatePetResource(PetResource petResource) {
        petResource.setUpdateTime(DateUtils.getNowDate());
        return petResourceMapper.updatePetResource(petResource);
    }

    /**
     * 批量删除商品
     *
     * @param resourceIds 需要删除的商品主键
     * @return 结果
     */
    @Override
    public int deletePetResourceByResourceIds(Long[] resourceIds) {
        return petResourceMapper.updateDeletePetResourceByResourceIds(resourceIds);
    }
//    @Override
//    public int deletePetResourceByResourceIds(Long[] resourceIds) {
//        return petResourceMapper.deletePetResourceByResourceIds(resourceIds);
//    }

    /**
     * 删除商品信息
     *
     * @param resourceId 商品主键
     * @return 结果
     */
    @Override
    public int deletePetResourceByResourceId(Long resourceId) {
        return petResourceMapper.deletePetResourceByResourceId(resourceId);
    }
}
