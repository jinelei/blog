package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.controller.UserController;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import cn.jinelei.rainbow.blog.service.UserService;
import cn.jinelei.rainbow.blog.utils.CheckUtils;
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
        value = "/users",
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
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public UserEntity queryEntityById(
            @PathVariable(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        CheckUtils.checkOwnerAndGroup(operator, id);
        UserEntity tmp = userService.findUserById(id);
        return tmp;
    }

    @Override
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public List<UserEntity> queryEntities(
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
        return userEntities;
    }

    @Override
    @RequestMapping(method = {RequestMethod.HEAD, RequestMethod.OPTIONS})
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
        List<UserEntity> responseEntity = queryEntities(params, operator);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(responseEntity.size());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @Override
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserEntity saveEntity(
            @RequestBody UserEntity userEntity,
            @CurrentUser(require = false) UserEntity operator) throws BlogException {
        // 检查用户名
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "username is empty").build();
        }
        // 检查用户密码
        if (StringUtils.isEmpty(userEntity.getPassword())) {
            throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "password is empty").build();
        }
        if (userEntity.getPassword().length() < 8) {
            throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "password is not strong").build();
        }
        // 检查手机号
        if (StringUtils.isEmpty(userEntity.getPhone())) {
            throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "phone is empty").build();
        }
        if (!CheckUtils.checkPhone(userEntity.getPhone())) {
            throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "phone is invalid: " + userEntity.getPhone()).build();
        }
        // 检查邮箱
        if (StringUtils.isEmpty(userEntity.getEmail())) {
            throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "email is empty").build();
        }
        if (!CheckUtils.checkEmail(userEntity.getEmail())) {
            throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "email is invalid: " + userEntity.getEmail()).build();
        }
        UserEntity opeartionResult = userService.addUser(userEntity);
        return opeartionResult;
    }

    @Override
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public UserEntity updateEntity(
            @RequestBody UserEntity userEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        UserEntity tmp = userService.findUserById(userEntity.getUserId());
        CheckUtils.checkOwnerAndGroup(operator, userEntity.getUserId());
        if (!StringUtils.isEmpty(userEntity.getNickname())) {
            tmp.setNickname(userEntity.getNickname());
        }
        if (!StringUtils.isEmpty(userEntity.getPhone())) {
            if (!CheckUtils.checkPhone(userEntity.getPhone())) {
                throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "phone is invalid: " + userEntity.getPhone()).build();
            }
            tmp.setPhone(userEntity.getPhone());
        }
        if (!StringUtils.isEmpty(userEntity.getEmail())) {
            if (!CheckUtils.checkEmail(userEntity.getEmail())) {
                throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "email is invalid: " + userEntity.getEmail()).build();
            }
            tmp.setEmail(userEntity.getEmail());
        }
        if (!StringUtils.isEmpty(userEntity.getPassword())) {
            if (userEntity.getPassword().length() < 8) {
                throw new BlogException.Builder(BlogExceptionEnum.CHECK_FAILED, "password is not strong").build();
            }
            tmp.setPassword(userEntity.getPassword());
        }
        if (!StringUtils.isEmpty(userEntity.getProvince())) {
            tmp.setProvince(userEntity.getProvince());
        }
        if (!StringUtils.isEmpty(userEntity.getCity())) {
            tmp.setCity(userEntity.getCity());
        }
        userService.updateUser(tmp);
        return tmp;
    }

    @Override
    @RequestMapping(value = "/id/{id}", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    @JsonView(value = UserEntity.WithoutPasswordView.class)
    public void deleteEntityById(
            @PathVariable(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        CheckUtils.checkOwnerAndGroup(operator, id);
        UserEntity tmp = userService.findUserById(id);
        try {
            userService.removeUser(tmp);
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_USER_SUCCESS, "id: " + id).build();
        } catch (Exception e) {
            throw new BlogException.Builder(BlogExceptionEnum.DELETE_USER_FAILED, "id: " + id).build();
        }
    }

}
