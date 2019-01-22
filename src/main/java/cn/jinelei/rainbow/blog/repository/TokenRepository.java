package cn.jinelei.rainbow.blog.repository;

import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhenlei
 */
@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, String>,
        JpaSpecificationExecutor<UserEntity> {
}
