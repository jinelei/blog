package cn.jinelei.rainbow.blog.entity.enumerate.convert;

import cn.jinelei.rainbow.blog.entity.enumerate.OperatorPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.UserPrivilege;

import javax.persistence.AttributeConverter;

/**
 * @author zhenlei
 */
public class OperatorPrivilegeConvert implements AttributeConverter<OperatorPrivilege, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OperatorPrivilege attribute) {
        return attribute.getCode();
    }

    @Override
    public OperatorPrivilege convertToEntityAttribute(Integer dbData) {
        return OperatorPrivilege.resolve(dbData);
    }
}
