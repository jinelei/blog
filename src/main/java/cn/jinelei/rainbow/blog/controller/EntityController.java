package cn.jinelei.rainbow.blog.controller;

import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * @author zhenlei
 */
public interface EntityController<T> {
    static final Logger LOGGER = LoggerFactory.getLogger(EntityController.class);


    /**
     * 查询实例
     *
     * @param id       实例主键
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default T queryEntityById(Integer id, UserEntity operator) throws BlogException {
        LOGGER.debug("default query entity by id");
        return null;
    }

    /**
     * 查询实例集合
     *
     * @param params
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default List<T> queryEntities(Map<String, Object> params, UserEntity operator) throws BlogException {
        LOGGER.debug("default query entities");
        return null;
    }

    /**
     * 查询实例集合容量
     *
     * @param params
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default ResponseEntity queryEntitiesSize(Map<String, Object> params, UserEntity operator) throws BlogException {
        LOGGER.debug("default query entities size");
        return null;
    }

    /**
     * 保存实例
     *
     * @param t
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default T saveEntity(T t, UserEntity operator) throws BlogException {
        LOGGER.debug("default save entity");
        return null;
    }

    /**
     * 更新实例
     *
     * @param t
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default T updateEntity(T t, UserEntity operator) throws BlogException {
        LOGGER.debug("default upadte entity");
        return null;
    }

    /**
     * 删除实例
     *
     * @param id
     * @param operator 操作者实例
     * @return 失败返回失败的实例
     * @throws BlogException 成功抛出成功异常
     */
    default void deleteEntityById(Integer id, UserEntity operator) throws BlogException {
        LOGGER.debug("default delete entity by id");
    }
}
