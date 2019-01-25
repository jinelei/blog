package cn.jinelei.rainbow.blog.controller;

import cn.jinelei.rainbow.blog.exception.BlogException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import cn.jinelei.rainbow.blog.entity.UserEntity;

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
    public ResponseEntity<UserEntity> login(@RequestParam String username, @RequestParam String password)
            throws BlogException;

    /**
     * 登出操作
     *
     * @param token 当前的token
     * @return
     * @throws BlogException
     */
    public ResponseEntity<BlogException> logout(String token) throws BlogException;

}

