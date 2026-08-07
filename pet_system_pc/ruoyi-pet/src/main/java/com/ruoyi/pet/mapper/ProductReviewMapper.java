package com.ruoyi.pet.mapper;

import com.ruoyi.pet.domain.ProductReview;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductReviewMapper
{
    public ProductReview selectProductReviewByReviewId(Long reviewId);

    public List<ProductReview> selectProductReviewList(ProductReview productReview);

    public List<ProductReview> selectProductReviewListByResourceId(@Param("resourceId") Long resourceId,
                                                                    @Param("orderBy") String orderBy,
                                                                    @Param("isAsc") String isAsc);

    public int insertProductReview(ProductReview productReview);

    public int updateProductReview(ProductReview productReview);

    public int deleteProductReviewByReviewId(Long reviewId);

    public int deleteProductReviewByReviewIds(Long[] reviewIds);

    public int updateLikeCount(@Param("reviewId") Long reviewId, @Param("increment") int increment);
}
