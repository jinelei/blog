package cn.jinelei.rainbow.blog.service;

import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;

/**
 * @author zhenlei
 */
public interface TokenService {
    /**
     * 创建一个token关联上指定用户
     *
     * @param userEntity 用户实体
     * @return 生成的token
     */
    TokenEntity createToken(UserEntity userEntity);

    /**
     * 从字符串中解析token
     *
     * @param authentication 加密后的字符串
     * @return Token实例
     * @throws BlogException
     */
    TokenEntity getToken(String authentication) throws BlogException;

    /**
     * 清除token
     *
     * @param token 加密的字符串
     * @throws BlogException
     */
    void deleteToken(String token) throws BlogException;
}
