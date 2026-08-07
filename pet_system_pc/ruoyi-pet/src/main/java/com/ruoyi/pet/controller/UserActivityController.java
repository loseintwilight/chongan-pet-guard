package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.UserActivity;
import com.ruoyi.pet.service.ITbActivityService;
import com.ruoyi.pet.service.IUserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户报名Controller
 *
 * @author ruoyi
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/pet/activity_registration")
public class UserActivityController extends BaseController {
    @Resource
    private IUserActivityService userActivityService;
    @Autowired
    private ITbActivityService tbActivityService;

    /**
     * 查询用户报名列表
     */
    @PreAuthorize("@ss.hasPermi('pet:activity_registration:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserActivity userActivity) {
        startPage();
        List<UserActivity> list = userActivityService.selectUserActivityList(userActivity);
        return getDataTable(list);
    }

    /**
     * 导出用户报名列表
     */
    @PreAuthorize("@ss.hasPermi('pet:activity_registration:export')")
    @Log(title = "用户报名", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserActivity userActivity) {
        List<UserActivity> list = userActivityService.selectUserActivityList(userActivity);
        ExcelUtil<UserActivity> util = new ExcelUtil<UserActivity>(UserActivity.class);
        util.exportExcel(response, list, "用户报名数据");
    }

    /**
     * 获取用户报名详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:activity_registration:query')")
//    @GetMapping(value = "/{uaId}")
//    public AjaxResult getInfo(@PathVariable("uaId") Long uaId)
//    {
//        return success(userActivityService.selectUserActivityByUaId(uaId));
//    }
//

    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Long activityId) {
        return success(tbActivityService.selectTbActivityByActivityId(activityId));
    }

    /**
     * 新增用户报名
     */
    @PreAuthorize("@ss.hasPermi('pet:activity_registration:add')")
    @Log(title = "用户报名", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserActivity userActivity) {
        return toAjax(userActivityService.insertUserActivity(userActivity));
    }

    /**
     * 修改用户报名
     */
    @PreAuthorize("@ss.hasPermi('pet:activity_registration:edit')")
    @Log(title = "用户报名", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserActivity userActivity) {
        return toAjax(userActivityService.updateUserActivity(userActivity));
    }

    /**
     * 删除用户报名
     */
    @PreAuthorize("@ss.hasPermi('pet:activity_registration:remove')")
    @Log(title = "用户报名", businessType = BusinessType.DELETE)
    @DeleteMapping("/{uaIds}")
    public AjaxResult remove(@PathVariable Long[] uaIds) {
        return toAjax(userActivityService.deleteUserActivityByUaIds(uaIds));
    }

    /**
     * 用户报名
     *
     * @param activityId 活动id
     * @return 结果
     */
    @GetMapping("/signup/{activityId}")
    public AjaxResult signUpUserToActivity(@PathVariable Long activityId) {
        userActivityService.signUpUserToActivity(activityId);
        return success();
    }

}
