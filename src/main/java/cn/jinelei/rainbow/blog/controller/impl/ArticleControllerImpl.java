package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.controller.ArticleController;
import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.service.ArticleService;
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
public class ArticleControllerImpl implements ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;

    @Override
    @RequestMapping(value = "/article", method = RequestMethod.POST)
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ResponseEntity<ArticleEntity> saveEntity(
            @RequestBody ArticleEntity articleEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(articleEntity.getAuthor().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        articleEntity.setAuthor(userService.findUserById(articleEntity.getAuthor().getUserId()));
        ArticleEntity opeartionResult = null;
        opeartionResult = articleService.addArticle(articleEntity);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI locationUrl = URI.create(String.format("http://%s:%d/article?id=%d",
                request.getLocalName(), request.getLocalPort(), opeartionResult.getArticleId()));
        httpHeaders.setLocation(locationUrl);
        return new ResponseEntity<ArticleEntity>(opeartionResult, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    @RequestMapping(value = "/article", method = RequestMethod.PUT)
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ResponseEntity<ArticleEntity> updateEntity(
            @RequestBody ArticleEntity articleEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(articleEntity.getAuthor().getUserId())) {
            throw new BlogException.UnAuthorized();
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
        Instant now = Instant.now();
        tmp.setAccessTime(now.toEpochMilli());
        tmp.setModifyTime(now.toEpochMilli());
        articleService.updateArticle(tmp);
        return new ResponseEntity<>(tmp, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/article", method = RequestMethod.DELETE)
    public ResponseEntity<BlogException> deleteEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        ArticleEntity tmp = articleService.findArticleById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getAuthor().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        try {
            articleService.removeArticle(tmp);
            return new ResponseEntity<>(new BlogException.DeleteArticleSuccess(), HttpStatus.OK);
        } catch (Exception e) {
            throw new BlogException.DeleteArticleFailed();
        }
    }

    @Override
    @RequestMapping(value = "/article", method = RequestMethod.GET)
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ResponseEntity<ArticleEntity> queryEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        ArticleEntity tmp = articleService.findArticleById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getAuthor().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        return new ResponseEntity<>(tmp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @RequestMapping(value = "/articles", method = RequestMethod.POST)
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ResponseEntity<List<ArticleEntity>> saveEntities(
            @RequestBody List<ArticleEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<ArticleEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (ArticleEntity tmp : list) {
                ResponseEntity<ArticleEntity> res = saveEntity(tmp, operator);
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
    @RequestMapping(value = "/articles", method = RequestMethod.PUT)
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ResponseEntity<List<ArticleEntity>> updateEntities(
            @RequestBody List<ArticleEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<ArticleEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (ArticleEntity tmp : list) {
                ResponseEntity<ArticleEntity> res = updateEntity(tmp, operator);
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
    @RequestMapping(value = "/articles", method = RequestMethod.DELETE)
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ResponseEntity<List<ArticleEntity>> deleteEntitiesById(
            @RequestParam(name = "ids") List<Integer> ids,
            @CurrentUser UserEntity operator) throws BlogException {
        List<ArticleEntity> operateFailed = new ArrayList<>(ids.size());
        try {
            for (Integer id : ids) {
                try {
                    ResponseEntity<BlogException> res = deleteEntityById(id, operator);
                    if (HttpStatus.BAD_REQUEST.equals(res.getStatusCode())) {
                        operateFailed.add(articleService.findArticleById(id));
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
    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    @JsonView(value = ArticleEntity.BaseArticleView.class)
    public ResponseEntity<List<ArticleEntity>> queryEntities(
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
        List<ArticleEntity> articleEntities = articleService.findArticleList(name, summary, userEntity, page, size, descFilters, ascFilters);
        return new ResponseEntity<List<ArticleEntity>>(articleEntities, HttpStatus.OK);
    }
}
