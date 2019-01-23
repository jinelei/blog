package cn.jinelei.rainbow.blog.entity.enumerate.convert;

import cn.jinelei.rainbow.blog.entity.enumerate.BrowsePrivilege;

import javax.persistence.AttributeConverter;

/**
 * @author zhenlei
 */
public class BrowsePrivilegeConvert implements AttributeConverter<BrowsePrivilege, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BrowsePrivilege attribute) {
        return attribute.getCode();
    }

    @Override
    public BrowsePrivilege convertToEntityAttribute(Integer dbData) {
        return BrowsePrivilege.resolve(dbData);
    }
}
