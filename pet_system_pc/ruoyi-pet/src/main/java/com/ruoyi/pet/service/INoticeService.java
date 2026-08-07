package com.ruoyi.pet.service;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pet.domain.Notice;

/**
 * 通知Service接口
 *
 * @author ruoyi
 * @date 2025-12-07
 */
public interface INoticeService {
    /**
     * 查询通知
     *
     * @param noticeId 通知主键
     * @return 通知
     */
    public Notice selectNoticeByNoticeId(Long noticeId);

    /**
     * 查询通知列表
     *
     * @param notice 通知
     * @return 通知集合
     */
    public List<Notice> selectNoticeList(Notice notice);

    /**
     * 新增通知
     *
     * @param notice 通知
     * @return 结果
     */
    public int insertNotice(Notice notice);

    /**
     * 修改通知
     *
     * @param notice 通知
     * @return 结果
     */
    public int updateNotice(Notice notice);

    /**
     * 批量删除通知
     *
     * @param noticeIds 需要删除的通知主键集合
     * @return 结果
     */
    public int deleteNoticeByNoticeIds(Long[] noticeIds);

    /**
     * 删除通知信息
     *
     * @param noticeId 通知主键
     * @return 结果
     */
    public int deleteNoticeByNoticeId(Long noticeId);

    /**
     * 查询微信个人通知页面
     *
     * @return 结果
     */
    List<Notice> getPersonNoticeList();


    AjaxResult getUnReadNoticeNumber();

}
