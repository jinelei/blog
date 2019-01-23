package cn.jinelei.rainbow.blog.service.impl;

import cn.jinelei.rainbow.blog.entity.CategoryEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.repository.CategoryRepository;
import cn.jinelei.rainbow.blog.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public CategoryEntity addCategory(CategoryEntity categoryEntity) throws BlogException {
        if (categoryEntity.getCreateTime() == null) {
            categoryEntity.setCreateTime(Instant.now().toEpochMilli());
        }
        try {
            List<CategoryEntity> list = categoryRepository.findAll(new Specification<CategoryEntity>() {
                @Override
                public Predicate toPredicate(Root<CategoryEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>(16);
                    if (!StringUtils.isEmpty(categoryEntity.getName())) {
                        predicates.add(criteriaBuilder.equal(root.get("name").as(String.class),
                                categoryEntity.getName()));
                    }
                    if (categoryEntity.getCategoryCreator() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("categoryCreator").as(UserEntity.class),
                                categoryEntity.getCategoryCreator()));
                    }
                    Predicate[] predicateList = new Predicate[predicates.size()];
                    return criteriaBuilder.and(predicates.toArray(predicateList));
                }
            });
            if (list.size() > 0) {
                throw new BlogException.CategoryAlreadyExist();
            }
            CategoryEntity saveResult = categoryRepository.save(categoryEntity);
            if (!saveResult.equalsWithoutId(categoryEntity)) {
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
    public void removeCategory(CategoryEntity categoryEntity) throws BlogException {
        categoryRepository.delete(categoryEntity);
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public CategoryEntity updateCategory(CategoryEntity categoryEntity) throws BlogException {
        CategoryEntity saveResult = categoryRepository.save(categoryEntity);
        if (!saveResult.equals(categoryEntity)) {
            throw new BlogException.UpdateDataError();
        }
        return saveResult;
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public CategoryEntity findCategoryById(Integer id) throws BlogException {
        Optional<CategoryEntity> findResult = categoryRepository.findById(id);
        if (findResult.isPresent()) {
            return findResult.get();
        } else {
            throw new BlogException.CategoryNotFound();
        }
    }

    @Override
    public List<CategoryEntity> findCategoryList(
            String name, String summary, UserEntity categoryCreator,
            Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException {
        // 设置查询条件
        Specification<CategoryEntity> specification = new Specification<CategoryEntity>() {
            @Override
            public Predicate toPredicate(Root<CategoryEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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
        if (page != null && size != null && page > -1 && size > -1) {
            Page<CategoryEntity> res = sort != null ?
                    categoryRepository.findAll(specification, PageRequest.of(page, size, sort)) :
                    categoryRepository.findAll(specification, PageRequest.of(page, size));
            return res.getContent();
        } else {
            return categoryRepository.findAll(specification);
        }
    }

}
