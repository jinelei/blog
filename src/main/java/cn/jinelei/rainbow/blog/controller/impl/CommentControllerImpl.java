package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.controller.CommentController;
import cn.jinelei.rainbow.blog.entity.CommentEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
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
        consumes = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_JSON_UTF8_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.MULTIPART_FORM_DATA_VALUE,
                MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        },
        produces = {
                MediaType.APPLICATION_JSON_UTF8_VALUE,
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE
        })
public class CommentControllerImpl implements CommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Override
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public ResponseEntity<CommentEntity> saveEntity(
            @RequestBody CommentEntity commentEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(commentEntity.getCommentator().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        commentEntity.setCommentator(userService.findUserById(commentEntity.getCommentator().getUserId()));
        CommentEntity opeartionResult = null;
        opeartionResult = commentService.addComment(commentEntity);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI locationUrl = URI.create(String.format("http://%s:%d/comment?id=%d",
                request.getLocalName(), request.getLocalPort(), opeartionResult.getCommentId()));
        httpHeaders.setLocation(locationUrl);
        return new ResponseEntity<CommentEntity>(opeartionResult, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    @RequestMapping(value = "/comment", method = RequestMethod.PUT)
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public ResponseEntity<CommentEntity> updateEntity(
            @RequestBody CommentEntity commentEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(commentEntity.getCommentator().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        CommentEntity tmp = commentService.findCommentById(commentEntity.getCommentId());
        if (!StringUtils.isEmpty(commentEntity.getContent())) {
            tmp.setContent(commentEntity.getContent());
        }
        Instant now = Instant.now();
        tmp.setAccessTime(now.toEpochMilli());
        tmp.setModifyTime(now.toEpochMilli());
        commentService.updateComment(tmp);
        return new ResponseEntity<>(tmp, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/comment", method = RequestMethod.DELETE)
    public ResponseEntity<BlogException> deleteEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        CommentEntity tmp = commentService.findCommentById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getCommentator().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        try {
            commentService.removeComment(tmp);
            return new ResponseEntity<>(new BlogException.DeleteCommentSuccess(), HttpStatus.OK);
        } catch (Exception e) {
            throw new BlogException.DeleteCommentFailed();
        }
    }

    @Override
    @RequestMapping(value = "/comment", method = RequestMethod.GET)
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public ResponseEntity<CommentEntity> queryEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        CommentEntity tmp = commentService.findCommentById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getCommentator().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        return new ResponseEntity<>(tmp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public ResponseEntity<List<CommentEntity>> saveEntities(
            @RequestBody List<CommentEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<CommentEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (CommentEntity tmp : list) {
                ResponseEntity<CommentEntity> res = saveEntity(tmp, operator);
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
    @RequestMapping(value = "/comments", method = RequestMethod.PUT)
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public ResponseEntity<List<CommentEntity>> updateEntities(
            @RequestBody List<CommentEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<CommentEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (CommentEntity tmp : list) {
                ResponseEntity<CommentEntity> res = updateEntity(tmp, operator);
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
    @RequestMapping(value = "/comments", method = RequestMethod.DELETE)
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public ResponseEntity<List<CommentEntity>> deleteEntitiesById(
            @RequestParam(name = "ids") List<Integer> ids,
            @CurrentUser UserEntity operator) throws BlogException {
        List<CommentEntity> operateFailed = new ArrayList<>(ids.size());
        try {
            for (Integer id : ids) {
                try {
                    ResponseEntity<BlogException> res = deleteEntityById(id, operator);
                    if (HttpStatus.BAD_REQUEST.equals(res.getStatusCode())) {
                        operateFailed.add(commentService.findCommentById(id));
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
    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    @JsonView(value = CommentEntity.BaseCommentView.class)
    public ResponseEntity<List<CommentEntity>> queryEntities(
            Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        String name = params.getOrDefault("name", "").toString();
        String summary = params.getOrDefault("summary", "").toString();
        String userId = params.getOrDefault("user_id", "").toString();
        UserEntity userEntity = null;
        if (StringUtils.isEmpty(userId)) {
            userEntity = userService.findUserById(Integer.valueOf(userId));
        }
        Integer page = Integer.valueOf(params.getOrDefault("page", "0").toString());
        Integer size = Integer.valueOf(params.getOrDefault("size", "10").toString());
        String[] descFilters = new String[]{};
        String[] ascFilters = new String[]{};
        List<CommentEntity> commentEntities = commentService.findCommentList(name, summary, userEntity, page, size, descFilters, ascFilters);
        return new ResponseEntity<List<CommentEntity>>(commentEntities, HttpStatus.OK);
    }
}
