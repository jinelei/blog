package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.controller.UserController;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
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
@JsonView(value = UserEntity.WithoutPasswordView.class)
public class UserControllerImpl implements UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @Override
    @RequestMapping(value = "/user", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity<UserEntity> saveEntity(
            @RequestBody UserEntity userEntity,
            @CurrentUser(require = false) UserEntity operator) throws BlogException {
        UserEntity opeartionResult = userService.addUser(userEntity);
        return new ResponseEntity<UserEntity>(opeartionResult, HttpStatus.CREATED);
    }

    @Override
    @RequestMapping(value = "/user", method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity<UserEntity> updateEntity(
            @RequestBody UserEntity userEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(userEntity.getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        UserEntity tmp = userService.findUserById(userEntity.getUserId());
        if (!StringUtils.isEmpty(userEntity.getNickname())) {
            tmp.setNickname(userEntity.getNickname());
        }
        if (!StringUtils.isEmpty(userEntity.getPhone())) {
            tmp.setPhone(userEntity.getPhone());
        }
        if (!StringUtils.isEmpty(userEntity.getEmail())) {
            tmp.setEmail(userEntity.getEmail());
        }
        if (!StringUtils.isEmpty(userEntity.getPassword())) {
            tmp.setPassword(userEntity.getPassword());
        }
        if (!StringUtils.isEmpty(userEntity.getProvince())) {
            tmp.setProvince(userEntity.getProvince());
        }
        if (!StringUtils.isEmpty(userEntity.getCity())) {
            tmp.setCity(userEntity.getCity());
        }
        userService.updateUser(tmp);
        return new ResponseEntity<>(tmp, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/user", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity<BlogException> deleteEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        UserEntity tmp = userService.findUserById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        try {
            userService.removeUser(tmp);
            return new ResponseEntity<>(new BlogException.Builder(BlogExceptionEnum.DELETE_USER_SUCCESS, "id: " + id).build(), HttpStatus.OK);
        } catch (Exception e) {
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_USER_FAILED, "id: " + id).build();
        }
    }

    @Override
    @RequestMapping(value = "/user", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity<UserEntity> queryEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        UserEntity tmp = userService.findUserById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getUserId())) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
        return new ResponseEntity<>(tmp, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/users", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity<List<UserEntity>> saveEntities(
            @RequestBody List<UserEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<UserEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (UserEntity tmp : list) {
                ResponseEntity<UserEntity> res = saveEntity(tmp, operator);
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
    @RequestMapping(value = "/users", method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity<List<UserEntity>> updateEntities(
            @RequestBody List<UserEntity> list,
            @CurrentUser UserEntity operator) throws BlogException {
        List<UserEntity> operateSuccess = new ArrayList<>(list.size());
        try {
            for (UserEntity tmp : list) {
                ResponseEntity<UserEntity> res = updateEntity(tmp, operator);
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
    @RequestMapping(value = "/users", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity<List<UserEntity>> deleteEntitiesById(
            @RequestParam(name = "ids") List<Integer> ids,
            @CurrentUser UserEntity operator) throws BlogException {
        List<UserEntity> operateFailed = new ArrayList<>(ids.size());
        try {
            for (Integer id : ids) {
                try {
                    ResponseEntity<BlogException> res = deleteEntityById(id, operator);
                    if (!HttpStatus.OK.equals(res.getStatusCode())) {
                        operateFailed.add(userService.findUserById(id));
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
    @RequestMapping(value = "/users", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity<List<UserEntity>> queryEntities(
            @RequestParam Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        String username = params.getOrDefault(Constants.USERNAME, Constants.DEFAULT_STRING).toString();
        String nickname = params.getOrDefault(Constants.NICKNAME, Constants.DEFAULT_STRING).toString();
        String phone = params.getOrDefault(Constants.PHONE, Constants.DEFAULT_STRING).toString();
        String city = params.getOrDefault(Constants.CITY, Constants.DEFAULT_STRING).toString();
        String province = params.getOrDefault(Constants.PROVINCE, Constants.DEFAULT_STRING).toString();
        String email = params.getOrDefault(Constants.EMAIL, Constants.DEFAULT_STRING).toString();
        Integer page = Integer.valueOf(params.get(Constants.PAGE).toString());
        Integer size = Integer.valueOf(params.get(Constants.SIZE).toString());
        String[] descFilters = StringUtils.isEmpty(params.getOrDefault(Constants.DESC_FILTERS, Constants.DEFAULT_STRING))
                ? null : params.get(Constants.DESC_FILTERS).toString().split(Constants.COMMA_SPLIT);
        String[] ascFilters = StringUtils.isEmpty(params.getOrDefault(Constants.ASC_FILTERS, Constants.DEFAULT_STRING))
                ? null : params.get(Constants.ASC_FILTERS).toString().split(Constants.COMMA_SPLIT);
        List<UserEntity> userEntities = userService.findUserList(username, nickname, phone, city, province, email, page, size, descFilters, ascFilters);
        return new ResponseEntity<List<UserEntity>>(userEntities, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/users", method = {RequestMethod.HEAD, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public ResponseEntity queryEntitiesSize(
            @RequestParam Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!params.containsKey(Constants.SIZE)) {
            params.put(Constants.SIZE, Constants.INVAILD_VALUE);
        }
        if (!params.containsKey(Constants.PAGE)) {
            params.put(Constants.PAGE, Constants.INVAILD_VALUE);
        }
        ResponseEntity<List<UserEntity>> responseEntity = queryEntities(params, operator);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(responseEntity.getBody().size());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

}
