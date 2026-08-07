package com.ruoyi.pet.mapper;

import java.util.List;
import com.ruoyi.pet.domain.UserPetCollect;
import org.apache.ibatis.annotations.Param;

/**
 * 用户收藏宠物领养Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
public interface UserPetCollectMapper 
{
    /**
     * 查询用户收藏宠物领养
     * 
     * @param upcId 用户收藏宠物领养主键
     * @return 用户收藏宠物领养
     */
    public UserPetCollect selectUserPetCollectByUpcId(Long upcId);

    /**
     * 查询用户收藏宠物领养列表
     * 
     * @param userPetCollect 用户收藏宠物领养
     * @return 用户收藏宠物领养集合
     */
    public List<UserPetCollect> selectUserPetCollectList(UserPetCollect userPetCollect);

    /**
     * 新增用户收藏宠物领养
     * 
     * @param userPetCollect 用户收藏宠物领养
     * @return 结果
     */
    public int insertUserPetCollect(UserPetCollect userPetCollect);

    /**
     * 修改用户收藏宠物领养
     * 
     * @param userPetCollect 用户收藏宠物领养
     * @return 结果
     */
    public int updateUserPetCollect(UserPetCollect userPetCollect);

    /**
     * 删除用户收藏宠物领养
     * 
     * @param upcId 用户收藏宠物领养主键
     * @return 结果
     */
    public int deleteUserPetCollectByUpcId(Long upcId);

    /**
     * 批量删除用户收藏宠物领养
     * 
     * @param upcIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserPetCollectByUpcIds(Long[] upcIds);

    UserPetCollect selectOneByUserIdWithPetId(@Param("userId") Long userId,@Param("petId") Long petId);

    int deleteUserPetCollectByUserIdAndPetId(@Param("userId") Long userId,@Param("petId") Long petId);

    List<UserPetCollect> selectUserPetCollectListByUserId(@Param("userId") Long userId);

}
