package com.ruoyi.pet.mapper;

import java.util.List;
import com.ruoyi.pet.domain.NoticeTemplate;

/**
 * 通知模板Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-07
 */
public interface NoticeTemplateMapper 
{
    /**
     * 查询通知模板
     * 
     * @param templateId 通知模板主键
     * @return 通知模板
     */
    public NoticeTemplate selectNoticeTemplateByTemplateId(Long templateId);

    /**
     * 查询通知模板列表
     * 
     * @param noticeTemplate 通知模板
     * @return 通知模板集合
     */
    public List<NoticeTemplate> selectNoticeTemplateList(NoticeTemplate noticeTemplate);

    /**
     * 新增通知模板
     * 
     * @param noticeTemplate 通知模板
     * @return 结果
     */
    public int insertNoticeTemplate(NoticeTemplate noticeTemplate);

    /**
     * 修改通知模板
     * 
     * @param noticeTemplate 通知模板
     * @return 结果
     */
    public int updateNoticeTemplate(NoticeTemplate noticeTemplate);

    /**
     * 删除通知模板
     * 
     * @param templateId 通知模板主键
     * @return 结果
     */
    public int deleteNoticeTemplateByTemplateId(Long templateId);

    /**
     * 批量删除通知模板
     * 
     * @param templateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNoticeTemplateByTemplateIds(Long[] templateIds);
}
