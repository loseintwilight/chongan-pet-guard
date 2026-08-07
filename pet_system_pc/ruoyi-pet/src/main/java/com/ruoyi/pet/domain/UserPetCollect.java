package com.ruoyi.pet.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户收藏宠物领养对象 tb_user_pet_collect
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
public class UserPetCollect extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键  */
    private Long upcId;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 收藏的宠物id */
    @Excel(name = "收藏的宠物id")
    private Long petId;

    public void setUpcId(Long upcId) 
    {
        this.upcId = upcId;
    }

    public Long getUpcId() 
    {
        return upcId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setPetId(Long petId) 
    {
        this.petId = petId;
    }

    public Long getPetId() 
    {
        return petId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("upcId", getUpcId())
            .append("userId", getUserId())
            .append("petId", getPetId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
