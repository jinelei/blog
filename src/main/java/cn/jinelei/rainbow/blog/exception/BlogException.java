package cn.jinelei.rainbow.blog.exception;

import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

import static cn.jinelei.rainbow.blog.constant.Constants.PARAM_PLACEHOLDER;

/**
 * @author zhenlei
 */
@JsonIgnoreProperties(value = {"cause", "stackTrace", "exceptionEnum", "suppressed", "localizedMessage"})
public class BlogException extends RuntimeException {
    protected Integer status;
    protected String reason;
    protected Integer code;
    protected String message;
    protected Instant timestamp;

    private BlogException(Integer status, String reason, Integer code, String message) {
        this.status = status;
        this.reason = reason;
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "status=" + status +
                ", reason='" + reason + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static class ArticleException extends BlogException {
        public ArticleException(Integer status, String reason, Integer code, String message) {
            super(status, reason, code, message);
        }
    }

    public static class UserException extends BlogException {
        public UserException(Integer status, String reason, Integer code, String message) {
            super(status, reason, code, message);
        }
    }

    public static class TagException extends BlogException {
        public TagException(Integer status, String reason, Integer code, String message) {
            super(status, reason, code, message);
        }
    }

    public static class CategoryException extends BlogException {
        public CategoryException(Integer status, String reason, Integer code, String message) {
            super(status, reason, code, message);
        }
    }

    public static class CommentException extends BlogException {
        public CommentException(Integer status, String reason, Integer code, String message) {
            super(status, reason, code, message);
        }
    }

    public static class Builder {
        private Integer status;
        private String reason;
        private Integer code;
        private String message;
        private Instant timestamp;


        public Builder(BlogExceptionEnum exceptionEnum) {
            this.status = exceptionEnum.getStatus();
            this.reason = exceptionEnum.getReason();
            this.code = exceptionEnum.getCode();
            this.message = exceptionEnum.getReason();
        }

        public Builder(BlogExceptionEnum exceptionEnum, String messages) {
            this.status = exceptionEnum.getStatus();
            this.reason = exceptionEnum.getReason();
            this.code = exceptionEnum.getCode();
            this.message = messages;
        }

        public Builder(BlogExceptionEnum exceptionEnum, Object[] messages) {
            this.status = exceptionEnum.getStatus();
            this.reason = exceptionEnum.getReason();
            this.code = exceptionEnum.getCode();
            String[] res = exceptionEnum.getMessage().split(PARAM_PLACEHOLDER);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < res.length; i++) {
                sb.append(res[i]);
                if (messages[i] != null) {
                    sb.append(String.valueOf(messages[i]));
                }
            }
            this.message = sb.toString();
        }

        public Builder() {
        }

        public Builder setExceptionEnum(BlogExceptionEnum exceptionEnum, Object[] messages) {
            this.status = exceptionEnum.getStatus();
            this.reason = exceptionEnum.getReason();
            this.code = exceptionEnum.getCode();
            String[] res = exceptionEnum.getMessage().split(PARAM_PLACEHOLDER);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < res.length; i++) {
                sb.append(res[i]);
                if (messages[i] != null) {
                    sb.append(String.valueOf(messages[i]));
                }
            }
            this.message = sb.toString();
            return this;
        }

        public Builder setExceptionEnum(BlogExceptionEnum exceptionEnum) {
            this.status = exceptionEnum.getStatus();
            this.reason = exceptionEnum.getReason();
            this.code = exceptionEnum.getCode();
            this.message = exceptionEnum.getMessage();
            return this;
        }

        public Builder setStatus(Integer status) {
            this.status = status != null ? status : null;
            return this;
        }

        public Builder setReason(String reason) {
            this.reason = reason != null ? reason : null;
            return this;
        }

        public Builder setCode(Integer code) {
            this.code = code != null ? code : null;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message != null ? message : null;
            return this;
        }

        public Builder setTimestamp(Instant timestamp) {
            this.timestamp = timestamp != null ? timestamp : null;
            return this;
        }

        public BlogException build() {
            if (status != null && reason != null && code != null && message != null) {
                if (code >= BlogExceptionEnum.BASE_ERROR.getCode()) {
                    return new BlogException(status, reason, code, message);
                } else if (code >= BlogExceptionEnum.USER_ERROR.getCode()) {
                    return new UserException(status, reason, code, message);
                } else if (code >= BlogExceptionEnum.ARTICLE_ERROR.getCode()) {
                    return new ArticleException(status, reason, code, message);
                } else if (code >= BlogExceptionEnum.CATEGORY_ERROR.getCode()) {
                    return new CategoryException(status, reason, code, message);
                } else if (code >= BlogExceptionEnum.COMMENT_ERROR.getCode()) {
                    return new CommentException(status, reason, code, message);
                } else if (code >= BlogExceptionEnum.TAG_ERROR.getCode()) {
                    return new TagException(status, reason, code, message);
                } else {
                    return new BlogException(status, reason, code, message);
                }
            } else {
                return null;
            }
        }

    }
}
