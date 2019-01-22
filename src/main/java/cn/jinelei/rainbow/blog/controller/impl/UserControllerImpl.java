package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.controller.UserController;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.service.UserService;
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
public class UserControllerImpl implements UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerImpl.class);
    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @Override
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<UserEntity> saveEntity(
            @RequestBody UserEntity userEntity,
            @CurrentUser(require = false) UserEntity operator) throws BlogException {
        UserEntity opeartionResult = userService.addUser(userEntity);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI locationUrl = URI.create(String.format("http://%s:%d/user/id/%d",
                request.getLocalName(), request.getLocalPort(), opeartionResult.getUserId()));
        httpHeaders.setLocation(locationUrl);
        return new ResponseEntity<UserEntity>(opeartionResult, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResponseEntity<UserEntity> updateEntity(
            @RequestBody UserEntity userEntity,
            @CurrentUser UserEntity operator) throws BlogException {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(userEntity.getUserId())) {
            throw new BlogException.UnAuthorized();
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
        return new ResponseEntity<UserEntity>(tmp, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public ResponseEntity<UserEntity> deleteEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException.DeleteUserSuccess, BlogException {
        UserEntity tmp = userService.findUserById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        try {
            userService.removeUser(tmp);
            throw new BlogException.DeleteUserSuccess();
        } catch (Exception e) {
            return new ResponseEntity<>(tmp, HttpStatus.OK);
        }
    }

    @Override
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<UserEntity> queryEntityById(
            @RequestParam(name = "id") Integer id,
            @CurrentUser UserEntity operator) throws BlogException {
        UserEntity tmp = userService.findUserById(id);
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(tmp.getUserId())) {
            throw new BlogException.UnAuthorized();
        }
        return new ResponseEntity<>(tmp, HttpStatus.BAD_REQUEST);
    }

    @Override
    @RequestMapping(value = "/users", method = RequestMethod.POST)
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
    @RequestMapping(value = "/users", method = RequestMethod.PUT)
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
    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    public ResponseEntity<List<UserEntity>> deleteEntitiesById(
            @RequestParam(name = "ids") List<Integer> ids,
            @CurrentUser UserEntity operator) throws BlogException {
        List<UserEntity> operateFailed = new ArrayList<>(ids.size());
        try {
            for (Integer id : ids) {
                try {
                    ResponseEntity<UserEntity> res = deleteEntityById(id, operator);
                    if (HttpStatus.BAD_REQUEST.equals(res.getStatusCode())) {
                        operateFailed.add(res.getBody());
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
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> queryEntities(
            Map<String, Object> params,
            @CurrentUser UserEntity operator) throws BlogException {
        String username = params.getOrDefault("username", "").toString();
        String nickname = params.getOrDefault("nickname", "").toString();
        String phone = params.getOrDefault("phone", "").toString();
        String city = params.getOrDefault("city", "").toString();
        String province = params.getOrDefault("province", "").toString();
        String email = params.getOrDefault("email", "").toString();
        Integer page = Integer.valueOf(params.getOrDefault("page", "0").toString());
        Integer size = Integer.valueOf(params.getOrDefault("size", "10").toString());
        String[] descFilters = new String[]{};
        String[] ascFilters = new String[]{};
        List<UserEntity> userEntities = userService.findUserList(username, nickname, phone, city, province, email, page, size, descFilters, ascFilters);
        return new ResponseEntity<List<UserEntity>>(userEntities, HttpStatus.OK);
    }

}
