package com.ruoyi.pet.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pet.domain.UserNotice;
import com.ruoyi.pet.service.IUserNoticeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 通知与用户之间的Controller
 * 
 * @author ruoyi
 * @date 2025-12-08
 */
@RestController
@RequestMapping("/pet/userNotice")
public class UserNoticeController extends BaseController
{
    @Autowired
    private IUserNoticeService userNoticeService;

    /**
     * 查询通知与用户之间的列表
     */
    @PreAuthorize("@ss.hasPermi('pet:userNotice:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserNotice userNotice)
    {
        startPage();
        List<UserNotice> list = userNoticeService.selectUserNoticeList(userNotice);
        return getDataTable(list);
    }

    /**
     * 导出通知与用户之间的列表
     */
    @PreAuthorize("@ss.hasPermi('pet:userNotice:export')")
    @Log(title = "通知与用户之间的", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserNotice userNotice)
    {
        List<UserNotice> list = userNoticeService.selectUserNoticeList(userNotice);
        ExcelUtil<UserNotice> util = new ExcelUtil<UserNotice>(UserNotice.class);
        util.exportExcel(response, list, "通知与用户之间的数据");
    }

    /**
     * 获取通知与用户之间的详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:userNotice:query')")
    @GetMapping(value = "/{unId}")
    public AjaxResult getInfo(@PathVariable("unId") Long unId)
    {
        return success(userNoticeService.selectUserNoticeByUnId(unId));
    }

    /**
     * 新增通知与用户之间的
     */
    @PreAuthorize("@ss.hasPermi('pet:userNotice:add')")
    @Log(title = "通知与用户之间的", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserNotice userNotice)
    {
        return toAjax(userNoticeService.insertUserNotice(userNotice));
    }

    /**
     * 修改通知与用户之间的
     */
    @PreAuthorize("@ss.hasPermi('pet:userNotice:edit')")
    @Log(title = "通知与用户之间的", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserNotice userNotice)
    {
        return toAjax(userNoticeService.updateUserNotice(userNotice));
    }

    /**
     * 删除通知与用户之间的
     */
    @PreAuthorize("@ss.hasPermi('pet:userNotice:remove')")
    @Log(title = "通知与用户之间的", businessType = BusinessType.DELETE)
	@DeleteMapping("/{unIds}")
    public AjaxResult remove(@PathVariable Long[] unIds)
    {
        return toAjax(userNoticeService.deleteUserNoticeByUnIds(unIds));
    }
}
