package com.ruoyi.pet.mapper;

import com.ruoyi.pet.domain.ProductComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductCommentMapper {

    List<ProductComment> selectProductCommentList(ProductComment query);

    List<ProductComment> selectCommentsByReviewIds(@Param("reviewIds") List<Long> reviewIds);

    List<ProductComment> selectRepliesByParentIds(@Param("parentIds") List<Long> parentIds);

    ProductComment selectProductCommentById(Long commentId);

    int insertProductComment(ProductComment comment);

    int updateLikeCount(@Param("commentId") Long commentId, @Param("delta") int delta);
}