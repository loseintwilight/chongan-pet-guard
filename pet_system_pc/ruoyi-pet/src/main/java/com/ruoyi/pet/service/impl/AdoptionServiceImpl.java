package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.AdoptionMapper;
import com.ruoyi.pet.domain.Adoption;
import com.ruoyi.pet.service.IAdoptionService;

/**
 * 宠物领养Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-07
 */
@Service
public class AdoptionServiceImpl implements IAdoptionService {
    @Autowired
    private AdoptionMapper adoptionMapper;

    /**
     * 查询宠物领养
     *
     * @param adoptionId 宠物领养主键
     * @return 宠物领养
     */
    @Override
    public Adoption selectAdoptionByAdoptionId(Long adoptionId) {
        return adoptionMapper.selectAdoptionByAdoptionId(adoptionId);
    }

    /**
     * 查询宠物领养列表
     *
     * @param adoption 宠物领养
     * @return 宠物领养
     */
    @Override
    public List<Adoption> selectAdoptionList(Adoption adoption) {
        return adoptionMapper.selectAdoptionList(adoption);
    }

    /**
     * 新增宠物领养
     *
     * @param adoption 宠物领养
     * @return 结果
     */
    @Override
    public int insertAdoption(Adoption adoption) {
        //判断是否是微信用户
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (StringUtils.isNotEmpty(openId)) {
            //微信用户
            //设置申请人id
            adoption.setUserId(SecurityUtils.getUserId());
        }
        adoption.setCreateTime(DateUtils.getNowDate());
        return adoptionMapper.insertAdoption(adoption);
    }

    /**
     * 修改宠物领养
     *
     * @param adoption 宠物领养
     * @return 结果
     */
    @Override
    public int updateAdoption(Adoption adoption) {
        adoption.setUpdateTime(DateUtils.getNowDate());
        return adoptionMapper.updateAdoption(adoption);
    }

    /**
     * 批量删除宠物领养
     *
     * @param adoptionIds 需要删除的宠物领养主键
     * @return 结果
     */
//    @Override
//    public int deleteAdoptionByAdoptionIds(Long[] adoptionIds) {
//        return adoptionMapper.deleteAdoptionByAdoptionIds(adoptionIds);
//    }

    /**
     * 批量删除宠物领养
     *
     * @param adoptionIds 需要删除的宠物领养主键
     * @return 结果
     */
    @Override
    public int deleteAdoptionByAdoptionIds(Long[] adoptionIds) {
        return adoptionMapper.updateDeleteAdoptionByAdoptionIds(adoptionIds);
    }

    /**
     * 删除宠物领养信息
     *
     * @param adoptionId 宠物领养主键
     * @return 结果
     */
    @Override
    public int deleteAdoptionByAdoptionId(Long adoptionId) {
        return adoptionMapper.deleteAdoptionByAdoptionId(adoptionId);
    }

    @Override
    public List<Adoption> selectMyAppliedPets(Long userId,Integer status)
    {
        return adoptionMapper.selectMyAppliedPets(userId, status);
    }
}
