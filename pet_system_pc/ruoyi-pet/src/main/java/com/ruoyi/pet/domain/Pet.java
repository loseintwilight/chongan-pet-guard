package com.ruoyi.pet.domain;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 宠物信息对象 tb_pet
 *
 * @author ruoyi
 * @date 2025-12-04
 */
public class Pet extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 宠物id
     */
    private Long petId;

    /**
     * 宠物名称
     */
    @Excel(name = "宠物名称")
    private String name;

    /**
     * 宠物图片
     */
    @Excel(name = "宠物图片")
    private String images;

    /**
     * 物种
     */
    @Excel(name = "物种")
    private Long species;

    /**
     * 品种
     */
    @Excel(name = "品种")
    private String breed;

    /**
     * 性别
     */
    @Excel(name = "性别")
    private Long gender;

    /**
     * 年龄
     */
    @Excel(name = "年龄")
    private Long age;

    /**
     * 健康状态描述
     */
    private String healthInfo;

    /**
     * 是否绝育（0-未绝育 1-已绝育 2- 未知）默认是2
     */
    private Long neuter;

    /**
     * 是否免疫( 0 -已免疫 , 1-未免疫 , 2 - 未知) 默认是2
     */
    private Long immunity;

    /**
     * 是否驱虫 (0 - 未驱虫  1- 已驱虫 2- 未知) 默认是2
     */
    private Long insectRepellent;

    /**
     * 驱虫周期（全部以日的形式展示）
     */
    private Long dewormingInterval;

    /**
     * 上回驱虫期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date insectRepellentLastTime;

    /**
     * 宠物描述
     */
    private String description;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private Long status;

    /**
     * 发现、所在地
     */
    private String location;

    /**
     * 所属店铺或者站点
     */
    @Excel(name = "所属店铺或者站点")
    private Long deptId;

    private int isCollect;

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    /**
     * 逻辑删除字段（0-未删除 1-已删除）
     */
    private Long isDeleted;

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getImages() {
        return images;
    }

    public void setSpecies(Long species) {
        this.species = species;
    }

    public Long getSpecies() {
        return species;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getBreed() {
        return breed;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public Long getGender() {
        return gender;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Long getAge() {
        return age;
    }

    public void setHealthInfo(String healthInfo) {
        this.healthInfo = healthInfo;
    }

    public String getHealthInfo() {
        return healthInfo;
    }

    public void setNeuter(Long neuter) {
        this.neuter = neuter;
    }

    public Long getNeuter() {
        return neuter;
    }

    public void setImmunity(Long immunity) {
        this.immunity = immunity;
    }

    public Long getImmunity() {
        return immunity;
    }

    public void setInsectRepellent(Long insectRepellent) {
        this.insectRepellent = insectRepellent;
    }

    public Long getInsectRepellent() {
        return insectRepellent;
    }

    public void setDewormingInterval(Long dewormingInterval) {
        this.dewormingInterval = dewormingInterval;
    }

    public Long getDewormingInterval() {
        return dewormingInterval;
    }

    public void setInsectRepellentLastTime(Date insectRepellentLastTime) {
        this.insectRepellentLastTime = insectRepellentLastTime;
    }

    public Date getInsectRepellentLastTime() {
        return insectRepellentLastTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setIsDeleted(Long isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getIsDeleted() {
        return isDeleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("petId", getPetId())
                .append("name", getName())
                .append("images", getImages())
                .append("species", getSpecies())
                .append("breed", getBreed())
                .append("gender", getGender())
                .append("age", getAge())
                .append("healthInfo", getHealthInfo())
                .append("neuter", getNeuter())
                .append("immunity", getImmunity())
                .append("insectRepellent", getInsectRepellent())
                .append("dewormingInterval", getDewormingInterval())
                .append("insectRepellentLastTime", getInsectRepellentLastTime())
                .append("description", getDescription())
                .append("status", getStatus())
                .append("location", getLocation())
                .append("deptId", getDeptId())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("remark", getRemark())
                .append("isDeleted", getIsDeleted())
                .append("isCollect", getIsCollect())
                .toString();
    }
}
