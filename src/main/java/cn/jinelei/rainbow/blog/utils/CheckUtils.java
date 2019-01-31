package cn.jinelei.rainbow.blog.utils;

import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;

/**
 * @author zhenlei
 */
public class CheckUtils {
    public static String EMAIL_REGEX = "[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+";
    public static String PHONE_REGEX = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

    public static void checkOwnerAndGroup(UserEntity operator, Integer userId) {
        if (!operator.getGroupPrivilege().equals(GroupPrivilege.ROOT_GROUP)
                && !operator.getUserId().equals(userId)) {
            throw new BlogException.Builder(BlogExceptionEnum.UNAUTHORIZED, operator.toString()).build();
        }
    }

    public static boolean checkEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public static boolean checkPhone(String phone) {
        return phone.matches(PHONE_REGEX);
    }

}
