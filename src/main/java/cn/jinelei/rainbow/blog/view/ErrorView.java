package cn.jinelei.rainbow.blog.view;

import cn.jinelei.rainbow.blog.exception.CustomizeException;

import java.io.Serializable;

/**
 * @author zhenlei
 */
public class ErrorView implements Serializable {
    private Integer code;
    private String message;

    public ErrorView(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorView(CustomizeException e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    @Override
    public String toString() {
        return "ErrorView{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
