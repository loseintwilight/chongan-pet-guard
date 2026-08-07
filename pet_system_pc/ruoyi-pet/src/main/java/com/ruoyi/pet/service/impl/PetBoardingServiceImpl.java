package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.domain.Shop;
import com.ruoyi.pet.mapper.ShopMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.PetBoardingMapper;
import com.ruoyi.pet.domain.PetBoarding;
import com.ruoyi.pet.service.IPetBoardingService;

/**
 * 宠物托养Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-04
 */
@Service
@Slf4j
public class PetBoardingServiceImpl implements IPetBoardingService {
    @Autowired
    private PetBoardingMapper petBoardingMapper;

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 查询宠物托养
     *
     * @param boardingId 宠物托养主键
     * @return 宠物托养
     */
    @Override
    public PetBoarding selectPetBoardingByBoardingId(Long boardingId) {
        return petBoardingMapper.selectPetBoardingByBoardingId(boardingId);
    }

    /**
     * 查询宠物托养列表
     *
     * @param petBoarding 宠物托养
     * @return 宠物托养
     */
    @Override
    public List<PetBoarding> selectPetBoardingList(PetBoarding petBoarding) {
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (openId == null) {
            //PC端用户
            //判断是否为管理员角色
            boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
            if (!isAdmin) {
                //非管理员用户，只能查看自己店铺的宠物
                Shop shop = shopMapper.selectShopByUserId(SecurityUtils.getUserId());
                if (null != shop) {
                    petBoarding.setShopId(shop.getShopId());
                } else {
                    //如果没有店铺，则不返回任何数据
                    petBoarding.setShopId(-1L); //设置一个不可能存在的店铺ID
                }
            }
            //管理员用户可以看到所有宠物，不需要设置deptId限制
        }
        //判断是否是微信用户，如果是，那么只查询当前用户的托养申请
        if (StringUtils.isNotEmpty(openId)) {
            //获取当前用户id
            Long userId = SecurityUtils.getUserId();
            petBoarding.setUserId(userId);
        }
        return petBoardingMapper.selectPetBoardingList(petBoarding);
    }

    /**
     * 新增宠物托养
     *
     * @param petBoarding 宠物托养
     * @return 结果
     */
    @Override
    public int insertPetBoarding(PetBoarding petBoarding) {
        //获取当前的操作用户信息
        petBoarding.setUserId(SecurityUtils.getUserId());
        petBoarding.setCreateTime(DateUtils.getNowDate());
        return petBoardingMapper.insertPetBoarding(petBoarding);
    }

    /**
     * 修改宠物托养
     *
     * @param petBoarding 宠物托养
     * @return 结果
     */
    @Override
    public int updatePetBoarding(PetBoarding petBoarding) {
        petBoarding.setUpdateTime(DateUtils.getNowDate());

        PetBoarding boarding = petBoardingMapper.selectPetBoardingByBoardingId(petBoarding.getBoardingId());

        // 检查是否已经审核过
//        if (boarding.getReviewTime() != null &&
//                (petBoarding.getStatus() == 6 || petBoarding.getStatus() == 7)) {
//            throw new ServiceException("不可重复审核");
//        }

        // 如果是审核操作（通过或不通过）
        if (petBoarding.getStatus() == 6 || petBoarding.getStatus() == 7) {
            log.info("审核状态改为：通过、不通过");
            // 设置审核时间
            petBoarding.setReviewTime(DateUtils.getNowDate());
            // 设置审核人ID（假设有setReviewerId方法）
            Long userId = SecurityUtils.getUserId();
            petBoarding.setReviewerId(userId);  // 正确设置审核人ID
        }
        return petBoardingMapper.updatePetBoarding(petBoarding);
    }


    /**
     * 批量删除宠物托养
     *
     * @param boardingIds 需要删除的宠物托养主键
     * @return 结果
     */
//    @Override
//    public int deletePetBoardingByBoardingIds(Long[] boardingIds) {
//        return petBoardingMapper.deletePetBoardingByBoardingIds(boardingIds);
//    }

    /**
     * 批量删除宠物托养
     *
     * @param boardingIds 需要删除的宠物托养主键
     * @return 结果
     */
    @Override
    public int deletePetBoardingByBoardingIds(Long[] boardingIds) {
        return petBoardingMapper.updateDeletePetBoardingByBoardingIds(boardingIds);
    }

    /**
     * 删除宠物托养信息
     *
     * @param boardingId 宠物托养主键
     * @return 结果
     */
    @Override
    public int deletePetBoardingByBoardingId(Long boardingId) {
        return petBoardingMapper.deletePetBoardingByBoardingId(boardingId);
    }
}
