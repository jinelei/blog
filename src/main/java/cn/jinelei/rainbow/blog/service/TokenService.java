package cn.jinelei.rainbow.blog.service;

import cn.jinelei.rainbow.blog.authorization.annotation.Authorization;
import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.CustomizeException;

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
     * 检查token是否有效
     *
     * @param model         token
     * @param authorization 注解对象
     * @return 是否有效
     * @throws CustomizeException
     */
    boolean checkToken(TokenEntity model, Authorization authorization) throws CustomizeException;

    /**
     * 从字符串中解析token
     *
     * @param authentication 加密后的字符串
     * @return Token实例
     * @throws CustomizeException
     */
    TokenEntity getToken(String authentication) throws CustomizeException;

    /**
     * 清除token
     *
     * @param userEntity 登录的用户
     * @throws CustomizeException
     */
    void deleteToken(UserEntity userEntity) throws CustomizeException;
}
