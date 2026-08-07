package com.ruoyi.pet.service;

import java.util.List;

import com.ruoyi.pet.domain.UserActivity;

/**
 * 用户报名Service接口
 *
 * @author ruoyi
 * @date 2025-12-06
 */
public interface IUserActivityService {
    /**
     * 查询用户报名
     *
     * @param uaId 用户报名主键
     * @return 用户报名
     */
    public UserActivity selectUserActivityByUaId(Long uaId);

    /**
     * 查询用户报名列表
     *
     * @param userActivity 用户报名
     * @return 用户报名集合
     */
    public List<UserActivity> selectUserActivityList(UserActivity userActivity);

    /**
     * 新增用户报名
     *
     * @param userActivity 用户报名
     * @return 结果
     */
    public int insertUserActivity(UserActivity userActivity);

    /**
     * 修改用户报名
     *
     * @param userActivity 用户报名
     * @return 结果
     */
    public int updateUserActivity(UserActivity userActivity);

    /**
     * 批量删除用户报名
     *
     * @param uaIds 需要删除的用户报名主键集合
     * @return 结果
     */
    public int deleteUserActivityByUaIds(Long[] uaIds);

    /**
     * 删除用户报名信息
     *
     * @param uaId 用户报名主键
     * @return 结果
     */
    public int deleteUserActivityByUaId(Long uaId);

    void signUpUserToActivity(Long activityId);

}
