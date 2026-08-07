package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.LostPet;

/**
 * 宠物丢失Service接口
 * 
 * @author ruoyi
 * @date 2025-12-06
 */
public interface ILostPetService 
{
    /**
     * 查询宠物丢失
     * 
     * @param lostId 宠物丢失主键
     * @return 宠物丢失
     */
    public LostPet selectLostPetByLostId(Long lostId);

    /**
     * 查询宠物丢失列表
     * 
     * @param lostPet 宠物丢失
     * @return 宠物丢失集合
     */
    public List<LostPet> selectLostPetList(LostPet lostPet);

    /**
     * 新增宠物丢失
     * 
     * @param lostPet 宠物丢失
     * @return 结果
     */
    public int insertLostPet(LostPet lostPet);

    /**
     * 修改宠物丢失
     * 
     * @param lostPet 宠物丢失
     * @return 结果
     */
    public int updateLostPet(LostPet lostPet);

    /**
     * 批量删除宠物丢失
     * 
     * @param lostIds 需要删除的宠物丢失主键集合
     * @return 结果
     */
    public int deleteLostPetByLostIds(Long[] lostIds);

    /**
     * 删除宠物丢失信息
     * 
     * @param lostId 宠物丢失主键
     * @return 结果
     */
    public int deleteLostPetByLostId(Long lostId);
}
