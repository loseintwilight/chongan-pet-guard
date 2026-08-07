package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.Notice;
import com.ruoyi.pet.service.INoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 通知Controller
 *
 * @author ruoyi
 * @date 2025-12-07
 */
@RestController
@RequestMapping("/pet/notice")
public class NoticeController extends BaseController {
    @Autowired
    private INoticeService noticeService;

    /**
     * 查询通知列表
     */
    @PreAuthorize("@ss.hasPermi('pet:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(Notice notice) {
        startPage();
        List<Notice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 查询微信小程序个人通知列表
     */
    @PreAuthorize("@ss.hasPermi('pet:notice:list')")
    @GetMapping("/getPersonNoticeMsg")
    public TableDataInfo getPersonNoticeMsg() {
        startPage();
        List<Notice> list = noticeService.getPersonNoticeList();
        return getDataTable(list);
    }

    /**
     * 获取个人未读通知数量
     */
    @PreAuthorize("@ss.hasPermi('pet:notice:list')")
    @GetMapping("/getUnReadNoticeNumber")
    public AjaxResult getUnReadNoticeNumber() {
        //返回未读数量
        return noticeService.getUnReadNoticeNumber();
    }

    /**
     * 导出通知列表
     */
    @PreAuthorize("@ss.hasPermi('pet:notice:export')")
    @Log(title = "通知", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Notice notice) {
        List<Notice> list = noticeService.selectNoticeList(notice);
        ExcelUtil<Notice> util = new ExcelUtil<Notice>(Notice.class);
        util.exportExcel(response, list, "通知数据");
    }

    /**
     * 获取通知详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public AjaxResult getInfo(@PathVariable("noticeId") Long noticeId) {
        return success(noticeService.selectNoticeByNoticeId(noticeId));
    }

    /**
     * 新增通知
     */
    @PreAuthorize("@ss.hasPermi('pet:notice:add')")
    @Log(title = "通知", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Notice notice) {
        return toAjax(noticeService.insertNotice(notice));
    }

    /**
     * 修改通知
     */
    @PreAuthorize("@ss.hasPermi('pet:notice:edit')")
    @Log(title = "通知", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Notice notice) {
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知
     */
    @PreAuthorize("@ss.hasPermi('pet:notice:remove')")
    @Log(title = "通知", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public AjaxResult remove(@PathVariable Long[] noticeIds) {
        return toAjax(noticeService.deleteNoticeByNoticeIds(noticeIds));
    }
}
