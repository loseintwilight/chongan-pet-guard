package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.Donation;
import com.ruoyi.pet.service.IDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 捐赠Controller
 *
 * @author ruoyi
 * @date 2025-12-03
 */
@RestController
@RequestMapping("/pet/donation")
public class DonationController extends BaseController {
    @Autowired
    private IDonationService donationService;

    /**
     * 查询捐赠列表
     */
    @PreAuthorize("@ss.hasPermi('pet:donation:list')")
    @GetMapping("/list")
    public TableDataInfo list(Donation donation) {
        startPage();
        List<Donation> list = donationService.selectDonationList(donation);
        return getDataTable(list);
    }

    /**
     * 导出捐赠列表
     */
    @PreAuthorize("@ss.hasPermi('pet:donation:export')")
    @Log(title = "捐赠", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Donation donation) {
        List<Donation> list = donationService.selectDonationList(donation);
        ExcelUtil<Donation> util = new ExcelUtil<Donation>(Donation.class);
        util.exportExcel(response, list, "捐赠数据");
    }

    /**
     * 获取捐赠详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:donation:query')")
    @GetMapping(value = "/{donationId}")
    public AjaxResult getInfo(@PathVariable("donationId") Long donationId) {
        return success(donationService.selectDonationByDonationId(donationId));
    }

    /**
     * 新增捐赠
     */
    @PreAuthorize("@ss.hasPermi('pet:donation:add')")
    @Log(title = "捐赠", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Donation donation) {
        //获取用户信息
        Long userId = SecurityUtils.getUserId();
        String username = SecurityUtils.getUsername();
        donation.setUserId(userId);
        donation.setUserName(username);
        //默认成功
        donation.setStatus(1L);
        //交易后生成的交易id
        //生成唯一的支付标识id
        donation.setTransctionId("TID" + System.currentTimeMillis() + userId);
        //设置创建时间，创建人
        donation.setCreateBy(username);
        donation.setCreateTime(DateUtils.getNowDate());
        return toAjax(donationService.insertDonation(donation));
    }

    /**
     * 修改捐赠
     */
    @PreAuthorize("@ss.hasPermi('pet:donation:edit')")
    @Log(title = "捐赠", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Donation donation) {
        return toAjax(donationService.updateDonation(donation));
    }

    /**
     * 删除捐赠
     */
    @PreAuthorize("@ss.hasPermi('pet:donation:remove')")
    @Log(title = "捐赠", businessType = BusinessType.DELETE)
    @DeleteMapping("/{donationIds}")
    public AjaxResult remove(@PathVariable Long[] donationIds) {
        return toAjax(donationService.deleteDonationByDonationIds(donationIds));
    }
}
