package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.Shop;

/**
 * 店铺，站点Service接口
 * 
 * @author ruoyi
 * @date 2025-12-04
 */
public interface IShopService 
{
    /**
     * 查询店铺，站点
     * 
     * @param shopId 店铺，站点主键
     * @return 店铺，站点
     */
    public Shop selectShopByShopId(Long shopId);

    /**
     * 查询店铺，站点列表
     * 
     * @param shop 店铺，站点
     * @return 店铺，站点集合
     */
    public List<Shop> selectShopList(Shop shop);

    /**
     * 新增店铺，站点
     * 
     * @param shop 店铺，站点
     * @return 结果
     */
    public int insertShop(Shop shop);

    /**
     * 修改店铺，站点
     * 
     * @param shop 店铺，站点
     * @return 结果
     */
    public int updateShop(Shop shop);

    /**
     * 批量删除店铺，站点
     * 
     * @param shopIds 需要删除的店铺，站点主键集合
     * @return 结果
     */
    public int deleteShopByShopIds(Long[] shopIds);

    /**
     * 删除店铺，站点信息
     * 
     * @param shopId 店铺，站点主键
     * @return 结果
     */
    public int deleteShopByShopId(Long shopId);
}
