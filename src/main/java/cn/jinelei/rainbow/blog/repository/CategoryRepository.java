package cn.jinelei.rainbow.blog.repository;

import cn.jinelei.rainbow.blog.entity.CategoryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhenlei
 */
@Repository
public interface CategoryRepository extends CrudRepository<CategoryEntity, Integer> {
}
