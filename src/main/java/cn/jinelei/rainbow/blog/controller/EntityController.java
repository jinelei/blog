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
     * 保存实例
     *
     * @param t
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default ResponseEntity<T> saveEntity(T t, UserEntity operator) throws BlogException {
        LOGGER.debug("default save entity");
        return new ResponseEntity<>((T) null, HttpStatus.OK);
    }

    /**
     * 更新实例
     *
     * @param t
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default ResponseEntity<T> updateEntity(T t, UserEntity operator) throws BlogException {
        LOGGER.debug("default upadte entity");
        return new ResponseEntity<>((T) null, HttpStatus.OK);
    }

    /**
     * 删除实例
     *
     * @param id
     * @param operator 操作者实例
     * @return 失败返回失败的实例
     * @throws BlogException 成功抛出成功异常
     */
    default ResponseEntity<BlogException> deleteEntityById(Integer id, UserEntity operator) throws BlogException {
        LOGGER.debug("default delete entity by id");
        return new ResponseEntity<>((BlogException) null, HttpStatus.OK);
    }

    /**
     * 查询实例
     *
     * @param id       实例主键
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default ResponseEntity<T> queryEntityById(Integer id, UserEntity operator) throws BlogException {
        LOGGER.debug("default query entity by id");
        return new ResponseEntity<>((T) null, HttpStatus.OK);
    }

    /**
     * 批量保存实例
     *
     * @param list
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default ResponseEntity<List<T>> saveEntities(List<T> list, UserEntity operator) throws BlogException {
        LOGGER.debug("default save entities");
        return new ResponseEntity<>((List<T>) null, HttpStatus.OK);
    }

    /**
     * 批量更新实例
     *
     * @param list
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default ResponseEntity<List<T>> updateEntities(List<T> list, UserEntity operator) throws BlogException {
        LOGGER.debug("default upadte entities");
        return new ResponseEntity<>((List<T>) null, HttpStatus.OK);
    }

    /**
     * 批量删除实例
     *
     * @param ids
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default ResponseEntity<List<T>> deleteEntitiesById(List<Integer> ids, UserEntity operator) throws BlogException {
        LOGGER.debug("default delete entities");
        return new ResponseEntity<>((List<T>) null, HttpStatus.OK);
    }

    /**
     * 查询实例集合
     *
     * @param params
     * @param operator 操作者实例
     * @return
     * @throws BlogException
     */
    default ResponseEntity<List<T>> queryEntities(Map<String, Object> params, UserEntity operator) throws BlogException {
        LOGGER.debug("default query entities");
        return new ResponseEntity<>((List<T>) null, HttpStatus.OK);
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
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
