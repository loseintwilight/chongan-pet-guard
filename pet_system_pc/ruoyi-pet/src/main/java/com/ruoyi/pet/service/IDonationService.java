package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.Donation;

/**
 * 捐赠Service接口
 * 
 * @author ruoyi
 * @date 2025-12-03
 */
public interface IDonationService 
{
    /**
     * 查询捐赠
     * 
     * @param donationId 捐赠主键
     * @return 捐赠
     */
    public Donation selectDonationByDonationId(Long donationId);

    /**
     * 查询捐赠列表
     * 
     * @param donation 捐赠
     * @return 捐赠集合
     */
    public List<Donation> selectDonationList(Donation donation);

    /**
     * 新增捐赠
     * 
     * @param donation 捐赠
     * @return 结果
     */
    public int insertDonation(Donation donation);

    /**
     * 修改捐赠
     * 
     * @param donation 捐赠
     * @return 结果
     */
    public int updateDonation(Donation donation);

    /**
     * 批量删除捐赠
     * 
     * @param donationIds 需要删除的捐赠主键集合
     * @return 结果
     */
    public int deleteDonationByDonationIds(Long[] donationIds);

    /**
     * 删除捐赠信息
     * 
     * @param donationId 捐赠主键
     * @return 结果
     */
    public int deleteDonationByDonationId(Long donationId);
}
