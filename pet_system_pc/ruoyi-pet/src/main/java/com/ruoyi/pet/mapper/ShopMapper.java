package com.ruoyi.pet.mapper;

import java.util.List;

import com.ruoyi.pet.domain.Shop;
import org.apache.ibatis.annotations.Param;

/**
 * 店铺，站点Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-04
 */
public interface ShopMapper {
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
     * 删除店铺，站点
     *
     * @param shopId 店铺，站点主键
     * @return 结果
     */
    public int deleteShopByShopId(Long shopId);

    /**
     * 批量删除店铺，站点
     *
     * @param shopIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopByShopIds(Long[] shopIds);

    Shop selectShopByUserId(@Param("userId") Long userId);

    int updateDeleteShopByShopIds(Long[] shopIds);

}
