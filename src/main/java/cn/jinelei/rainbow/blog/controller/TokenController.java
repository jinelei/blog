package cn.jinelei.rainbow.blog.controller;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhenlei
 */
public interface TokenController {

    /**
     * 登录操作
     *
     * @param username 用户名
     * @param password 密码
     * @return
     * @throws BlogException
     */
    public ResponseEntity login(@RequestParam String username, @RequestParam String password)
            throws BlogException;

    /**
     * 登出操作
     *
     * @param user 当前登录的用户
     * @return
     * @throws BlogException
     */
    public ResponseEntity logout(@CurrentUser UserEntity user) throws BlogException;

}

