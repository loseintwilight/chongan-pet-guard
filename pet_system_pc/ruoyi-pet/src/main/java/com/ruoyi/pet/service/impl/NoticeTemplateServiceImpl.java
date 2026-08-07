package com.ruoyi.pet.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.domain.Notice;
import com.ruoyi.pet.domain.SendNoticeTemplateDTO;
import com.ruoyi.pet.mapper.NoticeMapper;
import com.ruoyi.pet.utils.PlaceholderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.NoticeTemplateMapper;
import com.ruoyi.pet.domain.NoticeTemplate;
import com.ruoyi.pet.service.INoticeTemplateService;

/**
 * 通知模板Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-07
 */
@Service
@Slf4j
public class NoticeTemplateServiceImpl implements INoticeTemplateService {
    @Autowired
    private NoticeTemplateMapper noticeTemplateMapper;

    /**
     * 查询通知模板
     *
     * @param templateId 通知模板主键
     * @return 通知模板
     */
    @Override
    public NoticeTemplate selectNoticeTemplateByTemplateId(Long templateId) {
        return noticeTemplateMapper.selectNoticeTemplateByTemplateId(templateId);
    }

    /**
     * 查询通知模板列表
     *
     * @param noticeTemplate 通知模板
     * @return 通知模板
     */
    @Override
    public List<NoticeTemplate> selectNoticeTemplateList(NoticeTemplate noticeTemplate) {
        return noticeTemplateMapper.selectNoticeTemplateList(noticeTemplate);
    }

    /**
     * 新增通知模板
     *
     * @param noticeTemplate 通知模板
     * @return 结果
     */
    @Override
    public int insertNoticeTemplate(NoticeTemplate noticeTemplate) {
        noticeTemplate.setCreateTime(DateUtils.getNowDate());
        return noticeTemplateMapper.insertNoticeTemplate(noticeTemplate);
    }

    /**
     * 修改通知模板
     *
     * @param noticeTemplate 通知模板
     * @return 结果
     */
    @Override
    public int updateNoticeTemplate(NoticeTemplate noticeTemplate) {
        noticeTemplate.setUpdateTime(DateUtils.getNowDate());
        return noticeTemplateMapper.updateNoticeTemplate(noticeTemplate);
    }

    /**
     * 批量删除通知模板
     *
     * @param templateIds 需要删除的通知模板主键
     * @return 结果
     */
    @Override
    public int deleteNoticeTemplateByTemplateIds(Long[] templateIds) {
        return noticeTemplateMapper.deleteNoticeTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除通知模板信息
     *
     * @param templateId 通知模板主键
     * @return 结果
     */
    @Override
    public int deleteNoticeTemplateByTemplateId(Long templateId) {
        return noticeTemplateMapper.deleteNoticeTemplateByTemplateId(templateId);
    }


    @Autowired
    private PlaceholderUtils placeholderUtils;

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 发送通知通过通知模板
     *
     * @param sendNoticeTemplateDTO templateId 模板id
     *                              title 模板标题
     *                              content 模板内容
     *                              userId 用户id
     *                              petId 宠物id
     * @return 结果
     */
    @Override
    public int sendNoticeByNoticeTemplate(SendNoticeTemplateDTO sendNoticeTemplateDTO) {
        log.info("sendNoticeTemplateDTO =  =  = {}", sendNoticeTemplateDTO);
        //模板id
        Long templateId = sendNoticeTemplateDTO.getTemplateId();
        //模板标题
        String title = sendNoticeTemplateDTO.getTitle();
        //模板内容
        String content = sendNoticeTemplateDTO.getContent();

        // 1. 分别获取不同表的数据
        Map<String, Object> userParams = placeholderUtils.getFieldMapping("sys_user", sendNoticeTemplateDTO.getUserId());
        Map<String, Object> petBoardingParams = placeholderUtils.getFieldMapping("tb_pet_boarding", sendNoticeTemplateDTO.getBoardingId());
        Map<String, Object> petRescueParams = placeholderUtils.getFieldMapping("tb_rescue", sendNoticeTemplateDTO.getRescueId());
        Map<String, Object> petParams = placeholderUtils.getFieldMapping("tb_pet", sendNoticeTemplateDTO.getPetId());

        // 2. 将所有数据合并到一个Map中
        Map<String, Object> allParams = new HashMap<>();
        allParams.putAll(userParams); // 先放入用户信息
        allParams.putAll(petBoardingParams);  // 再放入宠物信息，如果有相同的key，后者会覆盖前者（通常不会有）
        allParams.putAll(petRescueParams);
        allParams.putAll(petParams);

        // 3. 使用合并后的完整参数Map，只进行一次渲染
        content = PlaceholderUtils.renderTemplate(content, allParams);

        /* 复制到新的通知表中 */
        Notice notice = new Notice();
        notice.setTemplateId(templateId);
        notice.setNoticeMain(content);
        notice.setPublishName("系统管理者");
        notice.setPublishTime(DateUtils.getNowDate());
        notice.setStatus(0L); //已发布
        notice.setTitle(title);
        int rows = noticeMapper.insertNotice(notice);
        /**
         * 用户和通知的中间表
         */
        if (rows > 0) {
            // 插入成功后，notice 对象的 noticeId 属性已自动填充
            Long noticeId = notice.getNoticeId();
            // 可以继续使用这个对象
            return Math.toIntExact(noticeId);
        }
        return 0;
    }
}
