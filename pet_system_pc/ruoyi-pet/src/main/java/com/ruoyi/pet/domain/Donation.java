package com.ruoyi.pet.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 捐赠对象 tb_donation
 * 
 * @author ruoyi
 * @date 2025-12-03
 */
public class Donation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 捐献记录id */
    private Long donationId;

    /** 捐献者id */
    private Long userId;

    /** 捐赠者名称 */
    @Excel(name = "捐赠者名称")
    private String userName;

    /** 捐献金额 */
    @Excel(name = "捐献金额")
    private Long amount;

    /** 交易后生成的交易id */
    @Excel(name = "交易后生成的交易id")
    private String transctionId;

    /** 支付状态（0-失败，1-成功） */
    @Excel(name = "支付状态", readConverterExp = "0=-失败，1-成功")
    private Long status;

    /** 逻辑删除（0-未删除，1-已删除） */
    private Long isDeleted;

    public void setDonationId(Long donationId) 
    {
        this.donationId = donationId;
    }

    public Long getDonationId() 
    {
        return donationId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setAmount(Long amount) 
    {
        this.amount = amount;
    }

    public Long getAmount() 
    {
        return amount;
    }

    public void setTransctionId(String transctionId) 
    {
        this.transctionId = transctionId;
    }

    public String getTransctionId() 
    {
        return transctionId;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setIsDeleted(Long isDeleted) 
    {
        this.isDeleted = isDeleted;
    }

    public Long getIsDeleted() 
    {
        return isDeleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("donationId", getDonationId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("amount", getAmount())
            .append("transctionId", getTransctionId())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
