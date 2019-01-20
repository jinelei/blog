package cn.jinelei.rainbow.blog.service;

import cn.jinelei.rainbow.blog.exception.CustomizeException;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author zhenlei
 */
public interface UserService {
    /**
     * 添加用户
     *
     * @param userEntity
     * @return
     * @throws CustomizeException
     */
    UserEntity addUser(UserEntity userEntity) throws CustomizeException;

    /**
     * 删除用户
     *
     * @param userEntity
     * @return
     * @throws CustomizeException
     */
    void removeUser(UserEntity userEntity) throws CustomizeException;

    /**
     * 更新用户
     *
     * @param userEntity
     * @return
     * @throws CustomizeException
     */
    UserEntity updateUser(UserEntity userEntity) throws CustomizeException;

    /**
     * 查找用户
     *
     * @param id 用户id
     * @return
     * @throws CustomizeException
     */
    UserEntity findUserById(Integer id) throws CustomizeException;

    /**
     * 验证用户名密码
     *
     * @param username
     * @param password
     * @return
     * @throws CustomizeException
     */
    UserEntity validUserByUsernameAndPassword(String username, String password)
            throws CustomizeException;

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
     * @throws CustomizeException
     */
    List<UserEntity> findUserList(String username, String nickname,
                                  String phone, String city,
                                  String province, String email,
                                  Integer page, Integer size,
                                  String[] descFilters, String[] ascFilters) throws CustomizeException;
}
