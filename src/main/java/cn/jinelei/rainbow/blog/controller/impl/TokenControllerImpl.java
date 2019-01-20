package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.authorization.annotation.Authorization;
import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.controller.TokenController;
import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.OperatorPrivilege;
import cn.jinelei.rainbow.blog.exception.CustomizeException;
import cn.jinelei.rainbow.blog.exception.enumerate.UserExceptionEnum;
import cn.jinelei.rainbow.blog.service.TokenService;
import cn.jinelei.rainbow.blog.service.UserService;
import cn.jinelei.rainbow.blog.service.impl.TokenServiceTestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity login(@RequestParam String username, @RequestParam String password)
            throws CustomizeException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new CustomizeException(UserExceptionEnum.NEED_FIELD);
        }
        UserEntity user = userService.validUserByUsernameAndPassword(username, password);
        TokenEntity tokenEntity = tokenService.createToken(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(String.format("%s:%s", tokenEntity.getUserEntity().getUserId(), tokenEntity.getToken()));
        return new ResponseEntity<UserEntity>(user, httpHeaders, HttpStatus.OK);
    }

    @Override
    @Authorization(orConditions = {
            @Authorization.AuthorizationCondition(grantGroup = GroupPrivilege.TOURIST_GROUP)
    })
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity logout(@CurrentUser UserEntity user) throws CustomizeException {
        tokenService.deleteToken(user);
        return ResponseEntity.ok("注销成功");
    }

    @RequestMapping("/test")
    public Map test() {
        return ((TokenServiceTestImpl) tokenService).getTokenStore();
    }

}

