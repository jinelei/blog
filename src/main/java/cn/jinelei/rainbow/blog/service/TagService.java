package cn.jinelei.rainbow.blog.service;

import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.TagEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhenlei
 */
public interface TagService {
    TagEntity addTag(TagEntity tagEntity) throws BlogException;

    void removeTag(TagEntity tagEntity) throws BlogException;

    TagEntity updateTag(TagEntity tagEntity) throws BlogException;

    TagEntity findTagById(Integer id) throws BlogException;

    List<TagEntity> findTagList(
            String name, String summary, UserEntity tagCreator,
            Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException;
}
