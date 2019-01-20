package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.Authorization;
import cn.jinelei.rainbow.blog.controller.UserController;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.OperatorPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.UserPrivilege;
import cn.jinelei.rainbow.blog.exception.CustomizeException;
import cn.jinelei.rainbow.blog.exception.enumerate.BaseExceptionEnum;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.enumerate.UserExceptionEnum;
import cn.jinelei.rainbow.blog.repository.UserRepository;
import cn.jinelei.rainbow.blog.service.UserService;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

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
public class UserControllerImpl implements UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/user/test")
    public String test() throws CustomizeException {
        LOGGER.debug(request.getServletContext().getRealPath("/"));
        LOGGER.debug(request.getServletContext().getContextPath());
        throw new CustomizeException(BaseExceptionEnum.INSERT_DATA_ERROR);
    }

    @Override
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<UserEntity> addUser(
            @RequestBody UserEntity userEntity) throws CustomizeException {
        LOGGER.debug("addUser: " + userEntity);
        if (userService.findUserList("", "", "", "", "", userEntity.getEmail(), 0, 10, new String[]{}, new String[]{}).size() > 0) {
            throw new CustomizeException(UserExceptionEnum.EMAIL_ALREADY_EXIST);
        }
        if (userService.findUserList("", "", userEntity.getPhone(), "", "", "", 0, 10, new String[]{}, new String[]{}).size() > 0) {
            throw new CustomizeException(UserExceptionEnum.PHONE_ALREADY_EXIST);
        }
        UserEntity opeartionResult = userService.addUser(userEntity);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI locationUrl = URI.create(String.format("http://%s:%d/user/id/%d",
                request.getLocalName(), request.getLocalPort(), opeartionResult.getUserId()));
        httpHeaders.setLocation(locationUrl);
        return new ResponseEntity<UserEntity>(opeartionResult, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    @Authorization(orConditions = {
            @Authorization.AuthorizationCondition(grantGroup = GroupPrivilege.ROOT_GROUP),
            @Authorization.AuthorizationCondition(grantOperator = OperatorPrivilege.ONLY_MYSELF, parameterName = "id"),
    })
    @RequestMapping(value = "/user/id/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable(name = "id") Integer id) throws CustomizeException {
        UserEntity userEntity = userService.findUserById(id);
        userService.removeUser(userEntity);
    }

    @Override
    @RequestMapping(value = "/user/id/{id}", method = RequestMethod.PUT)
    public UserEntity updateUser(@PathVariable(value = "id") Integer id,
                                 @RequestBody UserEntity userEntity) throws CustomizeException {
        UserEntity queryUser = userService.findUserById(id);
        if (!StringUtils.isEmpty(userEntity.getUsername())) {
            queryUser.setUsername(userEntity.getUsername());
        }
        if (!StringUtils.isEmpty(userEntity.getCity())) {
            queryUser.setCity(userEntity.getCity());
        }
        if (!StringUtils.isEmpty(userEntity.getEmail())) {
            queryUser.setEmail(userEntity.getEmail());
        }
        if (!StringUtils.isEmpty(userEntity.getNickname())) {
            queryUser.setNickname(userEntity.getNickname());
        }
        if (!StringUtils.isEmpty(userEntity.getPhone())) {
            queryUser.setPhone(userEntity.getPhone());
        }
        if (!StringUtils.isEmpty(userEntity.getProvince())) {
            queryUser.setProvince(userEntity.getProvince());
        }
        if (!StringUtils.isEmpty(userEntity.getPassword())) {
            queryUser.setPassword(userEntity.getPassword());
        }
        if (userEntity.getUserPrivilege() != null
                && !userEntity.getUserPrivilege().equals(queryUser.getUserPrivilege())) {
            queryUser.setUserPrivilege(userEntity.getUserPrivilege());
        }
        if (userEntity.getGroupPrivilege() != null
                && !userEntity.getGroupPrivilege().equals(queryUser.getGroupPrivilege())) {
            queryUser.setGroupPrivilege(userEntity.getGroupPrivilege());
        }
        UserEntity opeartionResult = userService.updateUser(queryUser);
        return opeartionResult;
    }

    @Override
    @Authorization(orConditions = {
            @Authorization.AuthorizationCondition(grantGroup = GroupPrivilege.ROOT_GROUP),
            @Authorization.AuthorizationCondition(grantOperator = OperatorPrivilege.ONLY_MYSELF, parameterName = "id"),
    })
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<UserEntity> getUserById(
            @RequestParam(name = "id") Integer id) throws CustomizeException {
        UserEntity opeartionResult = userService.findUserById(id);
        LOGGER.debug("getUserById: " + id);
        return new ResponseEntity<UserEntity>(opeartionResult, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> getUsers(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "nickname", required = false) String nickname,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "province", required = false) String province,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "descBy", required = false) String[] descFilters,
            @RequestParam(name = "ascBy", required = false) String[] ascFilters
    ) throws CustomizeException {
        List<UserEntity> userEntities = userService.findUserList(username, nickname, phone, city, province, email, page, size, descFilters, ascFilters);
        return new ResponseEntity<List<UserEntity>>(userEntities, HttpStatus.OK);
    }

}
