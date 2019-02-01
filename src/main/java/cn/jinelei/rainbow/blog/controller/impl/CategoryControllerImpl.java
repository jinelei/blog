package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.controller.CategoryController;
import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.CategoryEntity;
import cn.jinelei.rainbow.blog.entity.CommentEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import cn.jinelei.rainbow.blog.service.ArticleService;
import cn.jinelei.rainbow.blog.service.CategoryService;
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
        value = "/categories",
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
public class CategoryControllerImpl implements CategoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public CategoryEntity queryEntityById(
            @PathVariable(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        CategoryEntity tmp = categoryService.findCategoryById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getCategoryCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        return tmp;
    }


    @Override
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public List<CategoryEntity> queryEntities(
            @RequestParam Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        String name = params.getOrDefault(Constants.NAME, Constants.DEFAULT_STRING).toString();
        String summary = params.getOrDefault(Constants.SUMMARY, Constants.DEFAULT_STRING).toString();
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
        List<CategoryEntity> categoryEntities = categoryService.findCategoryList(name, summary, userEntity, articleEntity, page, size, descFilters, ascFilters);
        return categoryEntities;
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
        List<CategoryEntity> responseEntity = queryEntities(params, operator);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(responseEntity.size());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @Override
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryEntity saveEntity(
            @RequestBody CategoryEntity categoryEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(categoryEntity.getCategoryCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        if (categoryEntity.getCategoryCreator() == null) {
            categoryEntity.setCategoryCreator(operator);
        } else {
            categoryEntity.setCategoryCreator(userService.findUserById(categoryEntity.getCategoryCreator().getUserId()));
        }
        CategoryEntity opeartionResult = null;
        opeartionResult = categoryService.addCategory(categoryEntity);
        return opeartionResult;
    }

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public CategoryEntity updateEntity(
            @PathVariable(name = "id") Object id,
            @RequestBody CategoryEntity categoryEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(categoryEntity.getCategoryCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        CategoryEntity tmp = categoryService.findCategoryById(categoryEntity.getCategoryId());
        if (!StringUtils.isEmpty(categoryEntity.getName())) {
            tmp.setName(categoryEntity.getName());
        }
        if (!StringUtils.isEmpty(categoryEntity.getSummary())) {
            tmp.setSummary(categoryEntity.getSummary());
        }
        Instant now = Instant.now();
        tmp.setAccessTime(now.toEpochMilli());
        tmp.setModifyTime(now.toEpochMilli());
        categoryService.updateCategory(tmp);
        return tmp;
    }

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public void deleteEntityById(
            @PathVariable(name = "id") Object id,
            @CurrentUser UserEntity operator) throws BlogException {
        CategoryEntity tmp = categoryService.findCategoryById(Integer.valueOf(id.toString()));
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getCategoryCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        try {
            categoryService.removeCategory(tmp);
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_CATEGORY_SUCCESS).build();
        } catch (Exception e) {
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_CATEGORY_FAILED, "id: " + id).build();
        }
    }

}