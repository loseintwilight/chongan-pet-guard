package com.ruoyi.pet.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.domain.Shop;
import com.ruoyi.pet.domain.UserPetCollect;
import com.ruoyi.pet.mapper.PetMapper;
import com.ruoyi.pet.mapper.ShopMapper;
import com.ruoyi.pet.mapper.UserPetCollectMapper;
import com.ruoyi.pet.service.IPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.domain.Pet;
import org.springframework.transaction.annotation.Transactional;

/**
 * 宠物信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-03
 */
@Service
public class PetServiceImpl implements IPetService {
    @Autowired
    private PetMapper petMapper;

    @Autowired
    private UserPetCollectMapper userPetCollectMapper;

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 查询宠物信息
     *
     * @param petId 宠物信息主键
     * @return 宠物信息
     */
    @Override
    public Pet selectPetByPetId(Long petId) {
        //通过条件查询对应的宠物
        Pet pet = petMapper.selectPetByPetId(petId);
        //实现宠物领养的收藏功能
        //判断当前操作者是否是微信用户
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (StringUtils.isNotEmpty(openId)) {
            //如果openId不为空，那么证明是微信用户
            //通过userId,以及petId,查询用户与宠物之间的中间表
            //获取用户id
            Long userId = SecurityUtils.getUserId();
            //获取收藏状态
            UserPetCollect userPetCollect = userPetCollectMapper.selectOneByUserIdWithPetId(userId, petId);
            //如果存在对应的数据，那么就是收藏了
            //收藏了该宠物
            //未收藏
            pet.setIsCollect(null != userPetCollect ? 1 : 0);
        }
        return pet;
    }

    /**
     * 查询宠物信息列表
     *
     * @param pet 宠物信息
     * @return 宠物信息
     */
    @Override
    public List<Pet> selectPetList(Pet pet) {
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (openId == null) {
            //PC端用户
            //判断是否为管理员角色
            boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
            if (!isAdmin) {
                //非管理员用户，只能查看自己店铺的宠物
                Shop shop = shopMapper.selectShopByUserId(SecurityUtils.getUserId());
                if (null != shop) {
                    pet.setDeptId(shop.getShopId());
                } else {
                    //如果没有店铺，则不返回任何数据
                    pet.setDeptId(-1L); //设置一个不可能存在的店铺ID
                }
            }
            //管理员用户可以看到所有宠物，不需要设置deptId限制
        }
        return petMapper.selectPetList(pet);
    }


    /**
     * 查询所有宠物列表
     *
     * @return
     */
    @Override
    public List<Shop> selectPetShop() {
        System.out.println(petMapper.selectShopList());
        return petMapper.selectShopList();
    }

    /**
     * 新增宠物信息
     *
     * @param pet 宠物信息
     * @return 结果
     */
    @Override
    public int insertPet(Pet pet) {
        pet.setCreateTime(DateUtils.getNowDate());
        return petMapper.insertPet(pet);
    }

    /**
     * 修改宠物信息
     *
     * @param pet 宠物信息
     * @return 结果
     */
    @Override
    public int updatePet(Pet pet) {
        pet.setUpdateTime(DateUtils.getNowDate());
        return petMapper.updatePet(pet);
    }

    /**
     * 批量删除宠物信息
     *
     * @param petIds 需要删除的宠物信息主键
     * @return 结果
     */
//    @Override
//    public int deletePetByPetIds(Long[] petIds) {
//        return petMapper.deletePetByPetIds(petIds);
//    }

    /**
     * 批量删除宠物信息
     *
     * @param petIds 需要删除的宠物信息主键
     * @return 结果
     */
    @Override
    public int deletePetByPetIds(Long[] petIds) {
        return petMapper.updateDeletePetByPetIds(petIds);
    }

    /**
     * 删除宠物信息信息
     *
     * @param petId 宠物信息主键
     * @return 结果
     */
    @Override
    public int deletePetByPetId(Long petId) {
        return petMapper.deletePetByPetId(petId);
    }

    @Override
    @Transactional
    public List<Pet> selectCollectByUser(Pet pet) {
        List<Pet> pets = new ArrayList<>();
        //获取用户id
        Long userId = SecurityUtils.getUserId();
        //查询收藏表
        List<UserPetCollect> userPetCollects = userPetCollectMapper.selectUserPetCollectListByUserId(userId);
        if (userPetCollects != null) {
            //获取宠物ids
            userPetCollects.forEach(userPetCollect -> {
                //获取id
                Long petId = userPetCollect.getPetId();
                Pet petByPetId = petMapper.selectPetByPetId(petId);
                pets.add(petByPetId);
            });
        }
        return pets;
    }

    /**
     * 获取各个宠物的个数
     *
     * @return 结果
     */
    @Override
    public List<Map<String, Object>> getPetTypeNumber() {
        //获取猫的数量
        Integer count0 = petMapper.selectPetCountByPetType(0);
        Integer count1 = petMapper.selectPetCountByPetType(1);
        Integer count2 = petMapper.selectPetCountByPetType(2);
        List<Map<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map0 = new HashMap<>();
        map0.put("value", count0);
        map0.put("name", "猫咪");
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("value", count1);
        map1.put("name", "狗狗");
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("value", count2);
        map2.put("name", "其他");
        list.add(map0);
        list.add(map1);
        list.add(map2);
        return list;
    }
}
