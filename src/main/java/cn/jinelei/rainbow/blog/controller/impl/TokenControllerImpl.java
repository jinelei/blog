package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.controller.TokenController;
import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import cn.jinelei.rainbow.blog.service.TokenService;
import cn.jinelei.rainbow.blog.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * @author zhenlei
 */
@RestController
@RequestMapping("/token")
@ResponseBody
public class TokenControllerImpl implements TokenController {
    @Autowired
    UserService userService;

    @Autowired
    private TokenService tokenService;

    @Override
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.OPTIONS})
    @JsonView(UserEntity.WithoutPasswordView.class)
    public ResponseEntity<UserEntity> login(
            @RequestParam String username,
            @RequestParam String password) throws BlogException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BlogException.Builder(BlogExceptionEnum.USER_LOGIN_FAILED).build();
        }
        UserEntity user = userService.validUserByUsernameAndPassword(username, password);
        TokenEntity tokenEntity = tokenService.createToken(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(tokenEntity.getToken());
        return new ResponseEntity<>(user, httpHeaders, HttpStatus.OK);
    }

    @Override
    @RequestMapping(method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public BlogException logout(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) throws BlogException {
        if (!StringUtils.isEmpty(token)) {
            if (token.startsWith("Bearer ")) {
                tokenService.deleteToken(token.replace("Bearer ", ""));
                return new BlogException.Builder(BlogExceptionEnum.USER_LOGOUT_SUCCESS).build();
            } else {
                tokenService.deleteToken(token);
                return new BlogException.Builder(BlogExceptionEnum.USER_LOGOUT_SUCCESS).build();
            }

        } else {
            return new BlogException.Builder(BlogExceptionEnum.USER_LOGOUT_FAILED, "token is empty").build();
        }
    }

}

