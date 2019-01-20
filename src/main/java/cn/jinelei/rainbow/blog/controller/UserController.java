package cn.jinelei.rainbow.blog.controller;

import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.CustomizeException;
import cn.jinelei.rainbow.blog.exception.enumerate.BaseExceptionEnum;
import cn.jinelei.rainbow.blog.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

/**
 * @author zhenlei
 */
public interface UserController {

    /**
     * 添加用户 POST
     *
     * @param userEntity 用户实例
     * @return 插入的结果
     * @throws CustomizeException
     */
    public ResponseEntity<UserEntity> addUser(
            @RequestBody UserEntity userEntity) throws CustomizeException;

    /**
     * 删除用户 DELETE
     *
     * @param id 用户id
     * @throws CustomizeException
     */
    public void deleteUser(
            @PathVariable(name = "id") Integer id) throws CustomizeException;

    /**
     * 更新用户 PUT
     *
     * @param id         用户id
     * @param userEntity 用户实例，有则更新
     * @return
     * @throws CustomizeException
     */
    public UserEntity updateUser(@PathVariable(name = "id") Integer id,
                                 @RequestBody UserEntity userEntity) throws CustomizeException;

    /**
     * 获取用户
     *
     * @param id 用户id
     * @return
     * @throws CustomizeException
     */
    public ResponseEntity<UserEntity> getUserById(
            @RequestParam(name = "id", required = false) Integer id) throws CustomizeException;

    /**
     * 查询用户集合
     *
     * @param username    用户名
     * @param nickname    用户昵称
     * @param phone       手机号
     * @param city        城市
     * @param province    省
     * @param email       邮箱
     * @param current     当前页数
     * @param size        每页记录数量
     * @param descFilters 降序排列的字段
     * @param ascFilters  升序排列的字段
     * @return 用户集合
     * @throws CustomizeException
     */
    public ResponseEntity<List<UserEntity>> getUsers(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "nickname", required = false) String nickname,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "province", required = false) String province,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "descBy", required = false) String[] descFilters,
            @RequestParam(name = "ascBy", required = false) String[] ascFilters
    ) throws CustomizeException;

}

