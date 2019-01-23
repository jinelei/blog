package cn.jinelei.rainbow.blog.entity.enumerate.convert;

import cn.jinelei.rainbow.blog.entity.enumerate.CommentPrivilege;

import javax.persistence.AttributeConverter;

/**
 * @author zhenlei
 */
public class CommentPrivilegeConvert implements AttributeConverter<CommentPrivilege, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CommentPrivilege attribute) {
        return attribute.getCode();
    }

    @Override
    public CommentPrivilege convertToEntityAttribute(Integer dbData) {
        return CommentPrivilege.resolve(dbData);
    }
}
