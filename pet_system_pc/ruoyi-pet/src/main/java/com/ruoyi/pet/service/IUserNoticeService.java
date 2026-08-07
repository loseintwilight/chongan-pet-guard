package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.UserNotice;

/**
 * 通知与用户之间的Service接口
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
public interface IUserNoticeService 
{
    /**
     * 查询通知与用户之间的
     * 
     * @param unId 通知与用户之间的主键
     * @return 通知与用户之间的
     */
    public UserNotice selectUserNoticeByUnId(Long unId);

    /**
     * 查询通知与用户之间的列表
     * 
     * @param userNotice 通知与用户之间的
     * @return 通知与用户之间的集合
     */
    public List<UserNotice> selectUserNoticeList(UserNotice userNotice);

    /**
     * 新增通知与用户之间的
     * 
     * @param userNotice 通知与用户之间的
     * @return 结果
     */
    public int insertUserNotice(UserNotice userNotice);

    /**
     * 修改通知与用户之间的
     * 
     * @param userNotice 通知与用户之间的
     * @return 结果
     */
    public int updateUserNotice(UserNotice userNotice);

    /**
     * 批量删除通知与用户之间的
     * 
     * @param unIds 需要删除的通知与用户之间的主键集合
     * @return 结果
     */
    public int deleteUserNoticeByUnIds(Long[] unIds);

    /**
     * 删除通知与用户之间的信息
     * 
     * @param unId 通知与用户之间的主键
     * @return 结果
     */
    public int deleteUserNoticeByUnId(Long unId);
}
