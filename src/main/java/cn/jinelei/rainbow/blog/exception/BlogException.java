package cn.jinelei.rainbow.blog.exception;

import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Field;

/**
 * @author zhenlei
 */
@JsonIgnoreProperties(value = {"cause", "stackTrace", "exceptionEnum", "suppressed", "localizedMessage"})
public class BlogException extends Exception {
    BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.UNKNOWN_ERROR;

    int code;
    String message;

    public int getCode() {
        try {
            Field field = this.getClass().getDeclaredField("blogExceptionEnum");
            field.setAccessible(true);
            BlogExceptionEnum e = ((BlogExceptionEnum) field.get(this));
            return e.getCode();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return this.blogExceptionEnum.getCode();
        }
    }

    @Override
    public String getMessage() {
        try {
            Field field = this.getClass().getDeclaredField("blogExceptionEnum");
            field.setAccessible(true);
            BlogExceptionEnum e = ((BlogExceptionEnum) field.get(this));
            return e.getMessage();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return this.blogExceptionEnum.getMessage();
        }
    }

    public BlogExceptionEnum getExceptionEnum() {
        try {
            Field field = this.getClass().getDeclaredField("blogExceptionEnum");
            field.setAccessible(true);
            return ((BlogExceptionEnum) field.get(this));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return this.blogExceptionEnum;
        }
    }

    public static class InsertDataError extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.INSERT_DATA_ERROR;
    }

    public static class UpdateDataError extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.UPDATE_DATA_ERROR;
    }

    public static class DeleteDataError extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_DATA_ERROR;
    }

    public static class QueryDataError extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.QUERY_DATA_ERROR;
    }

    public static class UserNotLogin extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.USER_NOT_LOGIN;
    }

    public static class EmailAlreadyExist extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.EMAIL_ALREADY_EXIST;
    }

    public static class PhoneAlreadyExist extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.PHONE_ALREADY_EXIST;
    }

    public static class UsernameNotUnique extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.USERNAME_NOT_UNIQUE;
    }

    public static class UnAuthorized extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.UNAUTHORIZED;
    }

    public static class UserNotFound extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.USER_NOT_FOUND;
    }

    public static class UsernameOrPasswordInvalid extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.USERNAME_OR_PASSWORD_INVALID;
    }

    public static class NeedField extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.NEED_FIELD;
    }

    public static class UnAuthorizedUser extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.UNAUTHORIZED_USER;
    }

    public static class UnAuthorizedGroup extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.UNAUTHORIZED_GROUP;
    }

}
