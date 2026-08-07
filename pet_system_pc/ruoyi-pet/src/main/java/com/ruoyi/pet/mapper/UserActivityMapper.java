package com.ruoyi.pet.mapper;

import java.util.List;

import com.ruoyi.pet.domain.UserActivity;
import org.apache.ibatis.annotations.Param;

/**
 * 用户报名Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-06
 */
public interface UserActivityMapper {
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
     * 删除用户报名
     *
     * @param uaId 用户报名主键
     * @return 结果
     */
    public int deleteUserActivityByUaId(Long uaId);

    /**
     * 批量删除用户报名
     *
     * @param uaIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserActivityByUaIds(Long[] uaIds);

    UserActivity selectUserActivityByUserIdAndActivity(@Param("userId") Long userId, @Param("activityId") Long activityId);

    List<Long> selectUserActivityByUserId(@Param("userId") Long userId);

}
