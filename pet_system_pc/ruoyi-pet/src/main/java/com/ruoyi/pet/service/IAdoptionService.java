package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.Adoption;

/**
 * 宠物领养Service接口
 * 
 * @author ruoyi
 * @date 2025-12-07
 */
public interface IAdoptionService 
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
     * 批量删除宠物领养
     * 
     * @param adoptionIds 需要删除的宠物领养主键集合
     * @return 结果
     */
    public int deleteAdoptionByAdoptionIds(Long[] adoptionIds);

    /**
     * 删除宠物领养信息
     * 
     * @param adoptionId 宠物领养主键
     * @return 结果
     */
    public int deleteAdoptionByAdoptionId(Long adoptionId);

    public List<Adoption> selectMyAppliedPets(Long userId, Integer status);
}
