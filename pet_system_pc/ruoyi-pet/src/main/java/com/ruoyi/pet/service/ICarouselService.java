package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.Carousel;

/**
 * 轮播图Service接口
 *
 * @author ruoyi
 * @date 2025-12-02
 */
public interface ICarouselService
{
    /**
     * 查询轮播图
     *
     * @param carouselId 轮播图主键
     * @return 轮播图
     */
    public Carousel selectCarouselByCarouselId(Long carouselId);

    /**
     * 查询轮播图列表
     *
     * @param carousel 轮播图
     * @return 轮播图集合
     */
    public List<Carousel> selectCarouselList(Carousel carousel);

    /**
     * 新增轮播图
     *
     * @param carousel 轮播图
     * @return 结果
     */
    public int insertCarousel(Carousel carousel);

    /**
     * 修改轮播图
     *
     * @param carousel 轮播图
     * @return 结果
     */
    public int updateCarousel(Carousel carousel);

    /**
     * 批量删除轮播图
     *
     * @param carouselIds 需要删除的轮播图主键集合
     * @return 结果
     */
    public int deleteCarouselByCarouselIds(Long[] carouselIds);

    /**
     * 删除轮播图信息
     *
     * @param carouselId 轮播图主键
     * @return 结果
     */
    public int deleteCarouselByCarouselId(Long carouselId);
}
