package cn.jinelei.rainbow.blog.authorization.annotation;


import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.OperatorPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.UserPrivilege;
import org.hibernate.usertype.UserType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在Controller的方法上使用此注解，该方法在映射时会检查用户是否登录，未登录返回401错误
 *
 * @author zhenlei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorization {

    AuthorizationCondition[] orConditions() default {};

    AuthorizationCondition[] andConditions() default {};


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface AuthorizationCondition {

        /**
         * 用户权限
         *
         * @return
         */
        OperatorPrivilege grantOperator() default OperatorPrivilege.INVALID_VALUE;

        /**
         * 组权限
         *
         * @return
         */
        GroupPrivilege grantGroup() default GroupPrivilege.INVALID_VALUE;

        /**
         * 参数名
         *
         * @return
         */
        String parameterName() default "";

    }
}

