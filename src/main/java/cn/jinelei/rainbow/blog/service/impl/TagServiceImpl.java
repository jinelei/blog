package cn.jinelei.rainbow.blog.service.impl;

import cn.jinelei.rainbow.blog.constant.ConstantsCamelCase;
import cn.jinelei.rainbow.blog.entity.TagEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.UserPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import cn.jinelei.rainbow.blog.repository.TagRepository;
import cn.jinelei.rainbow.blog.service.TagService;
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
public class TagServiceImpl implements TagService {
    @Autowired
    TagRepository tagRepository;

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public TagEntity addTag(TagEntity tagEntity) throws BlogException {
        if (tagEntity.getCreateTime() == null) {
            tagEntity.setCreateTime(Instant.now().toEpochMilli());
        }
        try {
            List<TagEntity> list = tagRepository.findAll(new Specification<TagEntity>() {
                @Override
                public Predicate toPredicate(Root<TagEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>(16);
                    if (!StringUtils.isEmpty(tagEntity.getName())) {
                        predicates.add(criteriaBuilder.equal(root.get("name").as(String.class),
                                tagEntity.getName()));
                    }
                    if (tagEntity.getTagCreator() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("tagCreator").as(UserEntity.class),
                                tagEntity.getTagCreator()));
                    }
                    Predicate[] predicateList = new Predicate[predicates.size()];
                    return criteriaBuilder.and(predicates.toArray(predicateList));
                }
            });
            if (list.size() > 0) {
                throw new BlogException.Builder(BlogExceptionEnum.TAG_ALREADY_EXIST, tagEntity.toString()).build();
            }
            TagEntity saveResult = tagRepository.save(tagEntity);
            if (!saveResult.equalsWithoutId(tagEntity)) {
                throw new BlogException.Builder(BlogExceptionEnum.INSERT_DATA_FAILED, tagEntity.toString()).build();
            }
            return saveResult;
        } catch (Exception e) {
            if (e instanceof BlogException) {
                throw e;
            }
            throw new BlogException.Builder(BlogExceptionEnum.INSERT_DATA_FAILED, tagEntity.toString()).build();
        }
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public void removeTag(TagEntity tagEntity) throws BlogException {
        tagRepository.delete(tagEntity);
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public TagEntity updateTag(TagEntity tagEntity) throws BlogException {
        TagEntity saveResult = tagRepository.save(tagEntity);
        if (!saveResult.equals(tagEntity)) {
            throw new BlogException.Builder(BlogExceptionEnum.UPDATE_DATA_FAILED, tagEntity.toString()).build();
        }
        return saveResult;
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public TagEntity findTagById(Integer id) throws BlogException {
        Optional<TagEntity> findResult = tagRepository.findById(id);
        if (findResult.isPresent()) {
            return findResult.get();
        } else {
            throw new BlogException.Builder(BlogExceptionEnum.TAG_NOT_FOUND, "id: " + id).build();
        }
    }

    @Override
    public List<TagEntity> findTagList(
            String name, String summary, UserEntity tagCreator,
            Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException {
        // 设置查询条件
        Specification<TagEntity> specification = new Specification<TagEntity>() {
            @Override
            public Predicate toPredicate(Root<TagEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>(16);
                if (tagCreator == null) {
                    throw new BlogException.Builder(BlogExceptionEnum.NEED_FIELD, "need user").build();
                }
                if (!tagCreator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                        && !tagCreator.getUserPrivilege().equals(UserPrivilege.ROOT_USER)) {
                    predicates.add(criteriaBuilder.equal(root.get("tagCreator").as(UserEntity.class),
                            tagCreator));
                }
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
            Page<TagEntity> res = sort != null ?
                    tagRepository.findAll(specification, PageRequest.of(page, size, sort)) :
                    tagRepository.findAll(specification, PageRequest.of(page, size));
            return res.getContent();
        } else {
            return tagRepository.findAll(specification);
        }
    }

}
