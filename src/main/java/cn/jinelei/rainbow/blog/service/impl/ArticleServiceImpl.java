package cn.jinelei.rainbow.blog.service.impl;

import cn.jinelei.rainbow.blog.entity.ArticleEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.repository.ArticleRepository;
import cn.jinelei.rainbow.blog.service.ArticleService;
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
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    ArticleRepository articleRepository;

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public ArticleEntity addArticle(ArticleEntity articleEntity) throws BlogException {
        if (articleEntity.getCreateTime() == null) {
            articleEntity.setCreateTime(Instant.now().toEpochMilli());
        }
        try {
            List<ArticleEntity> list = articleRepository.findAll(new Specification<ArticleEntity>() {
                @Override
                public Predicate toPredicate(Root<ArticleEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>(16);
                    if (!StringUtils.isEmpty(articleEntity.getTitle())) {
                        predicates.add(criteriaBuilder.equal(root.get("title").as(String.class),
                                articleEntity.getTitle()));
                    }
                    if (articleEntity.getAuthor() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("author").as(UserEntity.class),
                                articleEntity.getAuthor()));
                    }
                    Predicate[] predicateList = new Predicate[predicates.size()];
                    return criteriaBuilder.and(predicates.toArray(predicateList));
                }
            });
            if (list.size() > 0) {
                throw new BlogException.ArticleAlreadyExist();
            }
            ArticleEntity saveResult = articleRepository.save(articleEntity);
            if (!saveResult.equalsWithoutId(articleEntity)) {
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
    public void removeArticle(ArticleEntity articleEntity) throws BlogException {
        articleRepository.delete(articleEntity);
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public ArticleEntity updateArticle(ArticleEntity articleEntity) throws BlogException {
        ArticleEntity saveResult = articleRepository.save(articleEntity);
        if (!saveResult.equals(articleEntity)) {
            throw new BlogException.UpdateDataError();
        }
        return saveResult;
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public ArticleEntity findArticleById(Integer id) throws BlogException {
        Optional<ArticleEntity> findResult = articleRepository.findById(id);
        if (findResult.isPresent()) {
            return findResult.get();
        } else {
            throw new BlogException.ArticleNotFound();
        }
    }

    @Override
    public List<ArticleEntity> findArticleList(
            String name, String summary, UserEntity articleCreator,
            Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException {
        // 设置查询条件
        Specification<ArticleEntity> specification = new Specification<ArticleEntity>() {
            @Override
            public Predicate toPredicate(Root<ArticleEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>(16);
                if (!StringUtils.isEmpty(name)) {
                    predicates.add(criteriaBuilder.like(root.get("name").as(String.class),
                            String.format("%%%s%%", name)));
                }
                if (!StringUtils.isEmpty(summary)) {
                    predicates.add(criteriaBuilder.like(root.get("summary").as(String.class),
                            String.format("%%%s%%", summary)));
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
        if (page != null && size != null) {
            Page<ArticleEntity> res = sort != null ?
                    articleRepository.findAll(specification, PageRequest.of(page, size, sort)) :
                    articleRepository.findAll(specification, PageRequest.of(page, size));
            return res.getContent();
        } else {
            return articleRepository.findAll(specification);
        }
    }

}
