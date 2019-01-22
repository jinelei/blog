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
     * 保留错误码
     */
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
