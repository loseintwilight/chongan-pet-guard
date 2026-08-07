package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.pet.domain.TbActivity;
import com.ruoyi.pet.mapper.TbActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.UserActivityMapper;
import com.ruoyi.pet.domain.UserActivity;
import com.ruoyi.pet.service.IUserActivityService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户报名Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-06
 */
@Service
public class UserActivityServiceImpl implements IUserActivityService {
    @Autowired
    private UserActivityMapper userActivityMapper;

    @Autowired
    private TbActivityMapper activityMapper;

    /**
     * 查询用户报名
     *
     * @param uaId 用户报名主键
     * @return 用户报名
     */
    @Override
    public UserActivity selectUserActivityByUaId(Long uaId) {
        return userActivityMapper.selectUserActivityByUaId(uaId);
    }

    /**
     * 查询用户报名列表
     *
     * @param userActivity 用户报名
     * @return 用户报名
     */
    @Override
    public List<UserActivity> selectUserActivityList(UserActivity userActivity) {
        return userActivityMapper.selectUserActivityList(userActivity);
    }

    /**
     * 新增用户报名
     *
     * @param userActivity 用户报名
     * @return 结果
     */
    @Override
    public int insertUserActivity(UserActivity userActivity) {
        userActivity.setCreateTime(DateUtils.getNowDate());
        return userActivityMapper.insertUserActivity(userActivity);
    }

    /**
     * 修改用户报名
     *
     * @param userActivity 用户报名
     * @return 结果
     */
    @Override
    public int updateUserActivity(UserActivity userActivity) {
        userActivity.setUpdateTime(DateUtils.getNowDate());
        return userActivityMapper.updateUserActivity(userActivity);
    }

    /**
     * 批量删除用户报名
     *
     * @param uaIds 需要删除的用户报名主键
     * @return 结果
     */
    @Override
    public int deleteUserActivityByUaIds(Long[] uaIds) {
        return userActivityMapper.deleteUserActivityByUaIds(uaIds);
    }

    /**
     * 删除用户报名信息
     *
     * @param uaId 用户报名主键
     * @return 结果
     */
    @Override
    public int deleteUserActivityByUaId(Long uaId) {
        return userActivityMapper.deleteUserActivityByUaId(uaId);
    }

    @Override
    @Transactional
    public void signUpUserToActivity(Long activityId) {
        //报名人数加1
        //查询当前的报名人数
        TbActivity activity = activityMapper.selectTbActivityByActivityId(activityId);
        //限额人数
        Long personCount = activity.getPersonCount();
        //当前报名人数
        Long count = activity.getCount();
        if (personCount <= count) {
            throw new ServiceException("报名人数已满");
        } else {
            activity.setCount(count + 1);
            activityMapper.updateTbActivity(activity);
            //获取当前报名用户信息
            UserActivity userActivity = new UserActivity();
            userActivity.setUserId(SecurityUtils.getUserId());
            SysUser user = SecurityUtils.getLoginUser().getUser();
            userActivity.setContactPhone(user.getPhonenumber());
            userActivity.setContactName(user.getUserName());
            userActivity.setStatus(0L);
            userActivity.setCreateTime(DateUtils.getNowDate());
            userActivity.setCreateBy(user.getUserName());
            userActivity.setActivityId(activityId);
            userActivity.setSignTime(DateUtils.getNowDate());
            userActivityMapper.insertUserActivity(userActivity);
        }
    }
}
