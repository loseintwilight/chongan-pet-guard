package com.ruoyi.pet.mapper;

import java.util.List;
import com.ruoyi.pet.domain.Adoption;
import org.apache.ibatis.annotations.Param;

/**
 * 宠物领养Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-07
 */
public interface AdoptionMapper 
{
    /**
     * 查询宠物领养
     * 
     * @param adoptionId 宠物领养主键
     * @return 宠物领养
     */
    public Adoption selectAdoptionByAdoptionId(Long adoptionId);

    /**
     * 查询宠物领养列表
     * 
     * @param adoption 宠物领养
     * @return 宠物领养集合
     */
    public List<Adoption> selectAdoptionList(Adoption adoption);

    /**
     * 新增宠物领养
     * 
     * @param adoption 宠物领养
     * @return 结果
     */
    public int insertAdoption(Adoption adoption);

    /**
     * 修改宠物领养
     * 
     * @param adoption 宠物领养
     * @return 结果
     */
    public int updateAdoption(Adoption adoption);

    /**
     * 删除宠物领养
     * 
     * @param adoptionId 宠物领养主键
     * @return 结果
     */
    public int deleteAdoptionByAdoptionId(Long adoptionId);

    /**
     * 批量删除宠物领养
     * 
     * @param adoptionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAdoptionByAdoptionIds(Long[] adoptionIds);

    int updateDeleteAdoptionByAdoptionIds(Long[] adoptionIds);

    /**
     * 查询用户已申请领养的宠物列表
     *
     * @param userId 用户ID
     * @param status 领养申请状态筛选
     * @return 宠物列表
     */
    public List<Adoption> selectMyAppliedPets(
            @Param("userId") Long userId,
            @Param("status") Integer status);

}
