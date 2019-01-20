package cn.jinelei.rainbow.blog.repository;

import cn.jinelei.rainbow.blog.entity.TagEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhenlei
 */
@Repository
public interface TagRepository extends CrudRepository<TagEntity, Integer> {
}
