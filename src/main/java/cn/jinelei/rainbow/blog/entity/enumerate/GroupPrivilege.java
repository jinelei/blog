package cn.jinelei.rainbow.blog.entity.enumerate;

import cn.jinelei.rainbow.blog.entity.enumerate.convert.GroupPrivilegeConvert;
import cn.jinelei.rainbow.blog.entity.enumerate.convert.UserPrivilegeConvert;

import javax.persistence.Convert;

/**
 * @author zhenlei
 */

public enum GroupPrivilege {
    INVALID_VALUE(0, "invalid value"),
    TOURIST_GROUP(1, "tourist group"),
    NORMAL_GROUP(2, "normal group"),
    ROOT_GROUP(4, "root group");
    private int code;
    private String desc;

    GroupPrivilege(int code, String desc) {
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
        public static final int TOURIST_GROUP = 1;
        public static final int NORMAL_GROUP = 2;
        public static final int ROOT_GROUP = 4;
    }

    public static GroupPrivilege resolve(int code) {
        switch (code) {
            case 1:
                return GroupPrivilege.TOURIST_GROUP;
            case 2:
                return GroupPrivilege.NORMAL_GROUP;
            case 4:
                return GroupPrivilege.ROOT_GROUP;
            case 0:
            default:
                return GroupPrivilege.INVALID_VALUE;
        }
    }
}
