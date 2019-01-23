package cn.jinelei.rainbow.blog.service;

import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.CategoryEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;

import java.util.List;

/**
 * @author zhenlei
 */
public interface CategoryService {
    CategoryEntity addCategory(CategoryEntity categoryEntity) throws BlogException;

    void removeCategory(CategoryEntity categoryEntity) throws BlogException;

    CategoryEntity updateCategory(CategoryEntity categoryEntity) throws BlogException;

    CategoryEntity findCategoryById(Integer id) throws BlogException;

    List<CategoryEntity> findCategoryList(
            String name, String summary, UserEntity categoryCreator, ArticleEntity articleEntity,
            Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException;
}
