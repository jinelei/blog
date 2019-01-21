package cn.jinelei.rainbow.blog.service;

import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;

import java.util.List;

/**
 * @author zhenlei
 */
public interface UserService {
    /**
     * 添加用户
     *
     * @param userEntity
     * @return
     * @throws BlogException
     */
    UserEntity addUser(UserEntity userEntity) throws BlogException;

    /**
     * 删除用户
     *
     * @param userEntity
     * @return
     * @throws BlogException
     */
    void removeUser(UserEntity userEntity) throws BlogException;

    /**
     * 更新用户
     *
     * @param userEntity
     * @return
     * @throws BlogException
     */
    UserEntity updateUser(UserEntity userEntity) throws BlogException;

    /**
     * 查找用户
     *
     * @param id 用户id
     * @return
     * @throws BlogException
     */
    UserEntity findUserById(Integer id) throws BlogException;

    /**
     * 验证用户名密码
     *
     * @param username
     * @param password
     * @return
     * @throws BlogException
     */
    UserEntity validUserByUsernameAndPassword(String username, String password)
            throws BlogException;

    /**
     * 查询用户集合
     *
     * @param username
     * @param nickname
     * @param phone
     * @param city
     * @param province
     * @param email
     * @param page
     * @param size
     * @param descFilters
     * @param ascFilters
     * @return
     * @throws BlogException
     */
    List<UserEntity> findUserList(String username, String nickname,
                                  String phone, String city,
                                  String province, String email,
                                  Integer page, Integer size,
                                  String[] descFilters, String[] ascFilters) throws BlogException;
}
