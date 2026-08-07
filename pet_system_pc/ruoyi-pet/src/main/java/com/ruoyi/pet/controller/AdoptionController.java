package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pet.domain.Adoption;
import com.ruoyi.pet.domain.Pet;
import com.ruoyi.pet.service.IAdoptionService;
import com.ruoyi.pet.service.IPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 宠物领养Controller
 *
 * @author ruoyi
 * @date 2025-12-07
 */
@RestController
@RequestMapping("/pet/adoption")
public class AdoptionController extends BaseController {
    @Autowired
    private IAdoptionService adoptionService;

    @Autowired
    private IPetService petService;

    /**
     * 查询宠物领养列表
     */
    @PreAuthorize("@ss.hasPermi('pet:adoption:list')")
    @GetMapping("/list")
    public TableDataInfo list(Adoption adoption) {
        startPage();
        List<Adoption> list = adoptionService.selectAdoptionList(adoption);
        return getDataTable(list);
    }

    /**
     * 导出宠物领养列表
     */
    @PreAuthorize("@ss.hasPermi('pet:adoption:export')")
    @Log(title = "宠物领养", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Adoption adoption) {
        List<Adoption> list = adoptionService.selectAdoptionList(adoption);
        ExcelUtil<Adoption> util = new ExcelUtil<Adoption>(Adoption.class);
        util.exportExcel(response, list, "宠物领养数据");
    }

    /**
     * 获取宠物领养详细信息
     */
    @PreAuthorize("@ss.hasPermi('pet:adoption:query')")
    @GetMapping(value = "/{adoptionId}")
    public AjaxResult getInfo(@PathVariable("adoptionId") Long adoptionId) {
        return success(adoptionService.selectAdoptionByAdoptionId(adoptionId));
    }

    /**
     * 新增宠物领养
     */
    @PreAuthorize("@ss.hasPermi('pet:adoption:add')")
    @Log(title = "宠物领养", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Adoption adoption) {
        return toAjax(adoptionService.insertAdoption(adoption));
    }

    /**
     * 修改宠物领养
     */
    @PreAuthorize("@ss.hasPermi('pet:adoption:edit')")
    @Log(title = "宠物领养", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Adoption adoption) {
        return toAjax(adoptionService.updateAdoption(adoption));
    }

    /**
     * 删除宠物领养
     */
    @PreAuthorize("@ss.hasPermi('pet:adoption:remove')")
    @Log(title = "宠物领养", businessType = BusinessType.DELETE)
    @DeleteMapping("/{adoptionIds}")
    public AjaxResult remove(@PathVariable Long[] adoptionIds) {
        return toAjax(adoptionService.deleteAdoptionByAdoptionIds(adoptionIds));
    }

    /**
     * 获取我的领养申请列表
     * 使用AjaxResult返回类型
     */
    @GetMapping("/pet/myAdoption")
    public TableDataInfo listMyAppliedPets(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getUserId();
            // 设置分页
            startPage();
            // 调用Service查询我的领养申请
            List<Adoption> list = adoptionService.selectMyAppliedPets(userId, status);
            for (Adoption adoption : list) {
                Long petId = adoption.getPetId();
                Pet pet = petService.selectPetByPetId(petId);
                adoption.setPet(pet);
            }
            System.out.println("查询结果条数: " + list.size());
            if (!list.isEmpty()) {
                System.out.println("第一条数据: " + list.get(0));
            }
            return getDataTable(list);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("查询异常: " + e.getMessage());
            return getDataTable(new ArrayList<>());
        }
    }
}
