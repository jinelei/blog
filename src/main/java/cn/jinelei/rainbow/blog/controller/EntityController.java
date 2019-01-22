package cn.jinelei.rainbow.blog.controller;

import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * @author zhenlei
 */
public interface EntityController<T> {
    /**
     * 保存实例
     *
     * @param t
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    ResponseEntity<T> saveEntity(T t, UserEntity operator) throws BlogException;

    /**
     * 更新实例
     *
     * @param t
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    ResponseEntity<T> updateEntity(T t, UserEntity operator) throws BlogException;

    /**
     * 删除实例
     *
     * @param id
     * @param operator 操作者实例
     * @return 失败返回失败的实例
     * @throws BlogException 成功抛出成功异常
     */
    ResponseEntity<UserEntity> deleteEntityById(Integer id, UserEntity operator) throws BlogException;

    /**
     * 查询实例
     *
     * @param id       实例主键
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    ResponseEntity<T> queryEntityById(Integer id, UserEntity operator) throws BlogException;

    /**
     * 批量保存实例
     *
     * @param list
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    ResponseEntity<List<T>> saveEntities(List<T> list, UserEntity operator) throws BlogException;

    /**
     * 批量更新实例
     *
     * @param list
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    ResponseEntity<List<T>> updateEntities(List<T> list, UserEntity operator) throws BlogException;

    /**
     * 批量删除实例
     *
     * @param ids
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    ResponseEntity<List<T>> deleteEntitiesById(List<Integer> ids, UserEntity operator) throws BlogException;

    /**
     * 查询实例集合
     *
     * @param params
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    ResponseEntity<List<T>> queryEntities(Map<String, Object> params, UserEntity operator) throws BlogException;
}
