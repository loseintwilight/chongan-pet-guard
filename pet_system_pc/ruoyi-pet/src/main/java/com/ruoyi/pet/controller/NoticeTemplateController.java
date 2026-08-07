package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.NoticeTemplate;
import com.ruoyi.pet.domain.SendNoticeTemplateDTO;
import com.ruoyi.pet.service.INoticeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 通知模板Controller
 *
 * @author ruoyi
 * @date 2025-12-07
 */
@RestController
@RequestMapping("/pet/template")
public class NoticeTemplateController extends BaseController {
    @Autowired
    private INoticeTemplateService noticeTemplateService;

    /**
     * 查询通知模板列表
     */
    @PreAuthorize("@ss.hasPermi('pet:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(NoticeTemplate noticeTemplate) {
        startPage();
        List<NoticeTemplate> list = noticeTemplateService.selectNoticeTemplateList(noticeTemplate);
        return getDataTable(list);
    }

    /**
     * 导出通知模板列表
     */
    @PreAuthorize("@ss.hasPermi('pet:template:export')")
    @Log(title = "通知模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NoticeTemplate noticeTemplate) {
        List<NoticeTemplate> list = noticeTemplateService.selectNoticeTemplateList(noticeTemplate);
        ExcelUtil<NoticeTemplate> util = new ExcelUtil<NoticeTemplate>(NoticeTemplate.class);
        util.exportExcel(response, list, "通知模板数据");
    }

    /**
     * 获取通知模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId) {
        return success(noticeTemplateService.selectNoticeTemplateByTemplateId(templateId));
    }

    /**
     * 新增通知模板
     */
    @PreAuthorize("@ss.hasPermi('pet:template:add')")
    @Log(title = "通知模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NoticeTemplate noticeTemplate) {
        return toAjax(noticeTemplateService.insertNoticeTemplate(noticeTemplate));
    }

    /**
     * 修改通知模板
     */
    @PreAuthorize("@ss.hasPermi('pet:template:edit')")
    @Log(title = "通知模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NoticeTemplate noticeTemplate) {
        return toAjax(noticeTemplateService.updateNoticeTemplate(noticeTemplate));
    }

    /**
     * 删除通知模板
     */
    @PreAuthorize("@ss.hasPermi('pet:template:remove')")
    @Log(title = "通知模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds) {
        return toAjax(noticeTemplateService.deleteNoticeTemplateByTemplateIds(templateIds));
    }

    /**
     * 根据通知模板发送通知接口
     * 模板id  templateId
     * 模板标题 title
     * 模板内容  content
     * 用户id  user_id
     * 宠物id  pet_id
     */
    @PreAuthorize("@ss.hasPermi('pet:template:insert')")
    @Log(title = "发送通知", businessType = BusinessType.INSERT)
    @PostMapping("/sendNotice")
    public int sendNoticeByNoticeTemplate(@RequestBody SendNoticeTemplateDTO sendNoticeTemplateDTO) {
        return noticeTemplateService.sendNoticeByNoticeTemplate(sendNoticeTemplateDTO);
    }

}
