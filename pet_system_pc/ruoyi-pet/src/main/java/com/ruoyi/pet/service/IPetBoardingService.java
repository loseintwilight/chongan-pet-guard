package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.PetBoarding;

/**
 * 宠物托养Service接口
 * 
 * @author ruoyi
 * @date 2025-12-04
 */
public interface IPetBoardingService 
{
    /**
     * 查询宠物托养
     * 
     * @param boardingId 宠物托养主键
     * @return 宠物托养
     */
    public PetBoarding selectPetBoardingByBoardingId(Long boardingId);

    /**
     * 查询宠物托养列表
     * 
     * @param petBoarding 宠物托养
     * @return 宠物托养集合
     */
    public List<PetBoarding> selectPetBoardingList(PetBoarding petBoarding);

    /**
     * 新增宠物托养
     * 
     * @param petBoarding 宠物托养
     * @return 结果
     */
    public int insertPetBoarding(PetBoarding petBoarding);

    /**
     * 修改宠物托养
     * 
     * @param petBoarding 宠物托养
     * @return 结果
     */
    public int updatePetBoarding(PetBoarding petBoarding);

    /**
     * 批量删除宠物托养
     * 
     * @param boardingIds 需要删除的宠物托养主键集合
     * @return 结果
     */
    public int deletePetBoardingByBoardingIds(Long[] boardingIds);

    /**
     * 删除宠物托养信息
     * 
     * @param boardingId 宠物托养主键
     * @return 结果
     */
    public int deletePetBoardingByBoardingId(Long boardingId);
}
