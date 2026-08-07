package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.domain.UserActivity;
import com.ruoyi.pet.mapper.UserActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.TbActivityMapper;
import com.ruoyi.pet.domain.TbActivity;
import com.ruoyi.pet.service.ITbActivityService;

import javax.annotation.Resource;

/**
 * 活动Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-04
 */
@Service
@Slf4j
public class TbActivityServiceImpl implements ITbActivityService {
    @Resource
    private TbActivityMapper tbActivityMapper;

    @Autowired
    private UserActivityMapper userActivityMapper;

    /**
     * 查询活动
     *
     * @param activityId 活动主键
     * @return 活动
     */
    @Override
    public TbActivity selectTbActivityByActivityId(Long activityId) {
        //如果当前是微信用户
        String openId = SecurityUtils.getLoginUser().getOpenId();
        long status = 0L;
        if (StringUtils.isNotEmpty(openId)) {
            //查询报名表，查看报名状态
            UserActivity userActivity = userActivityMapper.selectUserActivityByUserIdAndActivity(SecurityUtils.getUserId(), activityId);
            if (null != userActivity) {
                status = userActivity.getStatus() != 1 ? 1 : 0L;
            }
        }
        TbActivity activity = tbActivityMapper.selectTbActivityByActivityId(activityId);
        activity.setIsSignedUp(status);
        return activity;
    }

    /**
     * 查询活动列表
     *
     * @param tbActivity 活动
     * @return 活动
     */
    @Override
    public List<TbActivity> selectTbActivityList(TbActivity tbActivity) {
        return tbActivityMapper.selectTbActivityList(tbActivity);
    }

    /**
     * 新增活动
     *
     * @param tbActivity 活动
     * @return 结果
     */
    @Override
    public int insertTbActivity(TbActivity tbActivity) {
        tbActivity.setCreateTime(DateUtils.getNowDate());
        return tbActivityMapper.insertTbActivity(tbActivity);
    }

    /**
     * 修改活动
     *
     * @param tbActivity 活动
     * @return 结果
     */
    @Override
    public int updateTbActivity(TbActivity tbActivity) {
        tbActivity.setUpdateTime(DateUtils.getNowDate());
        return tbActivityMapper.updateTbActivity(tbActivity);
    }

    /**
     * 批量删除活动
     *
     * @param activityIds 需要删除的活动主键
     * @return 结果
     */
//    @Override
//    public int deleteTbActivityByActivityIds(Long[] activityIds) {
//        return tbActivityMapper.deleteTbActivityByActivityIds(activityIds);
//    }

    /**
     * 批量删除活动
     *
     * @param activityIds 需要删除的活动主键
     * @return 结果
     */
    @Override
    public int deleteTbActivityByActivityIds(Long[] activityIds) {
        return tbActivityMapper.updateDeleteTbActivityByActivityIds(activityIds);
    }

    /**
     * 删除活动信息
     *
     * @param activityId 活动主键
     * @return 结果
     */
    @Override
    public int deleteTbActivityByActivityId(Long activityId) {
        return tbActivityMapper.deleteTbActivityByActivityId(activityId);
    }

    /**
     * 微信端获取用户参与过的活动信息列表
     *
     * @param tbActivity 条件
     * @return 结果
     */
    @Override
    public List<TbActivity> selectWxTbActivityList(TbActivity tbActivity) {
        //获取用户id
        Long userId = SecurityUtils.getUserId();
        //查询用户与活动之间的中间表-报名表-获取活动id
        List<Long> activityIdList = userActivityMapper.selectUserActivityByUserId(userId);
        Long[] ids = null;
        if (activityIdList != null) {
            ids = activityIdList.toArray(new Long[0]);
        }
        return tbActivityMapper.selectActivityListByIds(ids);
    }
}
