package cn.jinelei.rainbow.blog.repository;

import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhenlei
 */
@Repository
public interface ArticleRepository extends CrudRepository<ArticleEntity, Integer> {
}
