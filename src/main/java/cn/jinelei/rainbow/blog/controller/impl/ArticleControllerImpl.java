package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.controller.ArticleController;
import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.CategoryEntity;
import cn.jinelei.rainbow.blog.entity.TagEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.BrowsePrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.CommentPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import cn.jinelei.rainbow.blog.service.ArticleService;
import cn.jinelei.rainbow.blog.service.CategoryService;
import cn.jinelei.rainbow.blog.service.TagService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhenlei
 */
@RestController
@ResponseBody
@RequestMapping(
        value = "/articles",
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
public class ArticleControllerImpl implements ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    TagService tagService;

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ArticleEntity queryEntityById(
            @PathVariable(name = "id") Integer id,
            @CurrentUser(require = false) UserEntity operator) throws BlogException {
        ArticleEntity tmp = articleService.findArticleById(id);
        switch (tmp.getBrowsePrivilege()) {
            case ALLOW_ALL:
                return tmp;
            case ALLOW_MYSELF:
                if (operator != null
                        && tmp.getAuthor().getUserId() != null
                        && tmp.getAuthor().getUserId().equals(operator.getUserId())) {
                    return tmp;
                } else {
                    throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
                }
            case ALLOW_FRIEND:
            case INVALID_VALUE:
            default:
                throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();

        }
    }

    @Override
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public List<ArticleEntity> queryEntities(
            @RequestParam Map<String, Object> params,
            @CurrentUser(require = false) UserEntity operator) throws BlogException {
        String title = params.getOrDefault(Constants.TITLE, Constants.DEFAULT_STRING).toString();
        UserEntity author = null;
        if (params.containsKey(Constants.AUTHOR)) {
            author = userService.findUserById(
                    Integer.valueOf(params.getOrDefault(Constants.AUTHOR, Constants.DEFAULT_STRING).toString()));
        }
        CategoryEntity category = null;
        if (params.containsKey(Constants.CATEGORY)) {
            category = categoryService.findCategoryById(
                    Integer.valueOf(params.getOrDefault(Constants.CATEGORY, Constants.DEFAULT_STRING).toString()));
        }
        List<TagEntity> tags = null;
        if (params.getOrDefault(Constants.TAGS, Constants.DEFAULT_STRING) != Constants.DEFAULT_STRING) {
            for (String tmp : params.getOrDefault(Constants.TAGS, Constants.DEFAULT_STRING).toString().split(Constants.COMMA_SPLIT)) {
                if (!StringUtils.isEmpty(tmp)) {
                    try {
                        TagEntity tagEntity = tagService.findTagById(Integer.valueOf(tmp));
                        if (tagEntity != null) {
                            if (tags == null) {
                                tags = new ArrayList<>();
                            }
                            tags.add(tagEntity);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        Integer page = Integer.valueOf(params.get(Constants.PAGE).toString());
        Integer size = Integer.valueOf(params.get(Constants.SIZE).toString());
        String[] descFilters = StringUtils.isEmpty(params.getOrDefault(Constants.DESC_FILTERS, Constants.DEFAULT_STRING))
                ? null : params.get(Constants.DESC_FILTERS).toString().split(Constants.COMMA_SPLIT);
        String[] ascFilters = StringUtils.isEmpty(params.getOrDefault(Constants.ASC_FILTERS, Constants.DEFAULT_STRING))
                ? null : params.get(Constants.ASC_FILTERS).toString().split(Constants.COMMA_SPLIT);
        List<ArticleEntity> tmp = articleService.findArticleList(title, author, category, tags, page, size, descFilters, ascFilters);
        List<ArticleEntity> articleEntities = new ArrayList<>(tmp.size());
        for (ArticleEntity articleEntity : tmp) {
            switch (articleEntity.getBrowsePrivilege()) {
                case ALLOW_ALL:
                    articleEntities.add(articleEntity);
                    continue;
                case ALLOW_MYSELF:
                    if (operator != null
                            && articleEntity.getAuthor().getUserId() != null
                            && articleEntity.getAuthor().getUserId().equals(operator.getUserId())) {
                        articleEntities.add(articleEntity);
                        continue;
                    } else {
                        continue;
                    }
                case ALLOW_FRIEND:
                case INVALID_VALUE:
                default:
                    continue;

            }
        }
        if (articleEntities.size() == 0) {
            throw new BlogException.Builder(BlogExceptionEnum.QUERY_DATA_FAILED, params.toString()).build();
        }
        return articleEntities;
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
        List<ArticleEntity> responseEntity = queryEntities(params, operator);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(responseEntity.size());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @Override
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ArticleEntity saveEntity(
            @RequestBody ArticleEntity articleEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(articleEntity.getAuthor().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        if (articleEntity.getAuthor() == null) {
            articleEntity.setAuthor(operator);
        } else {
            articleEntity.setAuthor(userService.findUserById(articleEntity.getAuthor().getUserId()));
        }
        ArticleEntity opeartionResult = null;
        opeartionResult = articleService.addArticle(articleEntity);
        return opeartionResult;
    }

    @Override
    @RequestMapping(value = "/id/{id}",method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ArticleEntity updateEntity(
            @PathVariable(name = "id") Object id,
            @RequestBody ArticleEntity articleEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(articleEntity.getAuthor().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        ArticleEntity tmp = articleService.findArticleById(articleEntity.getArticleId());
        if (!StringUtils.isEmpty(articleEntity.getTitle())) {
            tmp.setTitle(articleEntity.getTitle());
        }
        if (!StringUtils.isEmpty(articleEntity.getContent())) {
            tmp.setContent(articleEntity.getContent());
        }
        if (articleEntity.getTags() != null) {
            tmp.setTags(articleEntity.getTags());
        }
        if (articleEntity.getComments() != null) {
            tmp.setComments(articleEntity.getComments());
        }
        if (articleEntity.getCategory() != null) {
            tmp.setCategory(articleEntity.getCategory());
        }
        if (articleEntity.getBrowsePrivilege() != null) {
            tmp.setBrowsePrivilege(articleEntity.getBrowsePrivilege());
        }
        if (articleEntity.getCommentPrivilege() != null) {
            tmp.setCommentPrivilege(articleEntity.getCommentPrivilege());
        }
        Instant now = Instant.now();
        tmp.setAccessTime(now.toEpochMilli());
        tmp.setModifyTime(now.toEpochMilli());
        articleService.updateArticle(tmp);
        return tmp;
    }

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public void deleteEntityById(
            @PathVariable(name = "id") Object id,
            @CurrentUser UserEntity operator) throws BlogException {
        ArticleEntity tmp = articleService.findArticleById(Integer.valueOf(id.toString()));
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getAuthor().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        try {
            articleService.removeArticle(tmp);
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_ARTICLE_SUCCESS).build();
        } catch (Exception e) {
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_ARTICLE_FAILED, "id: " + id).build();
        }
    }

    @Override
    @RequestMapping(value = "/browses", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public List<BrowsePrivilege> getBrowsePrivilege() {
        List<BrowsePrivilege> privileges = new ArrayList<>();
        privileges.add(BrowsePrivilege.ALLOW_MYSELF);
        privileges.add(BrowsePrivilege.ALLOW_FRIEND);
        privileges.add(BrowsePrivilege.ALLOW_ALL);
        return privileges;
    }

    @Override
    @RequestMapping(value = "/comments", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public List<CommentPrivilege> getCommentPrivilege() {
        List<CommentPrivilege> privileges = new ArrayList<>();
        privileges.add(CommentPrivilege.ALLOW_MYSELF);
        privileges.add(CommentPrivilege.ALLOW_FRIEND);
        privileges.add(CommentPrivilege.ALLOW_ALL);
        return privileges;
    }

}
