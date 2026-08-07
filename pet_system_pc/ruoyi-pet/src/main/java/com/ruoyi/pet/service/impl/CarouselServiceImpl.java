package com.ruoyi.pet.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.CarouselMapper;
import com.ruoyi.pet.domain.Carousel;
import com.ruoyi.pet.service.ICarouselService;

/**
 * 轮播图Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-02
 */
@Service
public class CarouselServiceImpl implements ICarouselService
{
    @Autowired
    private CarouselMapper carouselMapper;

    /**
     * 查询轮播图
     *
     * @param carouselId 轮播图主键
     * @return 轮播图
     */
    @Override
    public Carousel selectCarouselByCarouselId(Long carouselId)
    {
        return carouselMapper.selectCarouselByCarouselId(carouselId);
    }

    /**
     * 查询轮播图列表
     *
     * @param carousel 轮播图
     * @return 轮播图
     */
    @Override
    public List<Carousel> selectCarouselList(Carousel carousel)
    {
        return carouselMapper.selectCarouselList(carousel);
    }

    /**
     * 新增轮播图
     *
     * @param carousel 轮播图
     * @return 结果
     */
    @Override
    public int insertCarousel(Carousel carousel)
    {
        carousel.setCreateTime(DateUtils.getNowDate());
        return carouselMapper.insertCarousel(carousel);
    }

    /**
     * 修改轮播图
     *
     * @param carousel 轮播图
     * @return 结果
     */
    @Override
    public int updateCarousel(Carousel carousel)
    {
        carousel.setUpdateTime(DateUtils.getNowDate());
        return carouselMapper.updateCarousel(carousel);
    }

    /**
     * 批量删除轮播图
     *
     * @param carouselIds 需要删除的轮播图主键
     * @return 结果
     */
    @Override
    public int deleteCarouselByCarouselIds(Long[] carouselIds)
    {
        return carouselMapper.deleteCarouselByCarouselIds(carouselIds);
    }

    /**
     * 删除轮播图信息
     *
     * @param carouselId 轮播图主键
     * @return 结果
     */
    @Override
    public int deleteCarouselByCarouselId(Long carouselId)
    {
        return carouselMapper.deleteCarouselByCarouselId(carouselId);
    }
}
