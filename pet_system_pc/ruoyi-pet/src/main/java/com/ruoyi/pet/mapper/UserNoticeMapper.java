package com.ruoyi.pet.mapper;

import java.util.List;
import com.ruoyi.pet.domain.UserNotice;
import org.apache.ibatis.annotations.Param;

/**
 * 通知与用户之间的Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
public interface UserNoticeMapper 
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
     * 删除通知与用户之间的
     * 
     * @param unId 通知与用户之间的主键
     * @return 结果
     */
    public int deleteUserNoticeByUnId(Long unId);

    /**
     * 批量删除通知与用户之间的
     * 
     * @param unIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserNoticeByUnIds(Long[] unIds);

    UserNotice selectUserNoticeByNoticeId(@Param("noticeId") Long noticeId);

    void updateReadStatus(UserNotice userNotice);

}
