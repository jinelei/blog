package cn.jinelei.rainbow.blog.controller;

import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.BrowsePrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.CommentPrivilege;

import java.util.List;

/**
 * @author zhenlei
 */
public interface ArticleController extends EntityController<ArticleEntity> {

    /**
     * 获取浏览权限
     *
     * @return
     */
    List<BrowsePrivilege> getBrowsePrivilege();

    /**
     * 获取评论权限
     *
     * @return
     */
    List<CommentPrivilege> getCommentPrivilege();
}

