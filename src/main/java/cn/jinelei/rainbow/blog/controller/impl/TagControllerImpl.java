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
    @RequestMapping(value = "/tag", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public ResponseEntity<TagEntity> saveEntity(
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
        HttpHeaders httpHeaders = new HttpHeaders();
        URI locationUrl = URI.create(String.format("http://%s:%d/tag?id=%d",
                request.getLocalName(), request.getLocalPort(), opeartionResult.getTagId()));
        httpHeaders.setLocation(locationUrl);
        return new ResponseEntity<TagEntity>(opeartionResult, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    @RequestMapping(value = "/tag", method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public ResponseEntity<TagEntity> updateEntity(
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
        return new ResponseEntity<>(tmp, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/tag", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public ResponseEntity<BlogException> deleteEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        TagEntity tmp = tagService.findTagById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getTagCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        try {
            tagService.removeTag(tmp);
            return new ResponseEntity<>(new BlogException.Builder(BlogExceptionEnum.DELETE_TAG_SUCCESS).build(), HttpStatus.OK);
        } catch (Exception e) {
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_TAG_FAILED).build();
        }
    }

    @Override
    @RequestMapping(value = "/tag", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public ResponseEntity<TagEntity> queryEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        TagEntity tmp = tagService.findTagById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getTagCreator().getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        return new ResponseEntity<>(tmp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @RequestMapping(value = "/tags", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public ResponseEntity<List<TagEntity>> saveEntities(
            @RequestBody List<TagEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<TagEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (TagEntity tmp : list) {
                ResponseEntity<TagEntity> res = saveEntity(tmp, operator);
                if (HttpStatus.OK.equals(res.getStatusCode())) {
                    operateSuccess.add(tmp);
                }
            }
            return new ResponseEntity<>(operateSuccess, HttpStatus.PARTIAL_CONTENT);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        } finally {
            if (operateSuccess.size() == 0) {
                return new ResponseEntity<>(operateSuccess, HttpStatus.BAD_REQUEST);
            } else if (operateSuccess.size() > 0 && operateSuccess.size() < list.size()) {
                return new ResponseEntity<>(operateSuccess, HttpStatus.PARTIAL_CONTENT);
            } else {
                return new ResponseEntity<>(operateSuccess, HttpStatus.OK);
            }
        }
    }

    @Override
    @RequestMapping(value = "/tags", method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public ResponseEntity<List<TagEntity>> updateEntities(
            @RequestBody List<TagEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<TagEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (TagEntity tmp : list) {
                ResponseEntity<TagEntity> res = updateEntity(tmp, operator);
                if (HttpStatus.OK.equals(res.getStatusCode())) {
                    operateSuccess.add(tmp);
                }
            }
            return new ResponseEntity<>(operateSuccess, HttpStatus.PARTIAL_CONTENT);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        } finally {
            if (operateSuccess.size() == 0) {
                return new ResponseEntity<>(operateSuccess, HttpStatus.BAD_REQUEST);
            } else if (operateSuccess.size() > 0 && operateSuccess.size() < list.size()) {
                return new ResponseEntity<>(operateSuccess, HttpStatus.PARTIAL_CONTENT);
            } else {
                return new ResponseEntity<>(operateSuccess, HttpStatus.OK);
            }
        }
    }

    @Override
    @RequestMapping(value = "/tags", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public ResponseEntity<List<TagEntity>> deleteEntitiesById(
            @RequestParam(name = "ids") List<Integer> ids,
            @CurrentUser UserEntity operator) throws BlogException {
        List<TagEntity> operateFailed = new ArrayList<>(ids.size());
        try {
            for (Integer id : ids) {
                try {
                    ResponseEntity<BlogException> res = deleteEntityById(id, operator);
                    if (HttpStatus.BAD_REQUEST.equals(res.getStatusCode())) {
                        operateFailed.add(tagService.findTagById(id));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return new ResponseEntity<>(operateFailed, HttpStatus.PARTIAL_CONTENT);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        } finally {
            if (operateFailed.size() == 0) {
                return new ResponseEntity<>(operateFailed, HttpStatus.BAD_REQUEST);
            } else if (operateFailed.size() > 0 && operateFailed.size() < ids.size()) {
                return new ResponseEntity<>(operateFailed, HttpStatus.PARTIAL_CONTENT);
            } else {
                return new ResponseEntity<>(operateFailed, HttpStatus.OK);
            }
        }
    }

    @Override
    @RequestMapping(value = "/tags", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = TagEntity.BaseTagView.class)
    public ResponseEntity<List<TagEntity>> queryEntities(
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
        return new ResponseEntity<List<TagEntity>>(tagEntities, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/tags", method = {RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity queryEntitiesSize(
            @RequestParam Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!params.containsKey(Constants.SIZE)) {
            params.put(Constants.SIZE, Constants.INVAILD_VALUE);
        }
        if (!params.containsKey(Constants.PAGE)) {
            params.put(Constants.PAGE, Constants.INVAILD_VALUE);
        }
        ResponseEntity<List<TagEntity>> responseEntity = queryEntities(params, operator);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(responseEntity.getBody().size());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }
}
