package cn.jinelei.rainbow.blog.entity.enumerate.convert;

import cn.jinelei.rainbow.blog.entity.enumerate.UserPrivilege;

import javax.persistence.AttributeConverter;
import java.text.AttributedCharacterIterator;

/**
 * @author zhenlei
 */
public class UserPrivilegeConvert implements AttributeConverter<UserPrivilege, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserPrivilege attribute) {
        return attribute.getCode();
    }

    @Override
    public UserPrivilege convertToEntityAttribute(Integer dbData) {
        return UserPrivilege.resolve(dbData);
    }
}
