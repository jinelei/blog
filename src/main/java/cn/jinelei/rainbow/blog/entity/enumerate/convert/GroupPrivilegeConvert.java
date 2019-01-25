package cn.jinelei.rainbow.blog.entity.enumerate.convert;

import cn.jinelei.rainbow.blog.entity.enumerate.BrowsePrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;

import javax.persistence.AttributeConverter;

/**
 * @author zhenlei
 */
public class GroupPrivilegeConvert implements AttributeConverter<GroupPrivilege, Integer> {
    @Override
    public Integer convertToDatabaseColumn(GroupPrivilege attribute) {
        return attribute.getCode();
    }

    @Override
    public GroupPrivilege convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return GroupPrivilege.INVALID_VALUE;
        }
        return GroupPrivilege.resolve(dbData);
    }
}
