package cn.jinelei.rainbow.blog.service.impl;

import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.CategoryEntity;
import cn.jinelei.rainbow.blog.entity.CommentEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.repository.CommentRepository;
import cn.jinelei.rainbow.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhenlei
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public CommentEntity addComment(CommentEntity commentEntity) throws BlogException {
        if (commentEntity.getCreateTime() == null) {
            commentEntity.setCreateTime(Instant.now().toEpochMilli());
        }
        try {
            CommentEntity saveResult = commentRepository.save(commentEntity);
            if (!saveResult.equalsWithoutId(commentEntity)) {
                throw new BlogException.InsertDataError();
            }
            return saveResult;
        } catch (Exception e) {
            if (e instanceof BlogException) {
                throw e;
            }
            throw new BlogException.InsertDataError();
        }
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public void removeComment(CommentEntity commentEntity) throws BlogException {
        commentRepository.delete(commentEntity);
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public CommentEntity updateComment(CommentEntity commentEntity) throws BlogException {
        CommentEntity saveResult = commentRepository.save(commentEntity);
        if (!saveResult.equals(commentEntity)) {
            throw new BlogException.UpdateDataError();
        }
        return saveResult;
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public CommentEntity findCommentById(Integer id) throws BlogException {
        Optional<CommentEntity> findResult = commentRepository.findById(id);
        if (findResult.isPresent()) {
            return findResult.get();
        } else {
            throw new BlogException.CommentNotFound();
        }
    }

    @Override
    public List<CommentEntity> findCommentList(
            String content, UserEntity commentator, ArticleEntity articleEntity,
            Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException {
        // 设置查询条件
        Specification<CommentEntity> specification = new Specification<CommentEntity>() {
            @Override
            public Predicate toPredicate(Root<CommentEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>(16);
                if (!StringUtils.isEmpty(content)) {
                    predicates.add(criteriaBuilder.like(root.get(Constants.CONTENT).as(String.class),
                            String.format("%%%s%%", content)));
                }
                if (commentator != null) {
                    predicates.add(criteriaBuilder.equal(root.get(Constants.COMMENTATOR).as(UserEntity.class),
                            commentator));
                }
                if (articleEntity != null) {
                    predicates.add(criteriaBuilder.equal(root.get(Constants.ARTICLE).as(ArticleEntity.class),
                            articleEntity));
                }
                Predicate[] predicateList = new Predicate[predicates.size()];
                return criteriaBuilder.and(predicates.toArray(predicateList));
            }
        };
        Sort sort = null;
        Pageable pageable = null;
        // 如果排序有效
        if (ascFilters != null && ascFilters.length > 0) {
            sort = new Sort(Sort.Direction.ASC, ascFilters);
        }
        if (descFilters != null && descFilters.length > 0) {
            sort = new Sort(Sort.Direction.DESC, descFilters);
        }
        // 如果分页有效
        if (page != null && size != null && page > -1 && size > -1) {
            Page<CommentEntity> res = sort != null ?
                    commentRepository.findAll(specification, PageRequest.of(page, size, sort)) :
                    commentRepository.findAll(specification, PageRequest.of(page, size));
            return res.getContent();
        } else {
            return commentRepository.findAll(specification);
        }
    }

}

