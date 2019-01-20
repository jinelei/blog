package cn.jinelei.rainbow.blog.exception.enumerate;

/**
 * @author zhenlei
 */

public enum UserExceptionEnum {
    /**
     * 插入失败
     */
    INSERT_DATA_ERROR(900001, "insert data error"),
    UPDATE_DATA_ERROR(900002, "update data error"),
    REMOVE_DATA_ERROR(900004, "remove data error"),
    QUERY_DATA_ERROR(900008, "query data error"),
    USER_NOT_FOUND(900016, "user not found"),
    USERNAME_OR_PASSWORD_INVAILD(9000032, "username or password invaild"),
    NEED_FIELD(9000064, "need field"),
    UNAUTHORIZED_USER(9000128, "unauthorized user"),
    UNAUTHORIZED_GROUP(9000256, "unauthorized group"),
    USER_NOT_LOGIN(9000512, "user not login"),
    EMAIL_ALREADY_EXIST(9001024, "email already exist"),
    PHONE_ALREADY_EXIST(9002048, "phone already exist"),
    USERNAME_NOT_UNIQUE(9004096, "username not unique"),
    UNKNOW_ERROR(999999, "unknow error");
    private Integer code;
    private String message;

    UserExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
