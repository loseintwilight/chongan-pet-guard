package com.ruoyi.pet.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.pet.domain.Pet;
import com.ruoyi.pet.domain.Shop;

/**
 * 宠物信息Service接口
 *
 * @author ruoyi
 * @date 2025-12-03
 */
public interface IPetService {
    /**
     * 查询宠物信息
     *
     * @param petId 宠物信息主键
     * @return 宠物信息
     */
    public Pet selectPetByPetId(Long petId);

    /**
     * 查询宠物信息列表
     *
     * @param pet 宠物信息
     * @return 宠物信息集合
     */
    public List<Pet> selectPetList(Pet pet);

    /**
     * 根据宠物表的店铺id字段查询店铺表的店铺名称
     *
     * @return
     */
    public List<Shop> selectPetShop();

    /**
     * 新增宠物信息
     *
     * @param pet 宠物信息
     * @return 结果
     */
    public int insertPet(Pet pet);

    /**
     * 修改宠物信息
     *
     * @param pet 宠物信息
     * @return 结果
     */
    public int updatePet(Pet pet);

    /**
     * 批量删除宠物信息
     *
     * @param petIds 需要删除的宠物信息主键集合
     * @return 结果
     */
    public int deletePetByPetIds(Long[] petIds);

    /**
     * 删除宠物信息信息
     *
     * @param petId 宠物信息主键
     * @return 结果
     */
    public int deletePetByPetId(Long petId);

    List<Pet> selectCollectByUser(Pet pet);

    List<Map<String, Object>> getPetTypeNumber();
}
