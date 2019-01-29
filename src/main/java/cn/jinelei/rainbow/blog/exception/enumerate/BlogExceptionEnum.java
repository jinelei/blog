package cn.jinelei.rainbow.blog.exception.enumerate;

/**
 * @author zhenlei
 */

public enum BlogExceptionEnum {
    /**
     * 基础错误码
     */
    INSERT_DATA_ERROR(900001, "insert data error"),
    UPDATE_DATA_ERROR(900002, "update data error"),
    DELETE_DATA_ERROR(900004, "remove data error"),
    QUERY_DATA_ERROR(900008, "query data error"),
    TOKEN_NOT_EFFECTIVE(900016, "token not effective"),
    TOKEN_IS_EXPIRED(900032, "token is expired"),
    /**
     * 用户错误码
     */
    USER_NOT_LOGIN(800000, "user not login"),
    EMAIL_ALREADY_EXIST(800001, "email already exist"),
    PHONE_ALREADY_EXIST(800002, "phone already exist"),
    USERNAME_NOT_UNIQUE(800004, "username not unique"),
    UNAUTHORIZED(800008, "unauthorized"),
    USER_NOT_FOUND(800016, "user not found"),
    USERNAME_OR_PASSWORD_INVALID(800032, "username or password invalid"),
    NEED_FIELD(800064, "need field"),
    UNAUTHORIZED_USER(800128, "unauthorized user"),
    UNAUTHORIZED_GROUP(800256, "unauthorized group"),
    USER_LOGIN_SUCCESS(800512, "user login success"),
    USER_LOGIN_FAILED(801024, "user login failed"),
    USER_LOGOUT_SUCCESS(802048, "user logout success"),
    USER_LOGOUT_FAILED(804096, "user logout failed"),
    DELETE_USER_SUCCESS(808192, "delete user success"),
    DELETE_USER_FAILED(816384, "delete user failed"),
    /**
     * 标签错误码
     */
    TAG_NOT_FOUND(700000, "tag not found"),
    DELETE_TAG_SUCCESS(700001, "delete tag success"),
    DELETE_TAG_FAILED(700002, "delete tag failed"),
    TAG_ALREADY_EXIST(700004, "tag already exist"),
    /**
     * 分类错误码
     */
    CATEGORY_NOT_FOUND(600000, "category not found"),
    DELETE_CATEGORY_SUCCESS(600001, "delete category success"),
    DELETE_CATEGORY_FAILED(600002, "delete category failed"),
    CATEGORY_ALREADY_EXIST(600004, "category already exist"),
    /**
     * 评论错误码
     */
    COMMENT_NOT_FOUND(500000, "comment not found"),
    DELETE_COMMENT_SUCCESS(500001, "delete comment success"),
    DELETE_COMMENT_FAILED(500002, "delete comment failed"),
    COMMENT_ALREADY_EXIST(500004, "comment already exist"),
    /**
     * 文章错误码
     */
    ARTICLE_NOT_FOUND(400000, "article not found"),
    DELETE_ARTICLE_SUCCESS(400001, "delete article success"),
    DELETE_ARTICLE_FAILED(400002, "delete article failed"),
    ARTICLE_ALREADY_EXIST(400004, "article already exist"),
    /**
     * 保留错误码
     */
    UPLOAD_IMAGE_FAILED(999997, "upload image failed"),
    EMPTY_IMAGE(999998, "empty image"),
    UNKNOWN_ERROR(999999, "unknown error");
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("{\"code\":\"%d\",\"message\":\"%s\"}", code, message);
    }

    BlogExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
