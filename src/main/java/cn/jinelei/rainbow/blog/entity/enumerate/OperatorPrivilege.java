package cn.jinelei.rainbow.blog.entity.enumerate;

import cn.jinelei.rainbow.blog.entity.enumerate.convert.OperatorPrivilegeConvert;
import cn.jinelei.rainbow.blog.entity.enumerate.convert.UserPrivilegeConvert;

import javax.persistence.Convert;

/**
 * @author zhenlei
 */

public enum OperatorPrivilege {
    INVALID_VALUE(0, "invalid value"),
    ONLY_MYSELF(1, "only myself"),
    ONLY_ROOT(2, "only root");
    private int code;
    private String desc;

    OperatorPrivilege(int code, String desc) {
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

    public static OperatorPrivilege resolve(int code) {
        switch (code) {
            case 1:
                return OperatorPrivilege.ONLY_MYSELF;
            case 2:
                return OperatorPrivilege.ONLY_ROOT;
            case 0:
            default:
                return OperatorPrivilege.INVALID_VALUE;
        }
    }
}
