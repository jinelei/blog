package cn.jinelei.rainbow.blog.service;

import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.CategoryEntity;
import cn.jinelei.rainbow.blog.entity.TagEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;

import java.util.List;

/**
 * @author zhenlei
 */
public interface ArticleService {
    ArticleEntity addArticle(ArticleEntity articleEntity) throws BlogException;

    void removeArticle(ArticleEntity articleEntity) throws BlogException;

    ArticleEntity updateArticle(ArticleEntity articleEntity) throws BlogException;

    ArticleEntity findArticleById(Integer id) throws BlogException;

    List<ArticleEntity> findArticleList(
            String title, UserEntity author, CategoryEntity category, List<TagEntity> tags,
            Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException;
}
