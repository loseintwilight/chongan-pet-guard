package com.ruoyi.pet.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pet.domain.ProductComment;
import com.ruoyi.pet.domain.ProductReview;
import com.ruoyi.pet.mapper.ProductCommentMapper;
import com.ruoyi.pet.mapper.ProductReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Anonymous
@RestController
@RequestMapping("/mall/review")
public class MallReviewController extends BaseController {

    @Autowired
    private ProductReviewMapper productReviewMapper;

    @Autowired
    private ProductCommentMapper productCommentMapper;

    @GetMapping("/list")
    public AjaxResult list(@RequestParam Long resourceId,
                           @RequestParam(value = "orderBy", defaultValue = "create_time") String orderBy,
                           @RequestParam(value = "isAsc", defaultValue = "desc") String isAsc,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        ProductReview query = new ProductReview();
        query.setResourceId(resourceId);
        List<ProductReview> allReviews = productReviewMapper.selectProductReviewList(query);

        if (allReviews == null || allReviews.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("rows", new ArrayList<>());
            result.put("total", 0);
            return success(result);
        }

        sortReviews(allReviews, orderBy, isAsc);

        int total = allReviews.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<ProductReview> pagedReviews = start < total ? allReviews.subList(start, end) : new ArrayList<>();

        if (!pagedReviews.isEmpty()) {
            List<Long> reviewIds = new ArrayList<>();
            for (ProductReview r : pagedReviews) {
                reviewIds.add(r.getReviewId());
            }
            List<ProductComment> comments = productCommentMapper.selectCommentsByReviewIds(reviewIds);
            Map<Long, List<ProductComment>> commentMap = new HashMap<>();
            for (ProductComment c : comments) {
                commentMap.computeIfAbsent(c.getReviewId(), k -> new ArrayList<>()).add(c);
            }
            for (ProductReview r : pagedReviews) {
                r.setCommentList(commentMap.get(r.getReviewId()));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", pagedReviews);
        result.put("total", total);
        return success(result);
    }

    @GetMapping("/listWithReplies")
    public AjaxResult listWithReplies(@RequestParam Long resourceId,
                                       @RequestParam(value = "orderBy", defaultValue = "create_time") String orderBy,
                                       @RequestParam(value = "isAsc", defaultValue = "desc") String isAsc,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "20") Integer pageSize) {
        return list(resourceId, orderBy, isAsc, pageNum, pageSize);
    }

    @GetMapping("/stats/{resourceId}")
    public AjaxResult getStats(@PathVariable("resourceId") Long resourceId) {
        ProductReview query = new ProductReview();
        query.setResourceId(resourceId);
        query.setStatus(1L);
        List<ProductReview> reviews = productReviewMapper.selectProductReviewList(query);

        int totalCount = reviews.size();
        double avgRating = 0;
        if (totalCount > 0) {
            long totalRating = 0;
            for (ProductReview review : reviews) {
                totalRating += review.getRating() != null ? review.getRating() : 5;
            }
            avgRating = (double) totalRating / totalCount;
        }

        int fiveCount = 0, fourCount = 0, threeCount = 0, twoCount = 0, oneCount = 0;
        for (ProductReview review : reviews) {
            Long rating = review.getRating() != null ? review.getRating() : 5;
            switch (rating.intValue()) {
                case 5: fiveCount++; break;
                case 4: fourCount++; break;
                case 3: threeCount++; break;
                case 2: twoCount++; break;
                case 1: oneCount++; break;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("avgRating", Math.round(avgRating * 10) / 10.0);
        stats.put("fiveCount", fiveCount);
        stats.put("fourCount", fourCount);
        stats.put("threeCount", threeCount);
        stats.put("twoCount", twoCount);
        stats.put("oneCount", oneCount);

        return success(stats);
    }

    @PostMapping("/like/{commentId}")
    public AjaxResult like(@PathVariable("commentId") Long commentId) {
        int result = productCommentMapper.updateLikeCount(commentId, 1);
        return result > 0 ? success("点赞成功") : error("点赞失败");
    }

    @PostMapping("/unlike/{commentId}")
    public AjaxResult unlike(@PathVariable("commentId") Long commentId) {
        int result = productCommentMapper.updateLikeCount(commentId, -1);
        return result > 0 ? success("取消点赞成功") : error("取消点赞失败");
    }

    @PostMapping("/add")
    public AjaxResult add(@RequestBody ProductComment comment) {
        if (comment.getReviewId() == null || comment.getContent() == null || comment.getContent().isEmpty()) {
            return error("参数不完整");
        }
        comment.setStatus(1L);
        comment.setLikeCount(0);
        int rows = productCommentMapper.insertProductComment(comment);
        return rows > 0 ? success(comment) : error("评论失败");
    }

    private void sortReviews(List<ProductReview> reviews, String orderBy, String isAsc) {
        boolean asc = "asc".equalsIgnoreCase(isAsc);
        Comparator<ProductReview> comparator;
        switch (orderBy) {
            case "like_count":
                comparator = Comparator.comparing(
                    r -> r.getLikeCount() != null ? r.getLikeCount() : 0);
                break;
            case "rating":
                comparator = Comparator.comparing(
                    r -> r.getRating() != null ? r.getRating() : 5);
                break;
            default:
                comparator = Comparator.comparing(
                    r -> r.getCreateTime() != null ? r.getCreateTime() : new Date(0));
                break;
        }
        reviews.sort(asc ? comparator : comparator.reversed());
    }
}