package cn.jinelei.rainbow.blog.exception.enumerate;

/**
 * @author zhenlei
 */

public enum BaseExceptionEnum {
    INSERT_DATA_ERROR(900001, "insert data error"),
    UPDATE_DATA_ERROR(900002, "update data error"),
    REMOVE_DATA_ERROR(900004, "remove data error"),
    QUERY_DATA_ERROR(900008, "query data error"),
    UNKNOW_ERROR(999999, "unknow error");
    private Integer code;
    private String message;

    BaseExceptionEnum(Integer code, String message) {
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
