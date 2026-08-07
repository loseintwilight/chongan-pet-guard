package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.LostPetMapper;
import com.ruoyi.pet.domain.LostPet;
import com.ruoyi.pet.service.ILostPetService;

/**
 * 宠物丢失Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-06
 */
@Service
public class LostPetServiceImpl implements ILostPetService {
    @Autowired
    private LostPetMapper lostPetMapper;

    /**
     * 查询宠物丢失
     *
     * @param lostId 宠物丢失主键
     * @return 宠物丢失
     */
    @Override
    public LostPet selectLostPetByLostId(Long lostId) {
        return lostPetMapper.selectLostPetByLostId(lostId);
    }

    /**
     * 查询宠物丢失列表
     *
     * @param lostPet 宠物丢失
     * @return 宠物丢失
     */
    @Override
    public List<LostPet> selectLostPetList(LostPet lostPet) {
        return lostPetMapper.selectLostPetList(lostPet);
    }

    /**
     * 新增宠物丢失
     *
     * @param lostPet 宠物丢失
     * @return 结果
     */
    @Override
    public int insertLostPet(LostPet lostPet) {
        lostPet.setUserId(SecurityUtils.getUserId());
        lostPet.setCreateBy(SecurityUtils.getUsername());
        lostPet.setCreateTime(DateUtils.getNowDate());
        return lostPetMapper.insertLostPet(lostPet);
    }

    /**
     * 修改宠物丢失
     *
     * @param lostPet 宠物丢失
     * @return 结果
     */
    @Override
    public int updateLostPet(LostPet lostPet) {
        lostPet.setUpdateTime(DateUtils.getNowDate());
        return lostPetMapper.updateLostPet(lostPet);
    }

    /**
     * 批量删除宠物丢失
     *
     * @param lostIds 需要删除的宠物丢失主键
     * @return 结果
     */
//    @Override
//    public int deleteLostPetByLostIds(Long[] lostIds)
//    {
//        return lostPetMapper.deleteLostPetByLostIds(lostIds);
//    }

    /**
     * 批量删除宠物丢失
     *
     * @param lostIds 需要删除的宠物丢失主键
     * @return 结果
     */
    @Override
    public int deleteLostPetByLostIds(Long[] lostIds) {
        return lostPetMapper.updateDeleteLostPetByLostIds(lostIds);
    }

    /**
     * 删除宠物丢失信息
     *
     * @param lostId 宠物丢失主键
     * @return 结果
     */
    @Override
    public int deleteLostPetByLostId(Long lostId) {
        return lostPetMapper.deleteLostPetByLostId(lostId);
    }
}
