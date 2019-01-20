package cn.jinelei.rainbow.blog.entity.enumerate;

import cn.jinelei.rainbow.blog.entity.enumerate.convert.UserPrivilegeConvert;

import javax.persistence.Convert;

/**
 * @author zhenlei
 */

public enum UserPrivilege {
    INVALID_VALUE(0, "invalid value"),
    TOURIST_USER(1, "tourist user"),
    NORMAL_USER(2, "normal user"),
    ROOT_USER(4, "root user");
    private int code;
    private String desc;

    UserPrivilege(int code, String desc) {
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

    public static class Constants {
        public static final int TOURIST_USER = 1;
        public static final int NORMAL_USER = 2;
        public static final int ROOT_USER = 4;
    }

    public static UserPrivilege resolve(int code) {
        switch (code) {
            case 1:
                return UserPrivilege.TOURIST_USER;
            case 2:
                return UserPrivilege.NORMAL_USER;
            case 4:
                return UserPrivilege.ROOT_USER;
            case 0:
            default:
                return UserPrivilege.INVALID_VALUE;
        }
    }
}
