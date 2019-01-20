package cn.jinelei.rainbow.blog.exception;

import cn.jinelei.rainbow.blog.exception.enumerate.*;

/**
 * @author zhenlei
 */
public class CustomizeException extends Exception {
    private Integer code;
    private String message;

    public CustomizeException(ArticleExceptionEnum e) {
        code = e.getCode();
        message = e.getMessage();
    }

    public CustomizeException(BaseExceptionEnum e) {
        code = e.getCode();
        message = e.getMessage();
    }

    public CustomizeException(CategoryExceptionEnum e) {
        code = e.getCode();
        message = e.getMessage();
    }

    public CustomizeException(CommentExceptionEnum e) {
        code = e.getCode();
        message = e.getMessage();
    }

    public CustomizeException(TagExceptionEnum e) {
        code = e.getCode();
        message = e.getMessage();
    }

    public CustomizeException(UserExceptionEnum e) {
        code = e.getCode();
        message = e.getMessage();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.format("{code: %d, message: '%s'}", code, message);
    }
}
