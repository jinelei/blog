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
    String describe;

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

    public String getDescribe() {
        return describe;
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

    public BlogException(String describe) {
        super();
        this.describe = describe;
    }

    public BlogException() {
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

    public static class UserLoginSuccess extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.USER_LOGIN_SUCCESS;
    }

    public static class UserLoginFailed extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.USER_LOGIN_FAILED;
    }

    public static class UserLogoutSuccess extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.USER_LOGOUT_SUCCESS;
    }

    public static class UserLogoutFailed extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.USER_LOGOUT_FAILED;
    }

    public static class TokenIsExpired extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.TOKEN_IS_EXPIRED;
    }

    public static class TokenNotEffective extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.TOKEN_NOT_EFFECTIVE;
    }

    public static class DeleteUserSuccess extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_USER_SUCCESS;
    }

    public static class DeleteUserFailed extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_USER_FAILED;
    }

    public static class TagNotFound extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.TAG_NOT_FOUND;
    }

    public static class DeleteTagSuccess extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_TAG_SUCCESS;
    }

    public static class DeleteTagFailed extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_TAG_FAILED;
    }

    public static class TagAlreadyExist extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.TAG_ALREADY_EXIST;
    }

    public static class CategoryNotFound extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.CATEGORY_NOT_FOUND;
    }

    public static class DeleteCategorySuccess extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_CATEGORY_SUCCESS;
    }

    public static class DeleteCategoryFailed extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_CATEGORY_FAILED;
    }

    public static class CategoryAlreadyExist extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.CATEGORY_ALREADY_EXIST;
    }

    public static class CommentNotFound extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.COMMENT_NOT_FOUND;
    }

    public static class DeleteCommentSuccess extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_COMMENT_SUCCESS;
    }

    public static class DeleteCommentFailed extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_COMMENT_FAILED;
    }

    public static class CommentAlreadyExist extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.COMMENT_ALREADY_EXIST;
    }

    public static class ArticleNotFound extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.ARTICLE_NOT_FOUND;
    }

    public static class DeleteArticleSuccess extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_ARTICLE_SUCCESS;
    }

    public static class DeleteArticleFailed extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.DELETE_ARTICLE_FAILED;
    }

    public static class ArticleAlreadyExist extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.ARTICLE_ALREADY_EXIST;
    }

    public static class EmptyImage extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.EMPTY_IMAGE;
    }

    public static class UploadImageFailed extends BlogException {
        BlogExceptionEnum blogExceptionEnum = BlogExceptionEnum.UPLOAD_IMAGE_FAILED;
    }

}
