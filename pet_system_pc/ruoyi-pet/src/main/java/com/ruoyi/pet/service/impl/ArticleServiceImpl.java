package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.ArticleMapper;
import com.ruoyi.pet.domain.Article;
import com.ruoyi.pet.service.IArticleService;

/**
 * 科普文章Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-04
 */
@Service
public class ArticleServiceImpl implements IArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 查询科普文章
     *
     * @param articleId 科普文章主键
     * @return 科普文章
     */
    @Override
    public Article selectArticleByArticleId(Long articleId) {
        String openId = SecurityUtils.getLoginUser().getOpenId();
        if (StringUtils.isNotEmpty(openId)) {
            //如果是微信用户查询当前文章，阅读数量+1
            articleMapper.updateAddViewCount(articleId);
        }
        return articleMapper.selectArticleByArticleId(articleId);
    }

    /**
     * 查询科普文章列表
     *
     * @param article 科普文章
     * @return 科普文章
     */
    @Override
    public List<Article> selectArticleList(Article article) {
        return articleMapper.selectArticleList(article);
    }

    /**
     * 新增科普文章
     *
     * @param article 科普文章
     * @return 结果
     */
    @Override
    public int insertArticle(Article article) {
        article.setCreateTime(DateUtils.getNowDate());
        return articleMapper.insertArticle(article);
    }

    /**
     * 修改科普文章
     *
     * @param article 科普文章
     * @return 结果
     */
    @Override
    public int updateArticle(Article article) {
        article.setUpdateTime(DateUtils.getNowDate());
        return articleMapper.updateArticle(article);
    }

    /**
     * 批量删除科普文章
     *
     * @param articleIds 需要删除的科普文章主键
     * @return 结果
     */
//    @Override
//    public int deleteArticleByArticleIds(Long[] articleIds) {
//        return articleMapper.deleteArticleByArticleIds(articleIds);
//    }


    /**
     * 批量删除科普文章
     *
     * @param articleIds 需要删除的科普文章主键
     * @return 结果
     */
    @Override
    public int deleteArticleByArticleIds(Long[] articleIds) {
        return articleMapper.updateDeleteArticleByArticleIds(articleIds);
    }


    /**
     * 删除科普文章信息
     *
     * @param articleId 科普文章主键
     * @return 结果
     */
    @Override
    public int deleteArticleByArticleId(Long articleId) {
        return articleMapper.deleteArticleByArticleId(articleId);
    }
}
