package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.UserPetCollectMapper;
import com.ruoyi.pet.domain.UserPetCollect;
import com.ruoyi.pet.service.IUserPetCollectService;

/**
 * 用户收藏宠物领养Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-15
 */
@Service
public class UserPetCollectServiceImpl implements IUserPetCollectService {
    @Autowired
    private UserPetCollectMapper userPetCollectMapper;

    /**
     * 查询用户收藏宠物领养
     *
     * @param upcId 用户收藏宠物领养主键
     * @return 用户收藏宠物领养
     */
    @Override
    public UserPetCollect selectUserPetCollectByUpcId(Long upcId) {
        return userPetCollectMapper.selectUserPetCollectByUpcId(upcId);
    }

    /**
     * 查询用户收藏宠物领养列表
     *
     * @param userPetCollect 用户收藏宠物领养
     * @return 用户收藏宠物领养
     */
    @Override
    public List<UserPetCollect> selectUserPetCollectList(UserPetCollect userPetCollect) {
        return userPetCollectMapper.selectUserPetCollectList(userPetCollect);
    }

    /**
     * 新增用户收藏宠物领养
     *
     * @param userPetCollect 用户收藏宠物领养
     * @return 结果
     */
    @Override
    public int insertUserPetCollect(UserPetCollect userPetCollect) {
        userPetCollect.setUserId(SecurityUtils.getUserId());
        userPetCollect.setCreateTime(DateUtils.getNowDate());
        return userPetCollectMapper.insertUserPetCollect(userPetCollect);
    }

    /**
     * 修改用户收藏宠物领养
     *
     * @param userPetCollect 用户收藏宠物领养
     * @return 结果
     */
    @Override
    public int updateUserPetCollect(UserPetCollect userPetCollect) {
        return userPetCollectMapper.updateUserPetCollect(userPetCollect);
    }

    /**
     * 批量删除用户收藏宠物领养
     *
     * @param upcIds 需要删除的用户收藏宠物领养主键
     * @return 结果
     */
    @Override
    public int deleteUserPetCollectByUpcIds(Long[] upcIds) {
        return userPetCollectMapper.deleteUserPetCollectByUpcIds(upcIds);
    }

    /**
     * 删除用户收藏宠物领养信息
     *
     * @param upcId 用户收藏宠物领养主键
     * @return 结果
     */
    @Override
    public int deleteUserPetCollectByUpcId(Long upcId) {
        return userPetCollectMapper.deleteUserPetCollectByUpcId(upcId);
    }

    /**
     * 取消收藏
     *
     * @param petId
     * @return
     */
    @Override
    public int deleteUserPetCollectByUserIdAndPetId(Long petId) {
        Long userId = SecurityUtils.getUserId();
        return userPetCollectMapper.deleteUserPetCollectByUserIdAndPetId(userId, petId);
    }
}
