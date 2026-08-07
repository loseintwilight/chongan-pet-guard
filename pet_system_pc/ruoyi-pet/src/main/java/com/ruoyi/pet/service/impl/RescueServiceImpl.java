package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.RescueMapper;
import com.ruoyi.pet.domain.Rescue;
import com.ruoyi.pet.service.IRescueService;

/**
 * 救助登记Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-06
 */
@Service
public class RescueServiceImpl implements IRescueService {
    @Autowired
    private RescueMapper rescueMapper;

    /**
     * 查询救助登记
     *
     * @param rescueId 救助登记主键
     * @return 救助登记
     */
    @Override
    public Rescue selectRescueByRescueId(Long rescueId) {
        return rescueMapper.selectRescueByRescueId(rescueId);
    }

    /**
     * 查询救助登记列表
     *
     * @param rescue 救助登记
     * @return 救助登记
     */
    @Override
    public List<Rescue> selectRescueList(Rescue rescue) {
        //判断是否是微信用户
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (StringUtils.isNotEmpty(openId)) {
            rescue.setUserId(SecurityUtils.getUserId());
        }
        return rescueMapper.selectRescueList(rescue);
    }

    /**
     * 新增救助登记
     *
     * @param rescue 救助登记
     * @return 结果
     */
    @Override
    public int insertRescue(Rescue rescue) {
        rescue.setUserId(SecurityUtils.getUserId());
        rescue.setCreateBy(SecurityUtils.getUsername());
        rescue.setCreateTime(DateUtils.getNowDate());
        return rescueMapper.insertRescue(rescue);
    }

    /**
     * 修改救助登记
     *
     * @param rescue 救助登记
     * @return 结果
     */
    @Override
    public int updateRescue(Rescue rescue) {
        rescue.setUpdateTime(DateUtils.getNowDate());
        return rescueMapper.updateRescue(rescue);
    }

    /**
     * 批量删除救助登记
     *
     * @param rescueIds 需要删除的救助登记主键
     * @return 结果
     */
    @Override
    public int deleteRescueByRescueIds(Long[] rescueIds) {
        return rescueMapper.deleteRescueByRescueIds(rescueIds);
    }

    /**
     * 删除救助登记信息
     *
     * @param rescueId 救助登记主键
     * @return 结果
     */
    @Override
    public int deleteRescueByRescueId(Long rescueId) {
        return rescueMapper.deleteRescueByRescueId(rescueId);
    }
}
