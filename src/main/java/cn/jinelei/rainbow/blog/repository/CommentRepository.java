package cn.jinelei.rainbow.blog.repository;

import cn.jinelei.rainbow.blog.entity.CommentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhenlei
 */
@Repository
public interface CommentRepository extends CrudRepository<CommentEntity, Integer> {
}
