package cn.jinelei.rainbow.blog.entity.enumerate;

/**
 * @author zhenlei
 */

public enum CommentPrivilege {
    INVALID_VALUE(0, "invalid value"),
    ALLOW_MYSELF(1, "disallow all"),
    ALLOW_FRIEND(2, "allow friend"),
    ALLOW_ALL(4, "allow all");
    private int code;
    private String desc;

    CommentPrivilege(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static CommentPrivilege resolve(int code) {
        switch (code) {
            case 1:
                return CommentPrivilege.ALLOW_MYSELF;
            case 2:
                return CommentPrivilege.ALLOW_FRIEND;
            case 4:
                return CommentPrivilege.ALLOW_ALL;
            case 0:
            default:
                return CommentPrivilege.INVALID_VALUE;
        }
    }
}
