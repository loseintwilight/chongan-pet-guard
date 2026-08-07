package com.ruoyi.pet.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.UserNoticeMapper;
import com.ruoyi.pet.domain.UserNotice;
import com.ruoyi.pet.service.IUserNoticeService;

/**
 * 通知与用户之间的Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
@Service
public class UserNoticeServiceImpl implements IUserNoticeService 
{
    @Autowired
    private UserNoticeMapper userNoticeMapper;

    /**
     * 查询通知与用户之间的
     * 
     * @param unId 通知与用户之间的主键
     * @return 通知与用户之间的
     */
    @Override
    public UserNotice selectUserNoticeByUnId(Long unId)
    {
        return userNoticeMapper.selectUserNoticeByUnId(unId);
    }

    /**
     * 查询通知与用户之间的列表
     * 
     * @param userNotice 通知与用户之间的
     * @return 通知与用户之间的
     */
    @Override
    public List<UserNotice> selectUserNoticeList(UserNotice userNotice)
    {
        return userNoticeMapper.selectUserNoticeList(userNotice);
    }

    /**
     * 新增通知与用户之间的
     * 
     * @param userNotice 通知与用户之间的
     * @return 结果
     */
    @Override
    public int insertUserNotice(UserNotice userNotice)
    {
        userNotice.setCreateTime(DateUtils.getNowDate());
        return userNoticeMapper.insertUserNotice(userNotice);
    }

    /**
     * 修改通知与用户之间的
     * 
     * @param userNotice 通知与用户之间的
     * @return 结果
     */
    @Override
    public int updateUserNotice(UserNotice userNotice)
    {
        return userNoticeMapper.updateUserNotice(userNotice);
    }

    /**
     * 批量删除通知与用户之间的
     * 
     * @param unIds 需要删除的通知与用户之间的主键
     * @return 结果
     */
    @Override
    public int deleteUserNoticeByUnIds(Long[] unIds)
    {
        return userNoticeMapper.deleteUserNoticeByUnIds(unIds);
    }

    /**
     * 删除通知与用户之间的信息
     * 
     * @param unId 通知与用户之间的主键
     * @return 结果
     */
    @Override
    public int deleteUserNoticeByUnId(Long unId)
    {
        return userNoticeMapper.deleteUserNoticeByUnId(unId);
    }
}
