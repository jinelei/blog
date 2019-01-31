package cn.jinelei.rainbow.blog;

import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.constant.ConstantsCamelCase;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest
public class BlogApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
        UserEntity userEntity = userRepository.findById(8).get();
        userEntity.setEmail("asdfasdfasdfasdf");
        long res = userRepository.count((Root<UserEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(Constants.USERNAME).as(String.class), userEntity.getUsername()),
                        criteriaBuilder.notEqual(root.get(ConstantsCamelCase.USER_ID).as(Integer.class), userEntity.getUserId())
                )
        );
        System.out.println(res);
    }

}

