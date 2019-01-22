package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.controller.TokenController;
import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.service.TokenService;
import cn.jinelei.rainbow.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BlogException> login(
            @RequestParam String username,
            @RequestParam String password) throws BlogException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BlogException.UserLoginFailed();
        }
        UserEntity user = userService.validUserByUsernameAndPassword(username, password);
        TokenEntity tokenEntity = tokenService.createToken(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(tokenEntity.getToken());
        return new ResponseEntity<>(new BlogException.UserLoginSuccess(), httpHeaders, HttpStatus.OK);
    }

    @Override
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<BlogException> logout(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) throws BlogException {
        if (token.startsWith("Bearer ")) {
            tokenService.deleteToken(token.replace("Bearer ", ""));
            return new ResponseEntity<>(new BlogException.UserLogoutSuccess(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new BlogException.UserLogoutFailed(), HttpStatus.BAD_REQUEST);
        }
    }

}

