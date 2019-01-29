package cn.jinelei.rainbow.blog.repository;

import cn.jinelei.rainbow.blog.entity.TokenEntity;
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
public interface TokenRepository extends CrudRepository<TokenEntity, String>,
        JpaSpecificationExecutor<UserEntity> {
    /**
     * 通过用户实例查询Token
     *
     * @param userEntity
     * @return
     */
    Optional<TokenEntity> findTokenEntityByUserEntity(UserEntity userEntity);
}
