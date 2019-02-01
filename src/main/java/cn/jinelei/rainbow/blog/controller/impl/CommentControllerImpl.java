package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.controller.CommentController;
import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.CommentEntity;
import cn.jinelei.rainbow.blog.entity.TagEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import cn.jinelei.rainbow.blog.service.ArticleService;
import cn.jinelei.rainbow.blog.service.CommentService;
import cn.jinelei.rainbow.blog.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhenlei
 */
@RestController
@ResponseBody
@RequestMapping(
        value = "/comments",
        consumes = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_JSON_UTF8_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.MULTIPART_FORM_DATA_VALUE,
                MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                MediaType.ALL_VALUE
        },
        produces = {
                MediaType.APPLICATION_JSON_UTF8_VALUE,
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.ALL_VALUE
        })
public class CommentControllerImpl implements CommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;
    @Autowired
    ArticleService articleService;

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public CommentEntity queryEntityById(
            @PathVariable(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        CommentEntity tmp = commentService.findCommentById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getCommentator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        return tmp;
    }

    @Override
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public List<CommentEntity> queryEntities(
            @RequestParam Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        String content = params.getOrDefault(Constants.CONTENT, Constants.DEFAULT_STRING).toString();
        String userId = params.getOrDefault(Constants.USER_ID, Constants.DEFAULT_STRING).toString();
        String articleId = params.getOrDefault(Constants.ARTICLE_ID, Constants.DEFAULT_STRING).toString();
        UserEntity userEntity = null;
        ArticleEntity articleEntity = null;
        if (!StringUtils.isEmpty(userId)) {
            userEntity = userService.findUserById(Integer.valueOf(userId));
        }
        if (!StringUtils.isEmpty(articleId)) {
            articleEntity = articleService.findArticleById(Integer.valueOf(articleId));
        }
        Integer page = Integer.valueOf(params.get(Constants.PAGE).toString());
        Integer size = Integer.valueOf(params.get(Constants.SIZE).toString());
        String[] descFilters = StringUtils.isEmpty(params.getOrDefault(Constants.DESC_FILTERS, Constants.DEFAULT_STRING))
                ? null : params.get(Constants.DESC_FILTERS).toString().split(Constants.COMMA_SPLIT);
        String[] ascFilters = StringUtils.isEmpty(params.getOrDefault(Constants.ASC_FILTERS, Constants.DEFAULT_STRING))
                ? null : params.get(Constants.ASC_FILTERS).toString().split(Constants.COMMA_SPLIT);
        List<CommentEntity> commentEntities = commentService.findCommentList(content, userEntity, articleEntity, page, size, descFilters, ascFilters);
        return commentEntities;
    }

    @Override
    @RequestMapping(method = {RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity queryEntitiesSize(
            @RequestParam Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!params.containsKey(Constants.SIZE)) {
            params.put(Constants.SIZE, Constants.INVAILD_VALUE);
        }
        if (!params.containsKey(Constants.PAGE)) {
            params.put(Constants.PAGE, Constants.INVAILD_VALUE);
        }
        List<CommentEntity> responseEntity = queryEntities(params, operator);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(responseEntity.size());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @Override
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = CommentEntity.BaseCommentView.class)
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentEntity saveEntity(
            @RequestBody CommentEntity commentEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(commentEntity.getCommentator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        if (commentEntity.getCommentator() == null) {
            commentEntity.setCommentator(operator);
        } else {
            commentEntity.setCommentator(userService.findUserById(commentEntity.getCommentator().getUserId()));
        }
        if (commentEntity.getArticle() != null) {
            if (commentEntity.getArticle().getArticleId() != null) {
                commentEntity.setArticle(articleService.findArticleById(commentEntity.getArticle().getArticleId()));
            }
        }
        CommentEntity opeartionResult = null;
        opeartionResult = commentService.addComment(commentEntity);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI locationUrl = URI.create(String.format("http://%s:%d/comment?id=%d",
                request.getLocalName(), request.getLocalPort(), opeartionResult.getCommentId()));
        httpHeaders.setLocation(locationUrl);
        return opeartionResult;
    }

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public CommentEntity updateEntity(
            @PathVariable(name = "id") Object id,
            @RequestBody CommentEntity commentEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(commentEntity.getCommentator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        CommentEntity tmp = commentService.findCommentById(commentEntity.getCommentId());
        if (!StringUtils.isEmpty(commentEntity.getContent())) {
            tmp.setContent(commentEntity.getContent());
        }
        Instant now = Instant.now();
        tmp.setAccessTime(now.toEpochMilli());
        tmp.setModifyTime(now.toEpochMilli());
        commentService.updateComment(tmp);
        return tmp;
    }

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public void deleteEntityById(
            @PathVariable(name = "id") Object id,
            @CurrentUser UserEntity operator) throws BlogException {
        CommentEntity tmp = commentService.findCommentById(Integer.valueOf(id.toString()));
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getCommentator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        try {
            commentService.removeComment(tmp);
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_COMMENT_SUCCESS).build();
        } catch (Exception e) {
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_COMMENT_FAILED, "id: " + id).build();
        }
    }

}
