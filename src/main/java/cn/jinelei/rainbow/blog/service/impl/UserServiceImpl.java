package cn.jinelei.rainbow.blog.service.impl;

import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import cn.jinelei.rainbow.blog.repository.UserRepository;
import cn.jinelei.rainbow.blog.service.UserService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhenlei
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public UserEntity addUser(UserEntity userEntity) throws BlogException {
        UserEntity saveResult = userRepository.save(userEntity);
        if (!saveResult.equalsWithId(userEntity)) {
            throw new BlogException.Builder(BlogExceptionEnum.INSERT_DATA_FAILED, userEntity.toString()).build();
        }
        return saveResult;
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public void removeUser(UserEntity userEntity) throws BlogException {
        userRepository.delete(userEntity);
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public UserEntity updateUser(UserEntity userEntity) throws BlogException {
        UserEntity saveResult = userRepository.save(userEntity);
        if (!saveResult.equalsWithId(userEntity)) {
            throw new BlogException.Builder(BlogExceptionEnum.UPDATE_DATA_FAILED, userEntity.toString()).build();
        }
        return saveResult;
    }

    @Override
    @Transactional(rollbackFor = {BlogException.class, Exception.class})
    public UserEntity findUserById(Integer id) throws BlogException {
        Optional<UserEntity> findResult = userRepository.findById(id);
        if (findResult.isPresent()) {
            return findResult.get();
        } else {
            throw new BlogException.Builder(BlogExceptionEnum.DATA_NOT_FOUND, "id: " + id).build();
        }
    }

    @Override
    public UserEntity validUserByUsernameAndPassword(String username, String password) throws BlogException {
        List<UserEntity> userEntities = userRepository.findUserEntitiesByUsernameAndPassword(username, password);
        if (userEntities.size() == 1) {
            return userEntities.get(0);
        } else if (userEntities.size() > 1) {
            throw new BlogException.Builder(BlogExceptionEnum.UNKNOWN_ERROR, "user not unique").build();
        } else {
            throw new BlogException.Builder(BlogExceptionEnum.USERNAME_OR_PASSWORD_WRONG, Constants.USERNAME_OR_PASSWORD_WRONG_STR).build();
        }
    }

    @Override
    public List<UserEntity> findUserList(String username, String nickname, String phone, String city, String province, String email, Integer page, Integer size, String[] descFilters, String[] ascFilters) throws BlogException {
        // 设置查询条件
        Specification<UserEntity> specification = new Specification<UserEntity>() {
            @Override
            public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>(16);
                if (!StringUtils.isEmpty(username)) {
                    predicates.add(criteriaBuilder.like(root.get(Constants.USERNAME).as(String.class),
                            String.format("%%%s%%", username)));
                }
                if (!StringUtils.isEmpty(nickname)) {
                    predicates.add(criteriaBuilder.like(root.get(Constants.NICKNAME).as(String.class),
                            String.format("%%%s%%", nickname)));
                }
                if (!StringUtils.isEmpty(province)) {
                    predicates.add(criteriaBuilder.like(root.get(Constants.PROVINCE).as(String.class),
                            String.format("%%%s%%", province)));
                }
                if (!StringUtils.isEmpty(city)) {
                    predicates.add(criteriaBuilder.like(root.get(Constants.CITY).as(String.class),
                            String.format("%%%s%%", city)));
                }
                if (!StringUtils.isEmpty(phone)) {
                    predicates.add(criteriaBuilder.like(root.get(Constants.PHONE).as(String.class),
                            String.format("%%%s", phone.replace("^\\+\\d\\d", ""))));
                }
                if (!StringUtils.isEmpty(email)) {
                    predicates.add(criteriaBuilder.like(root.get(Constants.EMAIL).as(String.class),
                            email));
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
            Page<UserEntity> res = sort != null ?
                    userRepository.findAll(specification, PageRequest.of(page, size, sort)) :
                    userRepository.findAll(specification, PageRequest.of(page, size));
            return res.getContent();
        } else {
            return userRepository.findAll(specification);
        }
    }

}
