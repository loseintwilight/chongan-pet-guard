package com.ruoyi.pet.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.DonationMapper;
import com.ruoyi.pet.domain.Donation;
import com.ruoyi.pet.service.IDonationService;

/**
 * 捐赠Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-03
 */
@Service
public class DonationServiceImpl implements IDonationService 
{
    @Autowired
    private DonationMapper donationMapper;

    /**
     * 查询捐赠
     * 
     * @param donationId 捐赠主键
     * @return 捐赠
     */
    @Override
    public Donation selectDonationByDonationId(Long donationId)
    {
        return donationMapper.selectDonationByDonationId(donationId);
    }

    /**
     * 查询捐赠列表
     * 
     * @param donation 捐赠
     * @return 捐赠
     */
    @Override
    public List<Donation> selectDonationList(Donation donation)
    {
        return donationMapper.selectDonationList(donation);
    }

    /**
     * 新增捐赠
     * 
     * @param donation 捐赠
     * @return 结果
     */
    @Override
    public int insertDonation(Donation donation)
    {
        donation.setCreateTime(DateUtils.getNowDate());
        return donationMapper.insertDonation(donation);
    }

    /**
     * 修改捐赠
     * 
     * @param donation 捐赠
     * @return 结果
     */
    @Override
    public int updateDonation(Donation donation)
    {
        donation.setUpdateTime(DateUtils.getNowDate());
        return donationMapper.updateDonation(donation);
    }

    /**
     * 批量删除捐赠
     * 
     * @param donationIds 需要删除的捐赠主键
     * @return 结果
     */
//    @Override
//    public int deleteDonationByDonationIds(Long[] donationIds)
//    {
//        return donationMapper.deleteDonationByDonationIds(donationIds);
//    }

    /**
     * 批量删除捐赠
     *
     * @param donationIds 需要删除的捐赠主键
     * @return 结果
     */
    @Override
    public int deleteDonationByDonationIds(Long[] donationIds)
    {
        return donationMapper.updateDeleteDonationByDonationIds(donationIds);
    }

    /**
     * 删除捐赠信息
     * 
     * @param donationId 捐赠主键
     * @return 结果
     */
    @Override
    public int deleteDonationByDonationId(Long donationId)
    {
        return donationMapper.deleteDonationByDonationId(donationId);
    }
}
