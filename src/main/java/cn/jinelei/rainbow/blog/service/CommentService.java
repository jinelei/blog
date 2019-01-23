package cn.jinelei.rainbow.blog.service;

import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.CommentEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;

import java.util.List;

/**
 * @author zhenlei
 */
public interface CommentService {
    CommentEntity addComment(CommentEntity commentEntity) throws BlogException;

    void removeComment(CommentEntity commentEntity) throws BlogException;

    CommentEntity updateComment(CommentEntity commentEntity) throws BlogException;

    CommentEntity findCommentById(Integer id) throws BlogException;

    List<CommentEntity> findCommentList(
            String content, UserEntity commentCreator, ArticleEntity articleEntity,
            Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException;
}

