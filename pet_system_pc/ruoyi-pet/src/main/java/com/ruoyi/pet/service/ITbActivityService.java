package com.ruoyi.pet.service;

import java.util.List;

import com.ruoyi.pet.domain.TbActivity;

/**
 * 活动Service接口
 *
 * @author ruoyi
 * @date 2025-12-04
 */
public interface ITbActivityService {
    /**
     * 查询活动
     *
     * @param activityId 活动主键
     * @return 活动
     */
    public TbActivity selectTbActivityByActivityId(Long activityId);

    /**
     * 查询活动列表
     *
     * @param tbActivity 活动
     * @return 活动集合
     */
    public List<TbActivity> selectTbActivityList(TbActivity tbActivity);

    /**
     * 新增活动
     *
     * @param tbActivity 活动
     * @return 结果
     */
    public int insertTbActivity(TbActivity tbActivity);

    /**
     * 修改活动
     *
     * @param tbActivity 活动
     * @return 结果
     */
    public int updateTbActivity(TbActivity tbActivity);

    /**
     * 批量删除活动
     *
     * @param activityIds 需要删除的活动主键集合
     * @return 结果
     */
    public int deleteTbActivityByActivityIds(Long[] activityIds);

    /**
     * 删除活动信息
     *
     * @param activityId 活动主键
     * @return 结果
     */
    public int deleteTbActivityByActivityId(Long activityId);

    List<TbActivity> selectWxTbActivityList(TbActivity tbActivity);

}
