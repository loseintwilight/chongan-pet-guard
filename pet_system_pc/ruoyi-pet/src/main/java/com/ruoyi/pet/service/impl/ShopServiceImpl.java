package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.ShopMapper;
import com.ruoyi.pet.domain.Shop;
import com.ruoyi.pet.service.IShopService;

/**
 * 店铺，站点Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-04
 */
@Service
public class ShopServiceImpl implements IShopService {
    @Autowired
    private ShopMapper shopMapper;

    /**
     * 查询店铺，站点
     *
     * @param shopId 店铺，站点主键
     * @return 店铺，站点
     */
    @Override
    public Shop selectShopByShopId(Long shopId) {
        return shopMapper.selectShopByShopId(shopId);
    }

    /**
     * 查询店铺，站点列表
     *
     * @param shop 店铺，站点
     * @return 店铺，站点
     */
    @Override
    public List<Shop> selectShopList(Shop shop) {
        return shopMapper.selectShopList(shop);
    }

    /**
     * 新增店铺，站点
     *
     * @param shop 店铺，站点
     * @return 结果
     */
    @Override
    public int insertShop(Shop shop) {
        shop.setUserId(SecurityUtils.getUserId());
        shop.setOwnerPhone(SecurityUtils.getLoginUser().getUser().getPhonenumber());
        shop.setStatus(2L);
        shop.setCreareBy(SecurityUtils.getUsername());
        shop.setCreateTime(DateUtils.getNowDate());
        return shopMapper.insertShop(shop);
    }

    /**
     * 修改店铺，站点
     *
     * @param shop 店铺，站点
     * @return 结果
     */
    @Override
    public int updateShop(Shop shop) {
        shop.setUpdateTime(DateUtils.getNowDate());
        return shopMapper.updateShop(shop);
    }

    /**
     * 批量删除店铺，站点
     *
     * @param shopIds 需要删除的店铺，站点主键
     * @return 结果
     */
//    @Override
//    public int deleteShopByShopIds(Long[] shopIds)
//    {
//        return shopMapper.deleteShopByShopIds(shopIds);
//    }

    /**
     * 批量删除店铺，站点
     *
     * @param shopIds 需要删除的店铺，站点主键
     * @return 结果
     */
    @Override
    public int deleteShopByShopIds(Long[] shopIds) {
        return shopMapper.updateDeleteShopByShopIds(shopIds);
    }

    /**
     * 删除店铺，站点信息
     *
     * @param shopId 店铺，站点主键
     * @return 结果
     */
    @Override
    public int deleteShopByShopId(Long shopId) {
        return shopMapper.deleteShopByShopId(shopId);
    }
}
