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
import com.ruoyi.pet.domain.UserPetCollect;
import com.ruoyi.pet.service.IUserPetCollectService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户收藏宠物领养Controller
 *
 * @author ruoyi
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/pet/collect")
public class UserPetCollectController extends BaseController {
    @Autowired
    private IUserPetCollectService userPetCollectService;

    /**
     * 查询用户收藏宠物领养列表
     */
    @PreAuthorize("@ss.hasPermi('pet:collect:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserPetCollect userPetCollect) {
        startPage();
        List<UserPetCollect> list = userPetCollectService.selectUserPetCollectList(userPetCollect);
        return getDataTable(list);
    }

    /**
     * 导出用户收藏宠物领养列表
     */
    @PreAuthorize("@ss.hasPermi('pet:collect:export')")
    @Log(title = "用户收藏宠物领养", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserPetCollect userPetCollect) {
        List<UserPetCollect> list = userPetCollectService.selectUserPetCollectList(userPetCollect);
        ExcelUtil<UserPetCollect> util = new ExcelUtil<UserPetCollect>(UserPetCollect.class);
        util.exportExcel(response, list, "用户收藏宠物领养数据");
    }

    /**
     * 获取用户收藏宠物领养详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:collect:query')")
    @GetMapping(value = "/{upcId}")
    public AjaxResult getInfo(@PathVariable("upcId") Long upcId) {
        return success(userPetCollectService.selectUserPetCollectByUpcId(upcId));
    }

    /**
     * 新增用户收藏宠物领养
     */
//    @PreAuthorize("@ss.hasPermi('pet:collect:add')")
    //@Log(title = "用户收藏宠物领养", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserPetCollect userPetCollect) {
        return toAjax(userPetCollectService.insertUserPetCollect(userPetCollect));
    }

    /**
     * 修改用户收藏宠物领养
     */
    @PreAuthorize("@ss.hasPermi('pet:collect:edit')")
    @Log(title = "用户收藏宠物领养", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserPetCollect userPetCollect) {
        return toAjax(userPetCollectService.updateUserPetCollect(userPetCollect));
    }

    /**
     * 删除用户收藏宠物领养
     */
    @PreAuthorize("@ss.hasPermi('pet:collect:remove')")
    @Log(title = "用户收藏宠物领养", businessType = BusinessType.DELETE)
    @DeleteMapping("/{upcIds}")
    public AjaxResult remove(@PathVariable Long[] upcIds) {
        return toAjax(userPetCollectService.deleteUserPetCollectByUpcIds(upcIds));
    }

    @Log(title = "用户删除收藏宠物领养")
    @PostMapping("/cancelCollect")
    public AjaxResult cancelCollect(@RequestBody UserPetCollect userPetCollect) {
        return toAjax(userPetCollectService.deleteUserPetCollectByUserIdAndPetId(userPetCollect.getPetId()));
    }

}
