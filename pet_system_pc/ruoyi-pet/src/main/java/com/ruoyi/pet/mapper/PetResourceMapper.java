package com.ruoyi.pet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.pet.domain.PetResource;

/**
 * 商品Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
public interface PetResourceMapper 
{
    /**
     * 查询商品
     * 
     * @param resourceId 商品主键
     * @return 商品
     */
    public PetResource selectPetResourceByResourceId(Long resourceId);

    /**
     * 查询商品列表
     * 
     * @param petResource 商品
     * @return 商品集合
     */
    public List<PetResource> selectPetResourceList(PetResource petResource);

    /**
     * 新增商品
     * 
     * @param petResource 商品
     * @return 结果
     */
    public int insertPetResource(PetResource petResource);

    /**
     * 修改商品
     * 
     * @param petResource 商品
     * @return 结果
     */
    public int updatePetResource(PetResource petResource);

    /**
     * 删除商品
     * 
     * @param resourceId 商品主键
     * @return 结果
     */
    public int deletePetResourceByResourceId(Long resourceId);

    /**
     * 批量删除商品
     * 
     * @param resourceIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePetResourceByResourceIds(Long[] resourceIds);

    int updateDeletePetResourceByResourceIds(Long[] resourceIds);

    public List<PetResource> selectPetResourceListByShopHot(@Param("shopId") Long shopId, @Param("limit") int limit);
}
