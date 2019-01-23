package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.controller.CategoryController;
import cn.jinelei.rainbow.blog.entity.CategoryEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
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
public class CategoryControllerImpl implements CategoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    @Override
    @RequestMapping(value = "/category", method = RequestMethod.POST)
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public ResponseEntity<CategoryEntity> saveEntity(
            @RequestBody CategoryEntity categoryEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(categoryEntity.getCategoryCreator().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        if (categoryEntity.getCategoryCreator() == null) {
            categoryEntity.setCategoryCreator(operator);
        } else {
            categoryEntity.setCategoryCreator(userService.findUserById(categoryEntity.getCategoryCreator().getUserId()));
        }
        CategoryEntity opeartionResult = null;
        opeartionResult = categoryService.addCategory(categoryEntity);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI locationUrl = URI.create(String.format("http://%s:%d/category?id=%d",
                request.getLocalName(), request.getLocalPort(), opeartionResult.getCategoryId()));
        httpHeaders.setLocation(locationUrl);
        return new ResponseEntity<CategoryEntity>(opeartionResult, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    @RequestMapping(value = "/category", method = RequestMethod.PUT)
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public ResponseEntity<CategoryEntity> updateEntity(
            @RequestBody CategoryEntity categoryEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(categoryEntity.getCategoryCreator().getUserId())) {
            throw new BlogException.UnAuthorized();
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
        return new ResponseEntity<>(tmp, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/category", method = RequestMethod.DELETE)
    public ResponseEntity<BlogException> deleteEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        CategoryEntity tmp = categoryService.findCategoryById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getCategoryCreator().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        try {
            categoryService.removeCategory(tmp);
            return new ResponseEntity<>(new BlogException.DeleteCategorySuccess(), HttpStatus.OK);
        } catch (Exception e) {
            throw new BlogException.DeleteCategoryFailed();
        }
    }

    @Override
    @RequestMapping(value = "/category", method = RequestMethod.GET)
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public ResponseEntity<CategoryEntity> queryEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        CategoryEntity tmp = categoryService.findCategoryById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getCategoryCreator().getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        return new ResponseEntity<>(tmp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @RequestMapping(value = "/categorys", method = RequestMethod.POST)
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public ResponseEntity<List<CategoryEntity>> saveEntities(
            @RequestBody List<CategoryEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<CategoryEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (CategoryEntity tmp : list) {
                ResponseEntity<CategoryEntity> res = saveEntity(tmp, operator);
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
    @RequestMapping(value = "/categorys", method = RequestMethod.PUT)
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public ResponseEntity<List<CategoryEntity>> updateEntities(
            @RequestBody List<CategoryEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<CategoryEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (CategoryEntity tmp : list) {
                ResponseEntity<CategoryEntity> res = updateEntity(tmp, operator);
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
    @RequestMapping(value = "/categorys", method = RequestMethod.DELETE)
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public ResponseEntity<List<CategoryEntity>> deleteEntitiesById(
            @RequestParam(name = "ids") List<Integer> ids,
            @CurrentUser UserEntity operator) throws BlogException {
        List<CategoryEntity> operateFailed = new ArrayList<>(ids.size());
        try {
            for (Integer id : ids) {
                try {
                    ResponseEntity<BlogException> res = deleteEntityById(id, operator);
                    if (HttpStatus.BAD_REQUEST.equals(res.getStatusCode())) {
                        operateFailed.add(categoryService.findCategoryById(id));
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
    @RequestMapping(value = "/categorys", method = RequestMethod.GET)
    @JsonView(value = CategoryEntity.BaseCategoryView.class)
    public ResponseEntity<List<CategoryEntity>> queryEntities(
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
        List<CategoryEntity> categoryEntities = categoryService.findCategoryList(name, summary, userEntity, page, size, descFilters, ascFilters);
        return new ResponseEntity<List<CategoryEntity>>(categoryEntities, HttpStatus.OK);
    }
}