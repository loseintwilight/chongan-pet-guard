package com.ruoyi.pet.mapper;

import java.util.List;
import com.ruoyi.pet.domain.Notice;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * 通知Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-07
 */
public interface NoticeMapper 
{
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
     * 删除通知
     * 
     * @param noticeId 通知主键
     * @return 结果
     */
    public int deleteNoticeByNoticeId(Long noticeId);

    /**
     * 批量删除通知
     * 
     * @param noticeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNoticeByNoticeIds(Long[] noticeIds);


    public List<Notice> selectPersonNoticeByUserId(@Param("userId") Long userId);

    int getNumberNoticeNotRead(@Param("userId") Long userId);

    int updateDeleteNoticeByNoticeIds(Long[] noticeIds);
}
