package cn.jinelei.rainbow.blog.entity.enumerate;

/**
 * @author zhenlei
 */

public enum BrowsePrivilege {
    INVALID_VALUE(0, "invalid value"),
    ALLOW_MYSELF(1, "disallow all"),
    ALLOW_FRIEND(4, "allow friend"),
    ALLOW_ALL(2, "allow all");
    private int code;
    private String desc;

    BrowsePrivilege(int code, String desc) {
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

    public static BrowsePrivilege resolve(int code) {
        switch (code) {
            case 1:
                return BrowsePrivilege.ALLOW_MYSELF;
            case 2:
                return BrowsePrivilege.ALLOW_FRIEND;
            case 4:
                return BrowsePrivilege.ALLOW_ALL;
            case 0:
            default:
                return BrowsePrivilege.INVALID_VALUE;
        }
    }
}
