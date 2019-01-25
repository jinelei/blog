package cn.jinelei.rainbow.blog.entity.enumerate.convert;

import cn.jinelei.rainbow.blog.entity.enumerate.BrowsePrivilege;
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
        if (dbData == null) {
            return CommentPrivilege.INVALID_VALUE;
        }
        return CommentPrivilege.resolve(dbData);
    }
}
