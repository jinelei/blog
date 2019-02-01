package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.controller.TagController;
import cn.jinelei.rainbow.blog.entity.TagEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
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
import java.util.List;
import java.util.Map;

/**
 * @author zhenlei
 */
@RestController
@ResponseBody
@RequestMapping(
        value = "/tags",
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
public class TagControllerImpl implements TagController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    TagService tagService;

    @Autowired
    UserService userService;

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public TagEntity queryEntityById(
            @PathVariable(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        TagEntity tmp = tagService.findTagById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getTagCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        return tmp;
    }

    @Override
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public List<TagEntity> queryEntities(
            @RequestParam Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        String name = params.getOrDefault(Constants.NAME, Constants.DEFAULT_STRING).toString();
        String summary = params.getOrDefault(Constants.SUMMARY, Constants.DEFAULT_STRING).toString();
        String userId = params.getOrDefault(Constants.USER_ID, Constants.DEFAULT_STRING).toString();
        UserEntity userEntity = null;
        if (!StringUtils.isEmpty(userId)) {
            userEntity = userService.findUserById(Integer.valueOf(userId));
        }
        Integer page = Integer.valueOf(params.get(Constants.PAGE).toString());
        Integer size = Integer.valueOf(params.get(Constants.SIZE).toString());
        String[] descFilters = StringUtils.isEmpty(params.getOrDefault(Constants.DESC_FILTERS, Constants.DEFAULT_STRING))
                ? null : params.get(Constants.DESC_FILTERS).toString().split(Constants.COMMA_SPLIT);
        String[] ascFilters = StringUtils.isEmpty(params.getOrDefault(Constants.ASC_FILTERS, Constants.DEFAULT_STRING))
                ? null : params.get(Constants.ASC_FILTERS).toString().split(Constants.COMMA_SPLIT);
        List<TagEntity> tagEntities = tagService.findTagList(name, summary, userEntity, page, size, descFilters, ascFilters);
        return tagEntities;
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
        List<TagEntity> responseEntity = queryEntities(params, operator);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(responseEntity.size());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @Override
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    @ResponseStatus(value = HttpStatus.CREATED)
    public TagEntity saveEntity(
            @RequestBody TagEntity tagEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tagEntity.getTagCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        if (tagEntity.getTagCreator() == null) {
            tagEntity.setTagCreator(operator);
        } else {
            tagEntity.setTagCreator(userService.findUserById(tagEntity.getTagCreator().getUserId()));
        }
        TagEntity opeartionResult = null;
        opeartionResult = tagService.addTag(tagEntity);
        return opeartionResult;
    }

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public TagEntity updateEntity(
            @PathVariable(name = "id") Object id,
            @RequestBody TagEntity tagEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tagEntity.getTagCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        TagEntity tmp = tagService.findTagById(tagEntity.getTagId());
        if (!StringUtils.isEmpty(tagEntity.getName())) {
            tmp.setName(tagEntity.getName());
        }
        if (!StringUtils.isEmpty(tagEntity.getSummary())) {
            tmp.setSummary(tagEntity.getSummary());
        }
        Instant now = Instant.now();
        tmp.setAccessTime(now.toEpochMilli());
        tmp.setModifyTime(now.toEpochMilli());
        tagService.updateTag(tmp);
        return tmp;
    }

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public void deleteEntityById(
            @PathVariable(name = "id") Object id,
            @CurrentUser UserEntity operator) throws BlogException {
        TagEntity tmp = tagService.findTagById(Integer.valueOf(id.toString()));
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getTagCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        try {
            tagService.removeTag(tmp);
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_TAG_SUCCESS).build();
        } catch (Exception e) {
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_TAG_FAILED).build();
        }
    }

}
