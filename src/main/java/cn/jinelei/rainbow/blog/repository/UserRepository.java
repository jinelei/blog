package cn.jinelei.rainbow.blog.repository;

import cn.jinelei.rainbow.blog.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author zhenlei
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer>,
        JpaSpecificationExecutor<UserEntity> {
    /**
     * 通过用户名密码查询用户
     *
     * @param username
     * @param password
     * @return
     */
    List<UserEntity> findUserEntitiesByUsernameAndPassword(String username, String password);
}
