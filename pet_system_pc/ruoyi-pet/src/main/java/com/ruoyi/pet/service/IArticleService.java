package com.ruoyi.pet.service;

import java.util.List;
import com.ruoyi.pet.domain.Article;

/**
 * 科普文章Service接口
 * 
 * @author ruoyi
 * @date 2025-12-04
 */
public interface IArticleService 
{
    /**
     * 查询科普文章
     * 
     * @param articleId 科普文章主键
     * @return 科普文章
     */
    public Article selectArticleByArticleId(Long articleId);

    /**
     * 查询科普文章列表
     * 
     * @param article 科普文章
     * @return 科普文章集合
     */
    public List<Article> selectArticleList(Article article);

    /**
     * 新增科普文章
     * 
     * @param article 科普文章
     * @return 结果
     */
    public int insertArticle(Article article);

    /**
     * 修改科普文章
     * 
     * @param article 科普文章
     * @return 结果
     */
    public int updateArticle(Article article);

    /**
     * 批量删除科普文章
     * 
     * @param articleIds 需要删除的科普文章主键集合
     * @return 结果
     */
    public int deleteArticleByArticleIds(Long[] articleIds);

    /**
     * 删除科普文章信息
     * 
     * @param articleId 科普文章主键
     * @return 结果
     */
    public int deleteArticleByArticleId(Long articleId);
}
