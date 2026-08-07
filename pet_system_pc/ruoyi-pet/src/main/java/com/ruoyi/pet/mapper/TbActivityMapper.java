package com.ruoyi.pet.mapper;

import java.util.List;

import com.ruoyi.pet.domain.TbActivity;
import org.apache.ibatis.annotations.Param;

/**
 * 活动Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-06
 */
public interface TbActivityMapper {
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
     * 删除活动
     *
     * @param activityId 活动主键
     * @return 结果
     */
    public int deleteTbActivityByActivityId(Long activityId);

    /**
     * 批量删除活动
     *
     * @param activityIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTbActivityByActivityIds(Long[] activityIds);

    int updateDeleteTbActivityByActivityIds(Long[] activityIds);

    List<TbActivity> selectActivityListByIds(@Param("ids") Long[] ids);

}
