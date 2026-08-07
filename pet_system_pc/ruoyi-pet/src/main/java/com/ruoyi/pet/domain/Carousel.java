package com.ruoyi.pet.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 轮播图对象 tb_carousel
 *
 * @author ruoyi
 * @date 2025-12-02
 */
public class Carousel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long carouselId;

    /** 图片 */
    @Excel(name = "图片")
    private String images;

    /** 跳转到的链接 */
    @Excel(name = "跳转到的链接")
    private String linkUrl;

    /** 链接类型（0-无，1-内部页面，2-外部页面）默认是0 */
    @Excel(name = "链接类型", readConverterExp = "0=-无，1-内部页面，2-外部页面")
    private Long linkType;

    /** 图片上的文字 */
    @Excel(name = "图片上的文字")
    private String title;

    /** 排序，默认是0 */
    @Excel(name = "排序，默认是0")
    private Long sort;

    /** 状态（0-表示禁用，1-表示可用） */
    @Excel(name = "状态", readConverterExp = "0=-表示禁用，1-表示可用")
    private Long status;

    public void setCarouselId(Long carouselId)
    {
        this.carouselId = carouselId;
    }

    public Long getCarouselId()
    {
        return carouselId;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getImages()
    {
        return images;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLinkType(Long linkType)
    {
        this.linkType = linkType;
    }

    public Long getLinkType()
    {
        return linkType;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setSort(Long sort)
    {
        this.sort = sort;
    }

    public Long getSort()
    {
        return sort;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("carouselId", getCarouselId())
                .append("images", getImages())
                .append("linkUrl", getLinkUrl())
                .append("linkType", getLinkType())
                .append("title", getTitle())
                .append("sort", getSort())
                .append("status", getStatus())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("remark", getRemark())
                .toString();
    }
}
